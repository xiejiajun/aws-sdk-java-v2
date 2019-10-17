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

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.Validate;

/**
 * A presigned S3 'GetObject' request.
 * <p>
 * See {@link PresignedRequest} for a full description of each property of this class.
 */
@SdkPublicApi
public class PresignedGetObjectRequest implements PresignedRequest {

    private final URL url;
    private final Instant expiration;
    private final Map<String, List<String>> signedHeaders;
    private final SdkBytes signedPayload;
    private final SdkHttpRequest httpRequest;

    private PresignedGetObjectRequest(Builder b) {
        this.url = Validate.notNull(b.url, "url");
        this.expiration = Validate.notNull(b.expiration, "expiration");
        this.signedHeaders = b.signedHeaders;
        this.signedPayload = b.signedPayload;
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
        return this.httpRequest.method() == SdkHttpMethod.GET && !hasSignedHeaders() && !hasSignedPayload();
    }

    @Override
    public boolean hasSignedHeaders() {
        return this.signedHeaders != null;
    }

    @Override
    public Optional<Map<String, List<String>>> signedHeaders() {
        return Optional.ofNullable(signedHeaders);
    }

    @Override
    public boolean hasSignedPayload() {
        return this.signedPayload != null;
    }

    @Override
    public Optional<SdkBytes> signedPayload() {
        return Optional.ofNullable(signedPayload);
    }

    @Override
    public SdkHttpRequest httpRequest() {
        return httpRequest;
    }

    /**
     * Constructs a {@link Builder} object initialized with values of the properties of this object.
     */
    public Builder toBuilder() {
        return new Builder().expiration(expiration)
                            .httpRequest(httpRequest)
                            .url(url)
                            .signedHeaders(signedHeaders)
                            .signedPayload(signedPayload);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PresignedGetObjectRequest that = (PresignedGetObjectRequest) o;

        if (! url.equals(that.url)) {
            return false;
        }
        if (! expiration.equals(that.expiration)) {
            return false;
        }
        if (signedHeaders != null ? ! signedHeaders.equals(that.signedHeaders) : that.signedHeaders != null) {
            return false;
        }
        if (signedPayload != null ? ! signedPayload.equals(that.signedPayload) : that.signedPayload != null) {
            return false;
        }

        return httpRequest.equals(that.httpRequest);
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + expiration.hashCode();
        result = 31 * result + (signedHeaders != null ? signedHeaders.hashCode() : 0);
        result = 31 * result + (signedPayload != null ? signedPayload.hashCode() : 0);
        result = 31 * result + httpRequest.hashCode();
        return result;
    }

    /**
     * Constructs a newly initialized {@link Builder} object.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements PresignedRequest.Builder {
        private URL url;
        private Instant expiration;
        private Map<String, List<String>> signedHeaders;
        private SdkBytes signedPayload;
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
        public Builder signedHeaders(Map<String, List<String>> signedHeaders) {
            this.signedHeaders = signedHeaders;
            return this;
        }

        @Override
        public Builder signedPayload(SdkBytes signedPayload) {
            this.signedPayload = signedPayload;
            return this;
        }

        @Override
        public Builder httpRequest(SdkHttpRequest httpRequest) {
            this.httpRequest = httpRequest;
            return this;
        }

        @Override
        public PresignedGetObjectRequest build() {
            return new PresignedGetObjectRequest(this);
        }
    }
}
