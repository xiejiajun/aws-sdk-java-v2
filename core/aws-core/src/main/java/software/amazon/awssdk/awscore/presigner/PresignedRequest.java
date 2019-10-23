package software.amazon.awssdk.awscore.presigner;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.Validate;

/**
 * A generic presigned request. The isBrowserCompatible method can be used to determine whether this request
 * can be executed by a web browser.
 */
@SdkPublicApi
public abstract class PresignedRequest {
    private final URL url;
    private final Instant expiration;
    private final boolean isBrowserCompatible;
    private final Map<String, List<String>> signedHeaders;
    private final Optional<SdkBytes> signedPayload;
    private final SdkHttpRequest httpRequest;

    protected PresignedRequest(DefaultBuilder builder) {
        this.url = Validate.notNull(builder.url, "url");
        this.expiration = Validate.notNull(builder.expiration, "expiration");
        this.isBrowserCompatible = Validate.notNull(builder.isBrowserCompatible, "isBrowserCompatible");
        this.signedHeaders = Validate.notEmpty(builder.signedHeaders, "signedHeaders");
        this.signedPayload = Validate.notNull(builder.signedPayload, "signedPayload");
        this.httpRequest = Validate.notNull(builder.httpRequest, "httpRequest");
    }

    /**
     * The URL that the presigned request will execute against. The isBrowserCompatible method can be used to
     * determine whether this request will work in a browser.
     */
    public URL url() {
        return url;
    }

    /**
     * The exact SERVICE time that the request will expire. After this time, attempting to execute the request
     * will fail.
     * <p>
     * This may differ from the local clock, based on the skew between the local and AWS service clocks.
     */
    public Instant expiration() {
        return expiration;
    }

    /**
     * Returns true if the url returned by the url method can be executed in a browser.
     * <p>
     * This is true when the HTTP request method is GET, and hasSignedHeaders and hasSignedPayload are false.
     * <p>
     * TODO: This isn't a universally-agreed-upon-good method name. We should iterate on it before GA.
     */
    public boolean isBrowserCompatible() {
        return isBrowserCompatible;
    }

    /**
     * Returns the subset of headers that were signed, and MUST be included in the presigned request to prevent
     * the request from failing.
     */
    public Map<String, List<String>> signedHeaders() {
        return signedHeaders;
    }

    /**
     * Returns the payload that was signed, or Optional.empty() if hasSignedPayload is false.
     */
    public Optional<SdkBytes> signedPayload() {
        return signedPayload;
    }

    /**
     * The entire SigV4 query-parameter signed request (minus the payload), that can be transmitted as-is to a
     * service using any HTTP client that implement the SDK's HTTP client SPI.
     * <p>
     * This request includes signed AND unsigned headers.
     */
    public SdkHttpRequest httpRequest() {
        return httpRequest;
    }

    public interface Builder {
        /**
         * Specifies the URL that the presigned request will execute against. The isBrowserCompatible method can be used to
         * determine whether this request will work in a browser.
         */
        Builder url(URL url);

        /**
         * The exact SERVICE time that the request will expire. After this time, attempting to execute the request
         * will fail.
         * <p>
         * This may differ from the local clock, based on the skew between the local and AWS service clocks.
         */
        Builder expiration(Instant expiration);

        /**
         * Whether the url returned by the url method can be executed in a browser.
         */
        Builder isBrowserCompatible(Boolean isBrowserCompatible);

        /**
         * Returns the subset of headers that were signed, and MUST be included in the presigned request to prevent
         * the request from failing.
         */
        Builder signedHeaders(Map<String, List<String>> signedHeaders);

        /**
         * Returns the payload that was signed, or Optional.empty() if hasSignedPayload is false.
         */
        Builder signedPayload(SdkBytes signedPayload);

        /**
         * The entire SigV4 query-parameter signed request (minus the payload), that can be transmitted as-is to a
         * service using any HTTP client that implement the SDK's HTTP client SPI.
         * <p>
         * This request includes signed AND unsigned headers.
         */
        Builder httpRequest(SdkHttpRequest httpRequest);

        PresignedRequest build();
    }

    protected abstract static class DefaultBuilder implements Builder {
        private URL url;
        private Instant expiration;
        private Boolean isBrowserCompatible;
        private Map<String, List<String>> signedHeaders;
        private Optional<SdkBytes> signedPayload;
        private SdkHttpRequest httpRequest;

        protected DefaultBuilder() {}

        protected DefaultBuilder(PresignedRequest request) {
            this.url = request.url;
            this.expiration = request.expiration;
            this.isBrowserCompatible = request.isBrowserCompatible;
            this.signedHeaders = request.signedHeaders;
            this.signedPayload = request.signedPayload;
            this.httpRequest = request.httpRequest;
        }

        @Override
        public Builder url(URL url) {
            this.url = url;
            return this;
        }

        @Override
        public Builder expiration(Instant expiration) {
            this.expiration = expiration;
            return this;
        }

        @Override
        public Builder isBrowserCompatible(Boolean isBrowserCompatible) {
            this.isBrowserCompatible = isBrowserCompatible;
            return this;
        }

        @Override
        public Builder signedHeaders(Map<String, List<String>> signedHeaders) {
            this.signedHeaders = signedHeaders;
            return this;
        }

        @Override
        public Builder signedPayload(SdkBytes signedPayload) {
            this.signedPayload = Optional.ofNullable(signedPayload);
            return this;
        }

        @Override
        public Builder httpRequest(SdkHttpRequest httpRequest) {
            this.httpRequest = httpRequest;
            return this;
        }
    }
}