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
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.regions.providers.LazyAwsRegionProvider;
import software.amazon.awssdk.utils.Validate;

public final class S3Presigner {

    private final Region region;
    private final AwsCredentialsProvider credentialsProvider;
    private final URL endpointOverride;

    private S3Presigner(BuilderImpl b) {
        this.region = Validate.notNull(b.region, "region");
        this.credentialsProvider = Validate.notNull(b.credentialsProvider, "credentialsProvider");
        this.endpointOverride = b.endpointOverride;
    }

    public Region region() {
        return region;
    }

    public AwsCredentialsProvider credentialsProvider() {
        return credentialsProvider;
    }

    public URL endpointOverride() {
        return endpointOverride;
    }

    public static S3Presigner create() {
        return new BuilderImpl().region(new LazyAwsRegionProvider(DefaultAwsRegionProviderChain::new).getRegion())
                                .credentialsProvider(DefaultCredentialsProvider.create())
                                .build();
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    interface Builder {
        Builder region(Region region);

        Builder credentialsProvider(AwsCredentialsProvider awsCredentialsProvider);

        Builder endpointOverride(URL endpointOverride);

        S3Presigner build();
    }

    public static final class BuilderImpl implements Builder {

        private Region region;
        private AwsCredentialsProvider credentialsProvider;
        private URL endpointOverride;

        private BuilderImpl() {}

        @Override
        public Builder region(Region region) {
            this.region = region;
            return this;
        }

        @Override
        public Builder credentialsProvider(AwsCredentialsProvider awsCredentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        @Override
        public Builder endpointOverride(URL endpointOverride) {
            this.endpointOverride = endpointOverride;
            return this;
        }

        public S3Presigner build() {
            return new S3Presigner(this);
        }
    }
}
