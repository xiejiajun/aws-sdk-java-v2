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
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.awscore.presigner.SdkPresigner;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.internal.presigner.DefaultS3Presigner;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkPublicApi
@Immutable
@ThreadSafe
public interface S3Presigner extends SdkPresigner, SdkAutoCloseable {
    static S3Presigner create() {
        return DefaultS3Presigner.create();
    }

    static Builder builder() {
        return DefaultS3Presigner.builder();
    }

    PresignedGetObjectRequest presignGetObject(GetObjectPresignRequest request);

    @SdkPublicApi
    @NotThreadSafe
    interface Builder extends SdkPresigner.Builder {
        Builder region(Region region);

        Builder credentialsProvider(AwsCredentialsProvider credentialsProvider);

        Builder endpointOverride(URL endpointOverride);

        S3Presigner build();
    }
}
