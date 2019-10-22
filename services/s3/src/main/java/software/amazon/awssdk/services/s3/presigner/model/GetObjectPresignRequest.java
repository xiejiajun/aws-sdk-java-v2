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

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.presigner.PresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class GetObjectPresignRequest
        extends PresignRequest
        implements ToCopyableBuilder<GetObjectPresignRequest.Builder, GetObjectPresignRequest> {
    private final GetObjectRequest getObjectRequest;

    private GetObjectPresignRequest(DefaultBuilder builder) {
        super(builder);
        this.getObjectRequest = builder.getObjectRequest;
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public GetObjectRequest getObjectRequest() {
        return getObjectRequest;
    }

    @Override
    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    public interface Builder extends PresignRequest.Builder,
                                     CopyableBuilder<GetObjectPresignRequest.Builder, GetObjectPresignRequest> {
        Builder getObjectRequest(GetObjectRequest getObjectRequest);

        @Override
        Builder signatureDuration(Duration signatureDuration);

        @Override
        GetObjectPresignRequest build();
    }

    private static final class DefaultBuilder extends PresignRequest.DefaultBuilder implements Builder {
        private GetObjectRequest getObjectRequest;

        private DefaultBuilder() {}

        private DefaultBuilder(GetObjectPresignRequest request) {
            super(request);
            this.getObjectRequest = request.getObjectRequest;
        }

        @Override
        public Builder signatureDuration(Duration signatureDuration) {
            super.signatureDuration(signatureDuration);
            return this;
        }

        @Override
        public Builder getObjectRequest(GetObjectRequest getObjectRequest) {
            this.getObjectRequest = getObjectRequest;
            return this;
        }

        @Override
        public GetObjectPresignRequest build() {
            return null;
        }
    }
}
