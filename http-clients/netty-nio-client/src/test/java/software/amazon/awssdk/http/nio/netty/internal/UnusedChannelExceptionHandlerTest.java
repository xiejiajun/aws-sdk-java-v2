package software.amazon.awssdk.http.nio.netty.internal;

import static org.assertj.core.api.Assertions.assertThat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import java.io.IOException;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import software.amazon.awssdk.http.nio.netty.internal.utils.ExceptionConvertingCompletableFuture;

public class UnusedChannelExceptionHandlerTest {
    private Throwable exception = new Throwable();
    private IOException ioException = new IOException();

    private ChannelHandlerContext ctx;
    private Channel channel;
    private Attribute<Boolean> inUseAttribute;
    private Attribute<ExceptionConvertingCompletableFuture> futureAttribute;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void setUp() {
        ctx = Mockito.mock(ChannelHandlerContext.class);
        channel = Mockito.mock(Channel.class);

        inUseAttribute = Mockito.mock(Attribute.class);
        futureAttribute = Mockito.mock(Attribute.class);

        Mockito.when(ctx.channel()).thenReturn(channel);
        Mockito.when(channel.attr(ChannelAttributeKey.IN_USE)).thenReturn(inUseAttribute);
        Mockito.when(channel.attr(ChannelAttributeKey.EXECUTION_RESULT)).thenReturn(futureAttribute);
    }

    @Test
    public void inUseDoesNothing() {
        Mockito.when(inUseAttribute.get()).thenReturn(true);

        UnusedChannelExceptionHandler.getInstance().exceptionCaught(ctx, exception);

        Mockito.verify(ctx).fireExceptionCaught(exception);
        Mockito.verify(ctx, new Times(0)).close();
    }

    @Test
    public void notInUseNonIoExceptionCloses() {
        notInUseCloses(exception);
    }

    @Test
    public void notInUseIoExceptionCloses() {
        notInUseCloses(ioException);
    }

    @Test
    public void notInUseHasIoExceptionCauseCloses() {
        notInUseCloses(new RuntimeException(ioException));
    }


    private void notInUseCloses(Throwable exception) {
        ExceptionConvertingCompletableFuture result = ExceptionConvertingCompletableFuture.create();
        result.trySucceedExecution();

        Mockito.when(inUseAttribute.get()).thenReturn(false);
        Mockito.when(futureAttribute.get()).thenReturn(result);

        UnusedChannelExceptionHandler.getInstance().exceptionCaught(ctx, exception);

        Mockito.verify(ctx).close();
    }

    @Test
    public void notInUseFutureCompletes() {
        ExceptionConvertingCompletableFuture result = ExceptionConvertingCompletableFuture.create();

        Mockito.when(inUseAttribute.get()).thenReturn(false);
        Mockito.when(futureAttribute.get()).thenReturn(result);

        UnusedChannelExceptionHandler.getInstance().exceptionCaught(ctx, exception);

        Mockito.verify(ctx).close();
        assertThat(result.outputFuture().isDone()).isTrue();
    }
}
