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

import java.net.URI;
import java.net.URL;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.regions.providers.LazyAwsRegionProvider;
import software.amazon.awssdk.utils.IoUtils;

@SdkInternalApi
public abstract class DefaultSdkPresigner implements SdkPresigner {
    private static final LazyAwsRegionProvider DEFAULT_REGION_PROVIDER =
            new LazyAwsRegionProvider(DefaultAwsRegionProviderChain::new);

    protected final Region region;
    protected final AwsCredentialsProvider credentialsProvider;
    protected final URI endpointOverride;

    protected DefaultSdkPresigner(Builder b) {
        this.region = b.region != null ? b.region : DEFAULT_REGION_PROVIDER.getRegion();
        this.credentialsProvider = b.credentialsProvider != null ? b.credentialsProvider : DefaultCredentialsProvider.create();
        this.endpointOverride = b.endpointOverride;
    }

    @Override
    public void close() {
        IoUtils.closeIfCloseable(credentialsProvider, null);
    }

    public abstract static class Builder implements SdkPresigner.Builder {
        private Region region;
        private AwsCredentialsProvider credentialsProvider;
        private URI endpointOverride;

        protected Builder() {}

        @Override
        public Builder region(Region region) {
            this.region = region;
            return this;
        }

        @Override
        public Builder credentialsProvider(AwsCredentialsProvider credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            return this;
        }

        @Override
        public Builder endpointOverride(URI endpointOverride) {
            this.endpointOverride = endpointOverride;
            return this;
        }
    }
}
