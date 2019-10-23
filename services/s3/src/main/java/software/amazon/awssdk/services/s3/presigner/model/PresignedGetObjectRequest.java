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

package software.amazon.awssdk.services.s3.presigner.model;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public class PresignedGetObjectRequest
        extends PresignedRequest
        implements ToCopyableBuilder<PresignedGetObjectRequest.Builder, PresignedGetObjectRequest> {
    private PresignedGetObjectRequest(DefaultBuilder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    @SdkPublicApi
    @NotThreadSafe
    public interface Builder extends PresignedRequest.Builder,
                                     CopyableBuilder<PresignedGetObjectRequest.Builder, PresignedGetObjectRequest> {
        @Override
        Builder url(URL url);

        @Override
        Builder expiration(Instant expiration);

        @Override
        Builder isBrowserCompatible(Boolean isBrowserCompatible);

        @Override
        Builder signedHeaders(Map<String, List<String>> signedHeaders);

        @Override
        Builder signedPayload(SdkBytes signedPayload);

        @Override
        Builder httpRequest(SdkHttpRequest httpRequest);

        @Override
        PresignedGetObjectRequest build();
    }

    @SdkInternalApi
    private static final class DefaultBuilder
            extends PresignedRequest.DefaultBuilder
            implements Builder {
        private DefaultBuilder() { }

        private DefaultBuilder(PresignedGetObjectRequest request) {
            super(request);
        }

        @Override
        public Builder url(URL url) {
            super.url(url);
            return this;
        }

        @Override
        public Builder expiration(Instant expiration) {
            super.expiration(expiration);
            return this;
        }

        @Override
        public Builder isBrowserCompatible(Boolean isBrowserCompatible) {
            super.isBrowserCompatible(isBrowserCompatible);
            return this;
        }

        @Override
        public Builder signedHeaders(Map<String, List<String>> signedHeaders) {
            super.signedHeaders(signedHeaders);
            return this;
        }

        @Override
        public Builder signedPayload(SdkBytes signedPayload) {
            super.signedPayload(signedPayload);
            return this;
        }

        @Override
        public Builder httpRequest(SdkHttpRequest httpRequest) {
            super.httpRequest(httpRequest);
            return this;
        }

        @Override
        public PresignedGetObjectRequest build() {
            return new PresignedGetObjectRequest(this);
        }
    }
}
