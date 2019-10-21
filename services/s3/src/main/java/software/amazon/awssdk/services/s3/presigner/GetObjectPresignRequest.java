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

import java.time.Duration;
import software.amazon.awssdk.awscore.presigner.PresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.utils.Validate;

public class GetObjectPresignRequest extends PresignRequest {

    private final GetObjectRequest getObjectRequest;

    private final Duration signatureDuration;

    private GetObjectPresignRequest(Builder b) {
        this.getObjectRequest = Validate.notNull(b.getObjectRequest, "getObjectRequest");
        this.signatureDuration = Validate.notNull(b.signatureDuration, "signatureDuration");
    }

    public GetObjectRequest getObjectRequest() {
        return getObjectRequest;
    }

    @Override
    public Duration signatureDuration() {
        return signatureDuration;
    }

    public Builder toBuilder() {
        return new Builder().signatureDuration(signatureDuration).getObjectRequest(getObjectRequest);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements PresignRequest.Builder {

        private Duration signatureDuration;
        private GetObjectRequest getObjectRequest;

        private Builder() {}

        @Override
        public Builder signatureDuration(Duration signatureDuration) {
            this.signatureDuration = signatureDuration;
            return this;
        }

        public Builder getObjectRequest(GetObjectRequest getObjectRequest) {
            this.getObjectRequest = getObjectRequest;
            return this;
        }

        @Override
        public GetObjectPresignRequest build() {
            return new GetObjectPresignRequest(this);
        }
    }
}
