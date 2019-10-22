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

import java.time.Duration;
import software.amazon.awssdk.utils.Validate;

public abstract class PresignRequest {
    private final Duration signatureDuration;

    protected PresignRequest(DefaultBuilder builder) {
        this.signatureDuration = Validate.paramNotNull(builder.signatureDuration, "signatureDuration");
    }

    public Duration signatureDuration() {
        return this.signatureDuration;
    }

    protected <T extends Builder> T toBuilder(T builder) {
        builder.signatureDuration(signatureDuration);
        return builder;
    }

    public interface Builder {
        /**
         * Specifies the duration for which this presigned request should be valid. After this time has
         * expired, attempting to use the presigned request will fail.
         */
        Builder signatureDuration(Duration signatureDuration);

        PresignRequest build();
    }

    protected abstract static class DefaultBuilder implements Builder {
        private Duration signatureDuration;

        protected DefaultBuilder() {}

        protected DefaultBuilder(PresignRequest request) {
            this.signatureDuration = request.signatureDuration;
        }

        /**
         * The duration for which this presigned request should be valid. After this time has
         * expired, attempting to use the presigned request will fail.
         */
        @Override
        public Builder signatureDuration(Duration signatureDuration) {
            this.signatureDuration = signatureDuration;
            return this;
        }
    }
}
