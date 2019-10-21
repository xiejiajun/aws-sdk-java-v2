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

package software.amazon.awssdk.services.s3.internal.presigner;

import java.net.URL;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.regions.providers.LazyAwsRegionProvider;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultS3Presigner implements S3Presigner {
    private static final LazyAwsRegionProvider DEFAULT_REGION_PROVIDER =
            new LazyAwsRegionProvider(DefaultAwsRegionProviderChain::new);

    private final Region region;
    private final AwsCredentialsProvider credentialsProvider;
    private final URL endpointOverride;

    private DefaultS3Presigner(Builder b) {
        this.region = Validate.notNull(b.region, "region");
        this.credentialsProvider = Validate.notNull(b.credentialsProvider, "credentialsProvider");
        this.endpointOverride = b.endpointOverride;
    }

    public static S3Presigner create() {
        return new Builder().region(DEFAULT_REGION_PROVIDER.getRegion())
                            .credentialsProvider(DefaultCredentialsProvider.create())
                            .build();
    }

    public static S3Presigner.Builder builder() {
        return new Builder();
    }

    public static final class Builder implements S3Presigner.Builder {
        private Region region;
        private AwsCredentialsProvider credentialsProvider;
        private URL endpointOverride;

        private Builder() {}

        @Override
        public S3Presigner.Builder region(Region region) {
            this.region = region;
            return this;
        }

        @Override
        public S3Presigner.Builder credentialsProvider(AwsCredentialsProvider credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        @Override
        public S3Presigner.Builder endpointOverride(URL endpointOverride) {
            this.endpointOverride = endpointOverride;
            return this;
        }

        public S3Presigner build() {
            return new DefaultS3Presigner(this);
        }
    }
}
