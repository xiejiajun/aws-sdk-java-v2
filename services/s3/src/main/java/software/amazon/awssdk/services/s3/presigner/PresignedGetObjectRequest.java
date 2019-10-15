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
package software.amazon.awssdk.services.s3.presigner;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.Validate;

public class PresignedGetObjectRequest implements PresignedRequest {

    private final URL url;
    private final Instant expiration;
    private final boolean isBrowserCompatible;
    private final boolean hasSignedHeaders;
    private final Map<String, List<String>> signedHeaders;
    private final boolean hasSignedPayload;
    private final Optional<SdkBytes> signedPayload;
    private final SdkHttpRequest httpRequest;

    private PresignedGetObjectRequest(Builder b) {
        this.url = Validate.notNull(b.url, "url");
        this.expiration = Validate.notNull(b.expiration, "expiration");
        this.isBrowserCompatible = b.isBrowserCompatible;
        this.hasSignedHeaders = b.hasSignedHeaders;
        this.signedHeaders = Validate.notEmpty(b.signedHeaders, "signedHeaders");
        this.hasSignedPayload = b.hasSignedPayload;
        this.signedPayload = Validate.notNull(b.signedPayload, "signedPayload");
        this.httpRequest = Validate.notNull(b.httpRequest, "httpRequest");
    }

    @Override
    public URL url() {
        return url;
    }

    @Override
    public Instant expiration() {
        return expiration;
    }

    @Override
    public boolean isBrowserCompatible() {
        return isBrowserCompatible;
    }

    @Override
    public boolean hasSignedHeaders() {
        return hasSignedHeaders;
    }

    @Override
    public Map<String, List<String>> signedHeaders() {
        return signedHeaders;
    }

    @Override
    public boolean hasSignedPayload() {
        return hasSignedPayload;
    }

    @Override
    public Optional<SdkBytes> signedPayload() {
        return signedPayload;
    }

    @Override
    public SdkHttpRequest httpRequest() {
        return httpRequest;
    }

    public Builder toBuilder() {
        return new Builder().expiration(expiration)
                            .hasSignedHeaders(hasSignedHeaders)
                            .hasSignedPayload(hasSignedPayload)
                            .httpRequest(httpRequest)
                            .isBrowserCompatible(isBrowserCompatible)
                            .url(url)
                            .signedHeaders(signedHeaders)
                            .signedPayload(signedPayload.orElse(null));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements PresignedRequest.Builder {

        private URL url;
        private Instant expiration;
        private boolean isBrowserCompatible;
        private boolean hasSignedHeaders;
        private Map<String, List<String>> signedHeaders;
        private boolean hasSignedPayload;
        private Optional<SdkBytes> signedPayload;
        private SdkHttpRequest httpRequest;

        private Builder() {}
    
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
        public Builder isBrowserCompatible(boolean isBrowserCompatible) {
            this.isBrowserCompatible = isBrowserCompatible;
            return this;
        }

        @Override
        public Builder hasSignedHeaders(boolean hasSignedHeaders) {
            this.hasSignedHeaders = hasSignedHeaders;
            return this;
        }

        @Override
        public Builder signedHeaders(Map<String, List<String>> signedHeaders) {
            this.signedHeaders = signedHeaders;
            return this;
        }

        @Override
        public Builder hasSignedPayload(boolean hasSignedPayload) {
            this.hasSignedPayload = hasSignedPayload;
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

        @Override
        public PresignedRequest build() {
            return new PresignedGetObjectRequest(this);
        }
    }
}
