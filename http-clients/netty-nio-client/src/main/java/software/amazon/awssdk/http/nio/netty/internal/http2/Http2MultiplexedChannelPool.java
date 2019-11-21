/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.http.nio.netty.internal.http2;

import static software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils.doInEventLoop;
import static software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils.warnIfNotInEventLoop;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http2.Http2GoAwayFrame;
import io.netty.handler.codec.http2.Http2StreamChannelBootstrap;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseAggregator;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.concurrent.SucceededFuture;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.utils.BetterFixedChannelPool;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

/**
 * {@link ChannelPool} implementation that handles multiplexed streams. Child channels are created
 * for each HTTP/2 stream using {@link Http2StreamChannelBootstrap} with the parent channel being
 * the actual socket channel. This implementation assumes that all connections have the same setting
 * for MAX_CONCURRENT_STREAMS. Concurrent requests are load balanced across all available connections,
 * when the max concurrency for a connection is reached then a new connection will be opened.
 *
 * <p>
 * <b>Note:</b> This enforces no max concurrency. Relies on being wrapped with a {@link BetterFixedChannelPool}
 * to enforce max concurrency which gives a bunch of other good features like timeouts, max pending acquires, etc.
 * </p>
 */
@SdkInternalApi
public class Http2MultiplexedChannelPool implements ChannelPool {
    /**
     * Reference to the {@link MultiplexedChannelRecord} on a channel.
     */
    public static final AttributeKey<MultiplexedChannelRecord> MULTIPLEXED_CHANNEL = AttributeKey.newInstance(
        "aws.http.nio.netty.async.channelPoolRecord");

    private static final Logger log = Logger.loggerFor(Http2MultiplexedChannelPool.class);
    private final EventLoop eventLoop;
    private final ChannelPool connectionPool;
    private final ArrayList<MultiplexedChannelRecord> connections;
    private boolean closed = false;

    /**
     * @param connectionPool Connection pool for parent channels (i.e. the socket channel).
     * @param eventLoop Event loop to run all tasks in.
     */
    Http2MultiplexedChannelPool(ChannelPool connectionPool,
                                EventLoop eventLoop) {
        this.connectionPool = connectionPool;
        this.eventLoop = eventLoop;
        // Customers that want an unbounded connection pool may set max concurrency to something like
        // Long.MAX_VALUE so we just stick with the initial ArrayList capacity and grow from there.
        this.connections = new ArrayList<>();
    }

    @SdkTestInternalApi
    Http2MultiplexedChannelPool(ChannelPool connectionPool,
                                EventLoop eventLoop,
                                Collection<MultiplexedChannelRecord> connections) {
        this.connectionPool = connectionPool;
        this.eventLoop = eventLoop;
        this.connections = new ArrayList<>(connections);
    }

    @Override
    public Future<Channel> acquire() {
        return acquire(new DefaultPromise<>(eventLoop));
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> promise) {
        doInEventLoop(eventLoop, () -> acquire0(promise), promise);
        return promise;
    }

    private void acquire0(Promise<Channel> promise) {
        warnIfNotInEventLoop(eventLoop);

        if (closed) {
            promise.setFailure(new IOException("Channel pool is closed!"));
            return;
        }

        for (MultiplexedChannelRecord multiplexedChannel : connections) {
            if (multiplexedChannel.numAvailableStreams() > 0) {
                acquireChildStream(multiplexedChannel, promise);
                return;
            }
        }

        // No available streams on existing connections, establish new connection and add it to list
        acquireStreamOnNewConnection(promise);
    }

