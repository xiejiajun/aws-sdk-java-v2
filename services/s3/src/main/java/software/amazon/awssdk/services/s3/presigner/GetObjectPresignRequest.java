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

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.presigner.PresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.utils.Validate;

/**
 * A request object that can be used to construct a presigned 'GetObject' request for S3.
 */
@SdkPublicApi
public class GetObjectPresignRequest implements PresignRequest {

    private final GetObjectRequest getObjectRequest;

    private final Duration signatureDuration;

    private GetObjectPresignRequest(Builder b) {
        this.getObjectRequest = Validate.notNull(b.getObjectRequest, "getObjectRequest");
        this.signatureDuration = Validate.notNull(b.signatureDuration, "signatureDuration");
    }

    /**
     * The {@link GetObjectRequest} that is to be presigned.
     */
    public GetObjectRequest getObjectRequest() {
        return getObjectRequest;
    }

    /**
     * The duration for which this presigned request should be valid. After this time has
     * expired, attempting to use the presigned request will fail.
     */
    @Override
    public Duration signatureDuration() {
        return signatureDuration;
    }

    /**
     * Constructs a {@link Builder} object initialized with values of the properties of this object.
     */
    public Builder toBuilder() {
        return new Builder().signatureDuration(signatureDuration).getObjectRequest(getObjectRequest);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GetObjectPresignRequest that = (GetObjectPresignRequest) o;

        if (! getObjectRequest.equals(that.getObjectRequest)) {
            return false;
        }

        return signatureDuration.equals(that.signatureDuration);
    }

    @Override
    public int hashCode() {
        int result = getObjectRequest.hashCode();
        result = 31 * result + signatureDuration.hashCode();
        return result;
    }

    /**
     * Constructs a newly initialized {@link Builder} object.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements PresignRequest.Builder {

        private Duration signatureDuration;
        private GetObjectRequest getObjectRequest;

        private Builder() {}

        /**
         * Specifies the duration for which this presigned request should be valid. After this time has
         * expired, attempting to use the presigned request will fail.
         */
        @Override
        public Builder signatureDuration(Duration signatureDuration) {
            this.signatureDuration = signatureDuration;
            return this;
        }

        /**
         * The {@link GetObjectRequest} that is to be presigned.
         */
        public Builder getObjectRequest(GetObjectRequest getObjectRequest) {
            this.getObjectRequest = getObjectRequest;
            return this;
        }

        /**
         * Builds an instance of {@link GetObjectPresignRequest} with properties initialized with the values stored
         * by this builder.
         */
        @Override
        public GetObjectPresignRequest build() {
            return new GetObjectPresignRequest(this);
        }
    }
}
