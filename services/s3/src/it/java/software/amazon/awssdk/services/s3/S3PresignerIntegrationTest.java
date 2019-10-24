package software.amazon.awssdk.services.s3;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.UUID;
import java.util.function.Function;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import software.amazon.awssdk.core.BytesWrapper;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.HttpExecuteResponse;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.model.RequestPayer;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.utils.S3TestUtils;
import software.amazon.awssdk.utils.IoUtils;

public class S3PresignerIntegrationTest {
    private S3Client client;
    private S3Presigner presigner;
    private String testBucket;
    private String testNonDnsCompatibleBucket;
    private String testObjectKey;
    private String testObjectContent;

    @Before
    public void setUp() {
        this.client = S3Client.create();
        this.presigner = S3Presigner.create();
        this.testBucket = S3TestUtils.getTestBucket(client);
        this.testNonDnsCompatibleBucket = S3TestUtils.getNonDnsCompatibleTestBucket(client);
        this.testObjectKey = "s3-presigner-it-" + UUID.randomUUID();
        this.testObjectContent = "Howdy!";

        S3TestUtils.putObject(client, testBucket, testObjectKey, testObjectContent);
        S3TestUtils.putObject(client, testNonDnsCompatibleBucket, testObjectKey, testObjectContent);
    }

    @After
    public void tearDown() {
        this.client.close();
        this.presigner.close();
    }

    @Test
    public void browserCompatiblePresignedUrlWorks() throws IOException {
        assertThatPresigningWorks(testBucket, testObjectKey);
    }

    @Test
    public void bucketsWithScaryCharactersWorks() throws IOException {
        assertThatPresigningWorks(testNonDnsCompatibleBucket, testObjectKey);
    }

    @Test
    public void keysWithScaryCharactersWorks() throws IOException {
        String scaryObjectKey = testObjectKey + " !'/()~`";
        S3TestUtils.putObject(client, testBucket, scaryObjectKey, testObjectContent);
        assertThatPresigningWorks(testBucket, scaryObjectKey);
    }

    private void assertThatPresigningWorks(String bucket, String objectKey) throws IOException {
        PresignedGetObjectRequest presigned =
            presigner.presignGetObject(r -> r.signatureDuration(Duration.ofMinutes(5))
                                             .getObjectRequest(gor -> gor.bucket(bucket).key(objectKey)));

        assertThat(presigned.isBrowserCompatible()).isTrue();

        try (InputStream response = presigned.url().openConnection().getInputStream()) {
            assertThat(IoUtils.toUtf8String(response)).isEqualTo(testObjectContent);
        }
    }

    @Test
    public void browserIncompatiblePresignedUrlDoesNotWorkWithoutAdditionalHeaders() throws IOException {
        PresignedGetObjectRequest presigned =
            presigner.presignGetObject(r -> r.signatureDuration(Duration.ofMinutes(5))
                                             .getObjectRequest(gor -> gor.bucket(testBucket)
                                                                         .key(testObjectKey)
                                                                         .requestPayer(RequestPayer.REQUESTER)));

        assertThat(presigned.isBrowserCompatible()).isFalse();

        HttpURLConnection connection = (HttpURLConnection) presigned.url().openConnection();
        connection.connect();
        try {
            assertThat(connection.getResponseCode()).isEqualTo(403);
        } finally {
            connection.disconnect();
        }
    }

    @Test
    public void browserIncompatiblePresignedUrlWorksWithAdditionalHeaders() throws IOException {
        PresignedGetObjectRequest presigned =
            presigner.presignGetObject(r -> r.signatureDuration(Duration.ofMinutes(5))
                                             .getObjectRequest(gor -> gor.bucket(testBucket)
                                                                         .key(testObjectKey)
                                                                         .requestPayer(RequestPayer.REQUESTER)));

        assertThat(presigned.isBrowserCompatible()).isFalse();

        HttpURLConnection connection = (HttpURLConnection) presigned.url().openConnection();

        presigned.httpRequest().headers().forEach((header, values) -> {
            values.forEach(value -> {
                connection.addRequestProperty(header, value);
            });
        });

        try (InputStream content = connection.getInputStream()) {
            assertThat(IoUtils.toUtf8String(content)).isEqualTo(testObjectContent);
        }
    }

    @Test
    public void presignedHttpRequestCanBeInvokedDirectlyBySdk() throws IOException {
        PresignedGetObjectRequest presigned =
            presigner.presignGetObject(r -> r.signatureDuration(Duration.ofMinutes(5))
                                             .getObjectRequest(gor -> gor.bucket(testBucket)
                                                                         .key(testObjectKey)
                                                                         .requestPayer(RequestPayer.REQUESTER)));

        assertThat(presigned.isBrowserCompatible()).isFalse();

        SdkHttpClient httpClient = ApacheHttpClient.builder().build(); // or UrlConnectionHttpClient.builder().build()

        ContentStreamProvider requestPayload = presigned.signedPayload()
                                                        .map(SdkBytes::asContentStreamProvider)
                                                        .orElse(null);

        HttpExecuteRequest request = HttpExecuteRequest.builder()
                                                       .request(presigned.httpRequest())
                                                       .contentStreamProvider(requestPayload)
                                                       .build();

        HttpExecuteResponse response = httpClient.prepareRequest(request).call();

        assertThat(response.responseBody()).isPresent();
        try (InputStream responseStream = response.responseBody().get()) {
            assertThat(IoUtils.toUtf8String(responseStream)).isEqualTo(testObjectContent);
        }
    }
}
