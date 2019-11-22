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
import io.netty.channel.ChannelId;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.http2.Http2GoAwayFrame;
import io.netty.handler.codec.http2.Http2StreamChannel;
import io.netty.handler.codec.http2.Http2StreamChannelBootstrap;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import software.amazon.awssdk.annotations.SdkInternalApi;

/**
 * Contains a {@link Future} for the actual socket channel and tracks available
 * streams based on the MAX_CONCURRENT_STREAMS setting for the connection.
 */
@SdkInternalApi
public class MultiplexedChannelRecord {
    private final Channel connection;
    private final EventLoop eventLoop;
    private final Map<ChannelId, Http2StreamChannel> childChannels;
    private final long maxConcurrencyPerConnection;

    private boolean isClosing = false;

    MultiplexedChannelRecord(Channel connection, EventLoop eventLoop, long maxConcurrencyPerConnection) {
        this.connection = connection;
        this.eventLoop = eventLoop;
        this.maxConcurrencyPerConnection = maxConcurrencyPerConnection;
        this.childChannels = new ConcurrentHashMap<>();
    }

    Future<Channel> acquireChildChannel() {
        return acquireChildChannel(new DefaultPromise<>(eventLoop));
    }

    private Future<Channel> acquireChildChannel(Promise<Channel> channelPromise) {
        createChildChannel(channelPromise);
        return channelPromise;
    }

    /**
     * Handle a {@link Http2GoAwayFrame} on this connection, preventing new streams from being created on it, and closing any
     * streams newer than the last-stream-id on the go-away frame.
     */
    public void handleGoAway(Http2GoAwayFrame frame) {
        warnIfNotInEventLoop(eventLoop);

        this.isClosing = true;
        GoAwayException exception = new GoAwayException(frame.errorCode(), frame.content());
        childChannels.values().stream()
                     .filter(cc -> cc.stream().id() > frame.lastStreamId())
                     .forEach(cc -> shutdownChildChannel(cc, exception));
    }

    /**
     * Delivers the exception to all registered child channels, and prohibits new streams being created on this connection.
     *
     * @param t Exception to deliver.
     */
    public void shutdown(Throwable t) {
        warnIfNotInEventLoop(eventLoop);

        this.isClosing = true;
        for (Channel childChannel : childChannels.values()) {
            shutdownChildChannel(childChannel, t);
        }
    }

    private void shutdownChildChannel(Channel childChannel, Throwable t) {
        System.err.println("Firing " + t + " on " + childChannel);
        childChannel.pipeline().fireExceptionCaught(t);
    }

    /**
     * Bootstraps a child stream channel from the parent socket channel. Done in parent channel event loop.
     *
     * @param channelPromise Promise to notify when channel is available.
     */
    private void createChildChannel(Promise<Channel> channelPromise) {
        warnIfNotInEventLoop(eventLoop);

        if (numAvailableStreams() == 0) {
            channelPromise.tryFailure(new IOException("No streams are available on this connection."));
        } else {
            bootstrapChildChannel(channelPromise);
        }
    }

    private void bootstrapChildChannel(Promise<Channel> channelPromise) {
        warnIfNotInEventLoop(eventLoop);

        try {
            Future<Http2StreamChannel> streamFuture = new Http2StreamChannelBootstrap(connection).open();
            streamFuture.addListener((GenericFutureListener<Future<Http2StreamChannel>>) future -> {
                if (future.isSuccess()) {
                    Http2StreamChannel channel = future.getNow();
                    childChannels.put(channel.id(), channel);
                    channelPromise.setSuccess(channel);
                } else {
                    channelPromise.setFailure(future.cause());
                }
            });
        } catch (Exception e) {
            channelPromise.setFailure(e);
        }
    }

    void release(Channel channel) {
        warnIfNotInEventLoop(eventLoop);
        childChannels.remove(channel.id());
    }

    public Channel getConnection() {
        return connection;
    }

    long numAvailableStreams() {
        warnIfNotInEventLoop(eventLoop);
        return isClosing ? 0 : maxConcurrencyPerConnection - childChannels.size();
    }

    long numActiveChildChannels() {
        warnIfNotInEventLoop(eventLoop);
        return childChannels.size();
    }

    boolean isClosing() {
        warnIfNotInEventLoop(eventLoop);
        return isClosing;
    }
}
