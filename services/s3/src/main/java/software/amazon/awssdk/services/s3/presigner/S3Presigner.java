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

import java.net.URI;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.awscore.presigner.SdkPresigner;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.internal.presigner.DefaultS3Presigner;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.utils.SdkAutoCloseable;

/**
 * Enables signing an S3 {@link SdkRequest} so that it can be executed without requiring any additional authentication on the
 * part of the caller.
 * <p/>
 *
 * For example: if Alice has access to an S3 object, and she wants to temporarily share access to that object with Bob, she
 * can generate a pre-signed {@link GetObjectRequest} to secure share with Bob so that he can download the object without
 * requiring access to Alice's credentials.
 * <p/>
 *
 * <b>Signature Duration</b>
 * <p/>
 *
 * Pre-signed requests are only valid for a finite period of time, referred to as the signature duration. This signature
 * duration is configured when the request is generated, and cannot be longer than 7 days. Attempting to generate a signature
 * longer than 7 days in the future will fail at generation time. Attempting to use a pre-signed request after the signature
 * duration has passed will result in an access denied response from the service.
 * <p/>
 *
 * <b>Example Usage</b>
 * <p/>
 *
 * <pre>
 * {@code
 *     // Create an S3Presigner using the default region and credentials.
 *     // This is usually done at application startup, because creating a presigner can be expensive.
 *     S3Presigner presigner = S3Presigner.create();
 *
 *     // Create a GetObjectRequest to be pre-signed
 *     GetObjectRequest getObjectRequest =
 *             GetObjectRequest.builder()
 *                             .bucket("my-bucket")
 *                             .key("my-key")
 *                             .build();
 *
 *     // Create a GetObjectPresignRequest to specify the signature duration
 *     GetObjectPresignRequest getObjectPresignRequest =
 *         GetObjectPresignRequest.builder()
 *                                .signatureDuration(Duration.ofMinutes(10))
 *                                .getObjectRequest(request)
 *                                .build();
 *
 *     // Generate the presigned request
 *     PresignedGetObjectRequest presignedGetObjectRequest =
 *         presigner.presignGetObject(getObjectPresignRequest);
 *
 *     // Log the presigned URL, for example.
 *     System.out.println("Presigned URL: " + presignedGetObjectRequest.url());
 *
 *     // It is recommended to close the S3Presigner when it is done being used, because some credential
 *     // providers (e.g. if your AWS profile is configured to assume an STS role) require system resources
 *     // that need to be freed. If you are using one S3Presigner per application (as recommended), this
 *     // usually is not needed.
 *     presigner.close();
 * }
 * </pre>
 * <p/>
 *
 * <b>Browser Compatibility</b>
 * <p/>
 *
 * Some pre-signed requests can be executed by a web browser. These "browser compatible" pre-signed requests
 * do not require the customer to send anything other than a "host" header when performing an HTTP GET against
 * the pre-signed URL.
 * <p/>
 *
 * Whether a pre-signed request is "browser compatible" can be determined by checking the
 * {@link PresignedRequest#isBrowserCompatible()} flag. It is recommended to always check this flag when the pre-signed
 * request needs to be executed by a browser, because some request fields will result in the pre-signed request not
 * being browser-compatible.
 */
@SdkPublicApi
@Immutable
@ThreadSafe
public interface S3Presigner extends SdkPresigner {
    /**
     * Create an {@link S3Presigner} with default configuration. The region will be loaded from the
     * {@link DefaultAwsRegionProviderChain} and credentials will be loaded from the {@link DefaultCredentialsProvider}.
     */
    static S3Presigner create() {
        return builder().build();
    }

    static Builder builder() {
        return DefaultS3Presigner.builder();
    }

    PresignedGetObjectRequest presignGetObject(GetObjectPresignRequest request);

    default PresignedGetObjectRequest presignGetObject(Consumer<GetObjectPresignRequest.Builder> request) {
        GetObjectPresignRequest.Builder builder = GetObjectPresignRequest.builder();
        request.accept(builder);
        return presignGetObject(builder.build());
    }

    @SdkPublicApi
    @NotThreadSafe
    interface Builder extends SdkPresigner.Builder {
        Builder region(Region region);

        Builder credentialsProvider(AwsCredentialsProvider credentialsProvider);

        Builder endpointOverride(URI endpointOverride);

        S3Presigner build();
    }
}
