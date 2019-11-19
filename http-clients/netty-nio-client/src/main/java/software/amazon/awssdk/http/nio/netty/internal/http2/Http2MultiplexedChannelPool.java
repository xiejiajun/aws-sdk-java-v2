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

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http2.Http2GoAwayFrame;
import io.netty.handler.codec.http2.Http2StreamChannelBootstrap;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.util.ArrayList;
import java.util.Collection;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.utils.BetterFixedChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;
import software.amazon.awssdk.utils.Logger;

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
    public static final AttributeKey<MultiplexedChannelRecord> CHANNEL_POOL_RECORD = AttributeKey.newInstance(
        "aws.http.nio.netty.async.channelPoolRecord");

    private static final Logger log = Logger.loggerFor(Http2MultiplexedChannelPool.class);
    private final EventLoop eventLoop;
    private final ChannelPool connectionPool;
    private final long maxConcurrencyPerConnection;
    private final ArrayList<MultiplexedChannelRecord> connections;
    private boolean closed = false;

    /**
     * @param connectionPool Connection pool for parent channels (i.e. the socket channel).
     * @param eventLoop Event loop to run all tasks in.
     * @param maxConcurrencyPerConnection Max concurrent streams per HTTP/2 connection.
     */
    Http2MultiplexedChannelPool(ChannelPool connectionPool,
                                EventLoop eventLoop,
                                long maxConcurrencyPerConnection) {
        this.connectionPool = connectionPool;
        this.eventLoop = eventLoop;
        this.maxConcurrencyPerConnection = maxConcurrencyPerConnection;
        // Customers that want an unbounded connection pool may set max concurrency to something like
        // Long.MAX_VALUE so we just stick with the initial ArrayList capacity and grow from there.
        this.connections = new ArrayList<>();
    }

    @SdkTestInternalApi
    Http2MultiplexedChannelPool(ChannelPool connectionPool,
                                EventLoop eventLoop,
                                long maxConcurrencyPerConnection,
                                Collection<MultiplexedChannelRecord> connections) {
        this.connectionPool = connectionPool;
        this.eventLoop = eventLoop;
        this.maxConcurrencyPerConnection = maxConcurrencyPerConnection;
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

    public Future<?> handleConnectionLevelError(Channel channel, Throwable t) {
        return handleConnectionLevelError(channel, t, new DefaultPromise<>(eventLoop));
    }

    public Future<?> handleConnectionLevelError(Channel channel, Throwable t, Promise<?> promise) {
        doInEventLoop(eventLoop, () -> handleConnectionLevelError0(channel, t, promise));
        return promise;
    }

    private Future<?> handleConnectionLevelError0(Channel channel, Throwable t, Promise<?> promise) {
        try {
            MultiplexedChannelRecord channelPoolRecord = channel.attr(CHANNEL_POOL_RECORD).get();

            if (channelPoolRecord != null) {
                channelPoolRecord.shutdownChildChannels(t);
                releaseParentChannel()
            } else {
                log.error(() -> "Received connection-level error on a channel (" + channel.id() + ") that is not managed by "
                                + "this type of channel pool. The channel and its parents will be shut down, to prevent "
                                + "connection leaks.");
                killChannelAndParents(channel);
            }
        } catch (Exception e) {
            promise.setFailure(e);
        }

        promise.setSuccess(null);
        return promise;
    }

    public Future<?> handleGoAway(Channel channel, Http2GoAwayFrame frame) {
        return handleGoAway(channel, frame, new DefaultPromise<>(eventLoop));
    }

    public Future<?> handleGoAway(Channel channel, Http2GoAwayFrame frame, Promise<?> promise) {
        doInEventLoop(eventLoop, () -> handleGoAway0(channel, frame, promise));
        return promise;
    }

    private Future<?> handleGoAway0(Channel channel, Http2GoAwayFrame frame, Promise<?> promise) {
        try {
            MultiplexedChannelRecord channelPoolRecord = channel.attr(CHANNEL_POOL_RECORD).get();

            if (channelPoolRecord != null) {
                channelPoolRecord.goAway(frame);
            } else {
                log.error(() -> "Received GOAWAY on a channel (" + channel.id() + ") that is not managed by this type of "
                                + "channel pool. The channel and its parents will be shut down, to prevent connection leaks.");
                killChannelAndParents(channel);
            }
        } catch (Exception e) {
            promise.setFailure(e);
        }

        promise.setSuccess(null);
        return promise;
    }

    private Future<Channel> acquire0(Promise<Channel> promise) {
        if (closed) {
            return promise.setFailure(new IllegalStateException("Channel pool is closed!"));
        }

        for (MultiplexedChannelRecord connection : connections) {
            if (connection.numAvailableStreams() > 0) {
                acquireChildChannel(promise, connection);
                return promise;
            }
        }

        // No available streams, establish new connection and add it to list
        Future<Channel> acquire = connectionPool.acquire();

        MultiplexedChannelRecord channelRecord = new MultiplexedChannelRecord(acquire, maxConcurrencyPerConnection);
        connections.add(channelRecord);

        acquire.addListener(f -> {
            if (acquire.isSuccess()) {
                initializeChannelAttributes(channelRecord, acquire);
            } else {
                connections.remove(channelRecord);
                channelRecord.shutdownChildChannels(acquire.cause());
            }
        });

        acquireChildChannel(promise, channelRecord);

        return promise;
    }

    private void acquireChildChannel(MultiplexedChannelRecord channelRecord, Promise<Channel> promise) {
        channelRecord.acquireChildChannel(promise);
        promise.addListener(f -> childChannelAcquireComplete(channelRecord, promise));
    }

    private void childChannelAcquireComplete(MultiplexedChannelRecord channelRecord, Promise<Channel> promise) {
        if (promise.isSuccess()) {
            initializeChannelAttributes(channelRecord, promise);
        } else {
            Channel underlyingConnection = channelRecord.getConnection();
            if (underlyingConnection == null) {
                releaseParentChannel();
            }
        }
    }

    private void initializeChannelAttributes(MultiplexedChannelRecord channelRecord, Future<Channel> acquire) {
        Channel channel = acquire.getNow();
        channel.attr(ChannelAttributeKey.HTTP2_MULTIPLEXED_CHANNEL_POOL).set(this);
        channel.attr(CHANNEL_POOL_RECORD).set(channelRecord);
    }

    /**
     * Releases parent channel on failure and cleans up record from connections list.
     *
     * @param parentChannel Channel to release. May be null if no channel is established.
     * @param record Record to cleanup.
     */
    private void releaseParentChannel(Channel parentChannel, MultiplexedChannelRecord record) {
        doInEventLoop(eventLoop, () -> releaseParentChannel0(parentChannel, record));
    }

    private void releaseParentChannel0(Channel parentChannel, MultiplexedChannelRecord record) {
        if (parentChannel != null) {
            try {
                parentChannel.close().addListener(f -> warnOnCloseFailure(parentChannel, f));
            } finally {
                connectionPool.release(parentChannel);
            }
        }
        connections.remove(record);
    }

    @Override
    public Future<Void> release(Channel childChannel) {
        return release(childChannel, new DefaultPromise<>(eventLoop));
    }

    @Override
    public Future<Void> release(Channel channel, Promise<Void> promise) {
        doInEventLoop(eventLoop, () -> release0(channel, promise), promise);
        return promise;
    }

    private void release0(Channel channel, Promise<Void> promise) {
        if (channel.parent() == null) {
            // This is the socket channel, close and release from underlying connection pool
            try {
                releaseParentChannel(channel);
            } finally {
                // This channel doesn't technically belong to this pool as it was never acquired directly
                promise.setFailure(new IllegalArgumentException("Channel does not belong to this pool"));
            }
        } else {
            Channel parentChannel = channel.parent();
            MultiplexedChannelRecord channelRecord = parentChannel.attr(CHANNEL_POOL_RECORD).get();
            channelRecord.release(channel);
            channel.close().addListener(f -> warnOnCloseFailure(channel, f));

            if (channelRecord.isClosing() && channelRecord.numActiveChildChannels() == 0) {
                releaseParentChannel(parentChannel);
            }

            promise.setSuccess(null);
        }
    }

    private void releaseParentChannel(Channel parentChannel) {
        MultiplexedChannelRecord channelRecord = parentChannel.attr(CHANNEL_POOL_RECORD).get();
        connections.remove(channelRecord);
        parentChannel.close().addListener(f -> warnOnCloseFailure(parentChannel, f));
        connectionPool.release(parentChannel);
    }

    @Override
    public void close() {
        try {
            setClosedFlag().await();
            for (MultiplexedChannelRecord c : connections) {
                Future<Channel> f = c.getConnectionFuture();
                f.await();
                if (f.isSuccess()) {
                    connectionPool.release(f.getNow()).await();
                }
            }
            connectionPool.close();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ie);
        }
    }

    private Promise<Void> setClosedFlag() {
        Promise<Void> closedFuture = eventLoop.newPromise();
        doInEventLoop(eventLoop, () -> {
            closed = true;
            closedFuture.setSuccess(null);
        });
        return closedFuture;
    }

    private static void warnOnCloseFailure(Channel channel, Future<?> future) {
        if (!future.isSuccess()) {
            log.warn(() -> "Failure when closing channel: " + channel.id(), future.cause());
        }
    }

    private void killChannelAndParents(Channel channel) {
        Channel topmostParentChannel = NettyUtils.topmostParentChannel(channel);
        topmostParentChannel.close().addListener(f -> warnOnCloseFailure(topmostParentChannel, f));
    }
}
