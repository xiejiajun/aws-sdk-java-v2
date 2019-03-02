package software.amazon.awssdk.utils;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class CompletableFutureUtilsTest {

    @Test(timeout = 1000)
    public void testForwardException() {
        CompletableFuture src = new CompletableFuture();
        CompletableFuture dst = new CompletableFuture();

        Exception e = new RuntimeException("BOOM");

        CompletableFutureUtils.forwardExceptionTo(src, dst);

        src.completeExceptionally(e);

        try {
            dst.join();
            fail();
        } catch (Throwable t) {
            assertThat(t.getCause()).isEqualTo(e);
        }
    }
}
