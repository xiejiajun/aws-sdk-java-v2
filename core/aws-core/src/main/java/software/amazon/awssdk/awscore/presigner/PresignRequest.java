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

import software.amazon.awssdk.annotations.SdkPublicApi;

/**
 * Base interface for a request made to a specific client 'presigner' to generate presigned requests.
 */
@SdkPublicApi
public interface PresignRequest {

    /**
     * The duration for which this presigned request should be valid. After this time has
     * expired, attempting to use the presigned request will fail.
     */
    Duration signatureDuration();

    /**
     * Base interface that must be implemented by any builder that builds a class that implements
     * {@link PresignRequest}.
     */
    interface Builder {

        /**
         * Specifies the duration for which this presigned request should be valid. After this time has
         * expired, attempting to use the presigned request will fail.
         */
        Builder signatureDuration(Duration signatureDuration);

        /**
         * Builds a new object that implements {@link PresignRequest}.
         * @return A {@link PresignRequest} object.
         */
        PresignRequest build();
    }
}
