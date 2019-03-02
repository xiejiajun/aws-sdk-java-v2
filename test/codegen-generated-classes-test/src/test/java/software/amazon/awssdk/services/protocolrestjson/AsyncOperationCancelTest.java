package software.amazon.awssdk.services.protocolrestjson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.services.protocolrestjson.model.AllTypesResponse;
import software.amazon.awssdk.services.protocolrestjson.model.EventStream;
import software.amazon.awssdk.services.protocolrestjson.model.EventStreamOperationResponse;
import software.amazon.awssdk.services.protocolrestjson.model.EventStreamOperationResponseHandler;
import software.amazon.awssdk.services.protocolrestjson.model.StreamingInputOperationResponse;
import software.amazon.awssdk.services.protocolrestjson.model.StreamingOutputOperationResponse;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Test to ensure that cancelling the future returned for an async operation will cancel the future returned by the async HTTP client.
 */
@RunWith(MockitoJUnitRunner.class)
public class AsyncOperationCancelTest {
    @Mock
    private SdkAsyncHttpClient mockHttpClient;

    private ProtocolRestJsonAsyncClient client;

    private CompletableFuture executeFuture;

    @Before
    public void setUp() {
        client = ProtocolRestJsonAsyncClient.builder()
                .httpClient(mockHttpClient)
                .build();

        executeFuture = new CompletableFuture();
        when(mockHttpClient.execute(any())).thenReturn(executeFuture);
    }

    @Test
    public void testNonStreamingOperation() {
        CompletableFuture<AllTypesResponse> responseFuture = client.allTypes(r -> {});
        responseFuture.cancel(true);
        assertThat(executeFuture.isCompletedExceptionally()).isTrue();
        assertThat(executeFuture.isCancelled()).isTrue();
    }

    @Test
    public void testStreamingOperation() {
        CompletableFuture<StreamingInputOperationResponse> responseFuture = client.streamingInputOperation(r -> {}, AsyncRequestBody.empty());
        responseFuture.cancel(true);
        assertThat(executeFuture.isCompletedExceptionally()).isTrue();
        assertThat(executeFuture.isCancelled()).isTrue();
    }

    @Test
    public void testStreamingOutputOperation() {
        CompletableFuture<ResponseBytes<StreamingOutputOperationResponse>> responseFuture = client.streamingOutputOperation(r -> {
        }, AsyncResponseTransformer.toBytes());
        responseFuture.cancel(true);
        assertThat(executeFuture.isCompletedExceptionally()).isTrue();
        assertThat(executeFuture.isCancelled()).isTrue();
    }

    @Test
    public void testEventStreamingOperation() {
        CompletableFuture<Void> responseFuture = client.eventStreamOperation(r -> {
                },
                subscriber -> {},
                new EventStreamOperationResponseHandler() {
                    @Override
                    public void responseReceived(EventStreamOperationResponse response) {
                    }

                    @Override
                    public void onEventStream(SdkPublisher<EventStream> publisher) {
                    }

                    @Override
                    public void exceptionOccurred(Throwable throwable) {
                    }

                    @Override
                    public void complete() {
                    }
                });
        responseFuture.cancel(true);
        assertThat(executeFuture.isCompletedExceptionally()).isTrue();
        assertThat(executeFuture.isCancelled()).isTrue();
    }
}
