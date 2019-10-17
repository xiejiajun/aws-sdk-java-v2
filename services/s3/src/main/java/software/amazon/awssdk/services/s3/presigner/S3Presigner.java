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
import java.util.Optional;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.regions.providers.LazyAwsRegionProvider;
import software.amazon.awssdk.utils.Validate;

/**
 * Client that can be used to generate presigned requests for S3.
 */
@SdkPublicApi
public final class S3Presigner {

    private final Region region;
    private final AwsCredentialsProvider credentialsProvider;
    private final URL endpointOverride;

    private S3Presigner(BuilderImpl b) {
        this.region = Validate.notNull(b.region, "region");
        this.credentialsProvider = Validate.notNull(b.credentialsProvider, "credentialsProvider");
        this.endpointOverride = b.endpointOverride;
    }

    /**
     * The AWS region associated with this presigner. Will be used to determine the endpoint and signing region for the
     * presigned requests if no overrides are additionally specified.
     */
    public Region region() {
        return region;
    }

    /**
     * The AWS credentials provider to generate credentials from to be included with the presigned request. When the
     * presigned request is eventually executed, these are the credentials that will be used for the actual service
     * call.
     */
    public AwsCredentialsProvider credentialsProvider() {
        return credentialsProvider;
    }

    /**
     * If provided, will override the endpoint used in all generated presigned requests. If not provided, the
     * endpoint will be the default based on the region.
     */
    public Optional<URL> endpointOverride() {
        return Optional.ofNullable(endpointOverride);
    }

    /**
     * Constructs a {@link Builder} object initialized with values of the properties of this object.
     */
    public Builder toBuilder() {
        return builder().endpointOverride(this.endpointOverride)
                        .credentialsProvider(this.credentialsProvider)
                        .region(this.region);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        S3Presigner that = (S3Presigner) o;

        if (! region.equals(that.region)) {
            return false;
        }
        if (! credentialsProvider.equals(that.credentialsProvider)) {
            return false;
        }
        return endpointOverride != null ? endpointOverride.equals(that.endpointOverride) : that.endpointOverride == null;
    }

    @Override
    public int hashCode() {
        int result = region.hashCode();
        result = 31 * result + credentialsProvider.hashCode();
        result = 31 * result + (endpointOverride != null ? endpointOverride.hashCode() : 0);
        return result;
    }

    /**
     * Constructs a new instance of an {@link S3Presigner} using default chains to determine the values for required
     * properties such as the region and credentials provider.
     */
    public static S3Presigner create() {
        return new BuilderImpl().region(new LazyAwsRegionProvider(DefaultAwsRegionProviderChain::new).getRegion())
                                .credentialsProvider(DefaultCredentialsProvider.create())
                                .build();
    }

    /**
     * Constructs a newly initialized {@link Builder} object.
     */
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
            this.credentialsProvider = awsCredentialsProvider;
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
