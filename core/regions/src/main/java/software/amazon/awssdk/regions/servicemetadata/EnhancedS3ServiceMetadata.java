/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package software.amazon.awssdk.regions.servicemetadata;

import java.net.URI;
import java.util.List;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.profiles.ProfileProperty;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.ServiceMetadata;
import software.amazon.awssdk.regions.ServicePartitionMetadata;
import software.amazon.awssdk.utils.Logger;

/**
 * Decorator metadata class for S3 to allow customers to opt in to using the
 * regional S3 us-east-1 endpoint instead of the legacy
 * {@code s3.amazonaws.com} when specifying the us-east-1 region.
 */
@SdkPublicApi
public final class EnhancedS3ServiceMetadata implements ServiceMetadata {
    private static final Logger log = Logger.loggerFor(EnhancedS3ServiceMetadata.class);
    private static final String REGIONAL_SETTING = "regional";
    private static final S3ServiceMetadata S3_SERVICE_METADATA = new S3ServiceMetadata();

    private volatile boolean profileSettingChecked = false;
    private volatile String profileSetting = null;

    @Override
    public URI endpointFor(Region region) {
        if (Region.US_EAST_1.equals(region) && !useUsEast1RegionalEndpoint()) {
            return URI.create("s3.amazonaws.com");
        }
        return S3_SERVICE_METADATA.endpointFor(region);
    }

    @Override
    public Region signingRegion(Region region) {
        return S3_SERVICE_METADATA.signingRegion(region);
    }

    @Override
    public List<Region> regions() {
        return S3_SERVICE_METADATA.regions();
    }

    @Override
    public List<ServicePartitionMetadata> servicePartitions() {
        return S3_SERVICE_METADATA.servicePartitions();
    }

    private boolean useUsEast1RegionalEndpoint() {
        String env = envVarSetting();

        if (env != null) {
            return REGIONAL_SETTING.equalsIgnoreCase(env);
        }

        String profile = profileFileSetting();

        if (profile != null) {
            return REGIONAL_SETTING.equalsIgnoreCase(profile);
        }

        return false;
    }

    private static String envVarSetting() {
        return SdkSystemSetting.AWS_S3_US_EAST_1_REGIONAL_ENDPOINT.getStringValue().orElse(null);
    }

    private String profileFileSetting() {
        if (!profileSettingChecked) {
            synchronized (this) {
                if (!profileSettingChecked) {
                    try {
                        String profileName = ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow();
                        ProfileFile profileFile = ProfileFile.defaultProfileFile();
                        profileSetting = profileFile.profile(profileName)
                                .flatMap(p -> p.property(ProfileProperty.S3_US_EAST_1_REGIONAL_ENDPOINT))
                                .orElse(null);
                    } catch (Throwable t) {
                        log.warn(() -> "Unable to load config file", t);
                    }
                }
                profileSettingChecked = true;
            }
        }
        return profileSetting;
    }
}