    private void acquireStreamOnNewConnection(Promise<Channel> promise) {
        warnIfNotInEventLoop(eventLoop);

        Future<Channel> newConnectionAcquire = connectionPool.acquire();

        newConnectionAcquire.addListener(f -> {
            if (!newConnectionAcquire.isSuccess()) {
                promise.setFailure(newConnectionAcquire.cause());
                return;
            }

            Channel parentChannel = newConnectionAcquire.getNow();
            parentChannel.attr(ChannelAttributeKey.HTTP2_MULTIPLEXED_CHANNEL_POOL).set(this);

            // When the protocol future is completed on the new connection, we're ready for new streams to be added to it.
            parentChannel.attr(ChannelAttributeKey.PROTOCOL_FUTURE).get().thenAccept(protocol -> {
                doInEventLoop(eventLoop, () -> {
                    if (closed) {
                        // This connection pool was closed while waiting for the future to complete. Give up the connection
                        // we just finished creating.
                        closeAndReleaseParentChannel(parentChannel);
                        return;
                    }

                    Long maxStreams = parentChannel.attr(ChannelAttributeKey.MAX_CONCURRENT_STREAMS).get();

                    Validate.isTrue(protocol == Protocol.HTTP2,
                                    "Protocol negotiated on connection (%s) was expected to be HTTP/2, but it "
                                    + "was %s.", parentChannel, Protocol.HTTP1_1);
                    Validate.isTrue(maxStreams != null,
                                    "HTTP/2 was negotiated on the connection (%s), but the maximum number of "
                                    + "streams was not initialized.", parentChannel);
                    Validate.isTrue(maxStreams > 0, "Maximum streams were negative on channel (%s).", parentChannel);

                    MultiplexedChannelRecord multiplexedChannel = new MultiplexedChannelRecord(parentChannel, maxStreams);
                    parentChannel.attr(MULTIPLEXED_CHANNEL).set(multiplexedChannel);

                    // Before we cache the connection, make sure that exceptions on the connection will remove it from the cache.
                    parentChannel.pipeline().addLast(new ReleaseOnExceptionHandler());
                    connections.add(multiplexedChannel);

                    acquireChildStream(multiplexedChannel, promise);
                }, promise);
            })
            .exceptionally(exception -> {
                promise.setFailure(exception);
                return null;
            });
        });
    }

    private void acquireChildStream(MultiplexedChannelRecord channelRecord, Promise<Channel> promise) {
        Future<Channel> acquireFuture = channelRecord.acquireChildChannel();
        acquireFuture.addListener(f -> {
            try {
                if (!acquireFuture.isSuccess()) {
                    promise.setFailure(acquireFuture.cause());
                    return;
                }

                Channel channel = acquireFuture.getNow();
                channel.attr(ChannelAttributeKey.HTTP2_MULTIPLEXED_CHANNEL_POOL).set(this);
                channel.attr(MULTIPLEXED_CHANNEL).set(channelRecord);
                promise.setSuccess(channel);
            } catch (Exception e) {
                promise.setFailure(e);
            }
        });
    }

    @Override
    public Future<Void> release(Channel childChannel) {
        return release(childChannel, new DefaultPromise<>(eventLoop));
    }

    @Override
    public Future<Void> release(Channel childChannel, Promise<Void> promise) {
        doInEventLoop(eventLoop, () -> releaseChildChannel0(childChannel, promise), promise);
        return promise;
    }

    private void releaseChildChannel0(Channel childChannel, Promise<Void> promise) {
        warnIfNotInEventLoop(eventLoop);

        if (childChannel.parent() == null) {
            // This isn't a child channel. Notify it that something is wrong.
            Exception exception = new IOException("Channel (" + childChannel + ") is not a child channel.");
            childChannel.pipeline().fireExceptionCaught(exception);
            promise.setFailure(exception);
            return;
        }

        Channel parentChannel = childChannel.parent();
        MultiplexedChannelRecord multiplexedChannel = parentChannel.attr(MULTIPLEXED_CHANNEL).get();
        if (multiplexedChannel == null) {
            // This is a child channel, but there is no attached multiplexed channel, which there should be if it was from
            // this pool. Notify it that something is wrong.
            Exception exception =
                new IOException("Channel (" + childChannel + ") is not associated with any channel records.");
            childChannel.pipeline().fireExceptionCaught(exception);
            promise.setFailure(exception);
            return;
        }

        childChannel.close();
        multiplexedChannel.release(childChannel);

        if (multiplexedChannel.isClosing() && multiplexedChannel.numActiveChildChannels() == 0) {
            // We just closed the last stream in a connection that has reached the end of its life.
            closeAndReleaseParentChannel(parentChannel);
        }

        promise.setSuccess(null);
    }

