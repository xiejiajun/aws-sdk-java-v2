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

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineException;
import io.netty.handler.codec.http2.DefaultHttp2GoAwayFrame;
import io.netty.util.Attribute;
import org.junit.Before;
import org.junit.Test;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;

public class Http2GoAwayFrameHandlerTest {
    private static final DefaultHttp2GoAwayFrame GO_AWAY_FRAME = new DefaultHttp2GoAwayFrame(0, Unpooled.EMPTY_BUFFER);
    private ChannelHandlerContext ctx;
    private Channel channel;
    private ChannelPipeline channelPipeline;
    private Attribute<Http2MultiplexedChannelPool> attribute;

    @Before
    public void setup() {
        this.ctx = mock(ChannelHandlerContext.class);
        this.channel = mock(Channel.class);
        this.channelPipeline = mock(ChannelPipeline.class);
        this.attribute = mock(Attribute.class);

        when(ctx.channel()).thenReturn(channel);
        when(channel.pipeline()).thenReturn(channelPipeline);
        when(channel.attr(ChannelAttributeKey.HTTP2_MULTIPLEXED_CHANNEL_POOL)).thenReturn(attribute);
    }

    @Test
    public void goAwayWithNoChannelPoolRecordRaisesNoExceptions() {
        when(attribute.get()).thenReturn(null);
        new Http2GoAwayFrameHandler().channelRead0(ctx, GO_AWAY_FRAME);
        verify(channelPipeline).fireExceptionCaught(isA(GoAwayException.class));
    }

    @Test
    public void goAwayWithChannelPoolRecordPassesAlongTheFrame() {
        Http2MultiplexedChannelPool record = mock(Http2MultiplexedChannelPool.class);
        when(attribute.get()).thenReturn(record);
        new Http2GoAwayFrameHandler().channelRead0(ctx, GO_AWAY_FRAME);
        verify(record).handleGoAway(channel, GO_AWAY_FRAME);
        verifyNoMoreInteractions(record);
    }
}
