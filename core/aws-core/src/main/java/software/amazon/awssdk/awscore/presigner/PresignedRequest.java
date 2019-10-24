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

package software.amazon.awssdk.awscore.presigner;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.Validate;


/**
 * The base class for all presigned requests.
 * <p/>
 * The {@link #isBrowserCompatible} method can be used to determine whether this request can be executed by a web browser.
 */
@SdkPublicApi
public abstract class PresignedRequest {
    private final URL url;
    private final Instant expiration;
    private final boolean isBrowserCompatible;
    private final Map<String, List<String>> signedHeaders;
    private final Optional<SdkBytes> signedPayload;
    private final SdkHttpRequest httpRequest;

    protected PresignedRequest(DefaultBuilder<?> builder) {
        this.url = Validate.notNull(builder.url, "url");
        this.expiration = Validate.notNull(builder.expiration, "expiration");
        this.isBrowserCompatible = Validate.notNull(builder.isBrowserCompatible, "isBrowserCompatible");
        this.signedHeaders = Validate.notEmpty(builder.signedHeaders, "signedHeaders");
        this.signedPayload = Validate.notNull(builder.signedPayload, "signedPayload");
        this.httpRequest = Validate.notNull(builder.httpRequest, "httpRequest");
    }

    /**
     * The URL that the presigned request will execute against. The {@link #isBrowserCompatible} method can be used to
     * determine whether this request will work in a browser.
     */
    public URL url() {
        return url;
    }

    /**
     * The exact SERVICE time that the request will expire. After this time, attempting to execute the request
     * will fail.
     * <p/>
     * This may differ from the local clock, based on the skew between the local and AWS service clocks.
     */
    public Instant expiration() {
        return expiration;
    }

    /**
     * Whether the url returned by the url method can be executed in a browser.
     * <p/>
     * This is true when the HTTP request method is GET, and the request doesn't require any headers or payloads that wouldn't
     * be sent by a browser.
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
     * Returns the payload that was signed, or Optional.empty() if there is no signed payload with this request.
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

    @SdkPublicApi
    public interface Builder {
        /**
         * Configure the URL that the presigned request will execute against.
         */
        Builder url(URL url);

        /**
         * Configure the exact SERVICE time that the request will expire. After this time, attempting to execute the request
         * will fail.
         */
        Builder expiration(Instant expiration);

        /**
         * Configure whether the url returned by the url method can be executed in a browser.
         */
        Builder isBrowserCompatible(Boolean isBrowserCompatible);

        /**
         * Configure the subset of headers that were signed, and MUST be included in the presigned request to prevent
         * the request from failing.
         */
        Builder signedHeaders(Map<String, List<String>> signedHeaders);

        /**
         * Configure the payload that was signed.
         */
        Builder signedPayload(SdkBytes signedPayload);

        /**
         * Configure the entire SigV4 query-parameter signed request (minus the payload), that can be transmitted as-is to a
         * service using any HTTP client that implement the SDK's HTTP client SPI.
         */
        Builder httpRequest(SdkHttpRequest httpRequest);

        PresignedRequest build();
    }

    @SdkProtectedApi
    protected abstract static class DefaultBuilder<B extends DefaultBuilder<B>> implements Builder {
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
        public B url(URL url) {
            this.url = url;
            return thisBuilder();
        }

        @Override
        public B expiration(Instant expiration) {
            this.expiration = expiration;
            return thisBuilder();
        }

        @Override
        public B isBrowserCompatible(Boolean isBrowserCompatible) {
            this.isBrowserCompatible = isBrowserCompatible;
            return thisBuilder();
        }

        @Override
        public B signedHeaders(Map<String, List<String>> signedHeaders) {
            this.signedHeaders = signedHeaders;
            return thisBuilder();
        }

        @Override
        public B signedPayload(SdkBytes signedPayload) {
            this.signedPayload = Optional.ofNullable(signedPayload);
            return thisBuilder();
        }

        @Override
        public B httpRequest(SdkHttpRequest httpRequest) {
            this.httpRequest = httpRequest;
            return thisBuilder();
        }

        @SuppressWarnings("unused")
        protected B thisBuilder() {
            return (B) this;
        }
    }
}