package software.amazon.awssdk.protocol.tests;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import org.junit.Rule;
import org.junit.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.protocolrestjson.ProtocolRestJsonAsyncClient;
import software.amazon.awssdk.services.protocolrestjson.model.StreamingOutputOperationRequest;

public class StreamingTest {
    @Rule
    public WireMockRule wireMock = new WireMockRule(wireMockConfig().port(0), false);

    private static final String STREAMING_OUTPUT_PATH = "/2016-03-11/streamingOutputOperation";

    @Test
    public void streamingReadTimeoutsAreIoExceptions() {
        stubFor(post(urlPathEqualTo(STREAMING_OUTPUT_PATH)).willReturn(aResponse().withStatus(200)
                                                                                  .withHeader("Content-Length", "1024")
                                                                                  .withBody("Howdy!")));

        ProtocolRestJsonAsyncClient client = asyncClient();
        assertThatThrownBy(() -> client.streamingOutputOperation(StreamingOutputOperationRequest.builder().build(), AsyncResponseTransformer.toBytes()).join())
            .satisfies(Throwable::printStackTrace)
            .hasCauseInstanceOf(IOException.class);
    }

    private ProtocolRestJsonAsyncClient asyncClient() {
        return ProtocolRestJsonAsyncClient.builder()
                                          .region(Region.US_WEST_1)
                                          .endpointOverride(URI.create("http://localhost:" + wireMock.port()))
                                          .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("akid", "skid")))
                                          .httpClientBuilder(NettyNioAsyncHttpClient.builder().readTimeout(Duration.ofSeconds(2)))
                                          .build();
    }
}