    private Future<?> closeAndReleaseParentChannel(Channel parentChannel) {
        Promise<Void> resultPromise = new DefaultPromise<>(eventLoop);
        doInEventLoop(eventLoop, () -> closeAndReleaseParentChannel0(parentChannel, resultPromise), resultPromise);
        return resultPromise;
    }

    private Future<?> closeAndReleaseParentChannel0(Channel parentChannel, Promise<Void> resultPromise) {
        warnIfNotInEventLoop(eventLoop);

        if (parentChannel.parent() != null) {
            // This isn't a parent channel. Notify it that something is wrong.
            Exception exception = new IOException("Channel (" + parentChannel + ") is not a parent channel.");
            parentChannel.pipeline().fireExceptionCaught(exception);
            return new FailedFuture<>(eventLoop, new IOException(exception));
        }

        MultiplexedChannelRecord multiplexedChannel = parentChannel.attr(MULTIPLEXED_CHANNEL).get();

        // We may not have a multiplexed channel if the parent channel hasn't been fully initialized.
        if (multiplexedChannel != null) {
            connections.remove(multiplexedChannel);
        }

        parentChannel.close();
        connectionPool.release(parentChannel, resultPromise);
        return resultPromise;
    }

    public void handleGoAway(Channel parentChannel, Http2GoAwayFrame frame) {
        doInEventLoop(eventLoop, () -> handleGoAway0(parentChannel, frame));
    }

    private void handleGoAway0(Channel parentChannel, Http2GoAwayFrame frame) {
        warnIfNotInEventLoop(eventLoop);

        log.debug(() -> "Received GOAWAY on " + parentChannel + " with lastStreamId of " + frame.lastStreamId());
        try {
            MultiplexedChannelRecord multiplexedChannel = parentChannel.attr(MULTIPLEXED_CHANNEL).get();

            if (multiplexedChannel != null) {
                multiplexedChannel.handleGoAway(frame);
            } else {
                // If we don't have a multiplexed channel, the parent channel hasn't been fully initialized. Close it now.
                closeAndReleaseParentChannel(parentChannel);
            }
        } catch (Exception e) {
            log.error(() -> "Failed to handle GOAWAY frame on channel " + parentChannel, e);
        }
    }

    @Override
    public void close() {
        if (closed) {
            // Optimization: This is a non-volatile read, so no guarantees that the value is accurate.
            return;
        }

        Promise<?> closeCompletePromise = new DefaultPromise<>(eventLoop);
        doInEventLoop(eventLoop, () -> close0(closeCompletePromise), closeCompletePromise);

        try {
            if (!closeCompletePromise.await(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("Event loop didn't close after 10 seconds.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        Throwable exception = closeCompletePromise.cause();
        if (exception != null) {
            throw new RuntimeException("Failed to close channel pool.", exception);
        }
    }

    private void close0(Promise<?> closePromise) {
        warnIfNotInEventLoop(eventLoop);

        if (closed) {
            closePromise.setSuccess(null);
        }

        closed = true;
        Promise<Void> releaseAllChannelsPromise = new DefaultPromise<>(eventLoop);

        PromiseCombiner promiseCombiner = new PromiseCombiner(eventLoop);
        for (MultiplexedChannelRecord channel : connections) {
            promiseCombiner.add(closeAndReleaseParentChannel0(channel.getConnection(), new DefaultPromise<>(eventLoop)));
        }
        promiseCombiner.finish(releaseAllChannelsPromise);

        connections.clear();

        releaseAllChannelsPromise.addListener(f -> {
            connectionPool.close();
            closePromise.setSuccess(null);
        });
    }

    private final class ReleaseOnExceptionHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            closeAndReleaseParentChannel(ctx.channel());
            super.exceptionCaught(ctx, cause);
        }
    }
}
