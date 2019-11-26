
package software.amazon.awssdk.http.nio.netty.internal.utils;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.mockito.stubbing.Answer;

public class NettyMocks {
    public static <T> void mockAttribute(Channel mockChannel, AttributeKey<T> attributeKey, T attributeValue) {
        Attribute<T> attribute = mock(Attribute.class);
        when(attribute.get()).thenReturn(attributeValue);
        when(mockChannel.attr(attributeKey)).thenReturn(attribute);
    }

    public static <T> void mockAttributeAnswer(Channel mockChannel, AttributeKey<T> attributeKey, Answer<? extends T> attributeValue) {
        Attribute<T> attribute = mock(Attribute.class);
        when(attribute.get()).thenAnswer(attributeValue);
        when(mockChannel.attr(attributeKey)).thenReturn(attribute);
    }

    public static void ignoreAttributeSets(Channel mockChannel) {
        Attribute attribute = mock(Attribute.class);
        when(mockChannel.attr(any())).thenReturn(attribute);
    }
}
