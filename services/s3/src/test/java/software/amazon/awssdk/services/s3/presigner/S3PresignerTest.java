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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.net.URL;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;

@RunWith(MockitoJUnitRunner.class)
public class S3PresignerTest {
    private static final URL FAKE_URL;

    static {
        try {
            FAKE_URL = new URL("https://localhost");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Mock
    private AwsCredentialsProvider mockAwsCredentialsProvider;

    private S3Presigner generateMaximal() {
        return S3Presigner.builder()
                          .credentialsProvider(mockAwsCredentialsProvider)
                          .region(Region.US_EAST_1)
                          .endpointOverride(FAKE_URL)
                          .build();
    }

    private S3Presigner generateMinimal() {
        return S3Presigner.builder()
                          .credentialsProvider(mockAwsCredentialsProvider)
                          .region(Region.US_EAST_1)
                          .build();
    }

    @Test
    public void build_allProperties() {
        S3Presigner s3Presigner = generateMaximal();

        assertThat(s3Presigner.credentialsProvider()).isEqualTo(mockAwsCredentialsProvider);
        assertThat(s3Presigner.region()).isEqualTo(Region.US_EAST_1);
        assertThat(s3Presigner.endpointOverride()).isEqualTo(Optional.of(FAKE_URL));
    }

    @Test
    public void build_minimalProperties() {
        S3Presigner s3Presigner = generateMinimal();

        assertThat(s3Presigner.credentialsProvider()).isEqualTo(mockAwsCredentialsProvider);
        assertThat(s3Presigner.region()).isEqualTo(Region.US_EAST_1);
        assertThat(s3Presigner.endpointOverride()).isEmpty();
    }

    @Test
    public void build_missingRegion() {
        assertThatThrownBy(() -> S3Presigner.builder()
                                            .credentialsProvider(mockAwsCredentialsProvider)
                                            .build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("region");
    }

    @Test
    public void build_missingCredentialsProvider() {
        assertThatThrownBy(() -> S3Presigner.builder()
                                            .region(Region.US_EAST_1)
                                            .build())
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("credentialsProvider");
    }

    @Test
    public void toBuilder() {
        S3Presigner s3Presigner = generateMaximal();

        S3Presigner otherS3Presigner = s3Presigner.toBuilder().build();

        assertThat(otherS3Presigner.endpointOverride()).isEqualTo(Optional.of(FAKE_URL));
        assertThat(otherS3Presigner.credentialsProvider()).isEqualTo(mockAwsCredentialsProvider);
        assertThat(otherS3Presigner.region()).isEqualTo(Region.US_EAST_1);
    }

    @Test
    public void equalsAndHashCode_maximal() {
        S3Presigner s3Presigner = generateMaximal();
        S3Presigner otherS3Presigner = generateMaximal();

        assertThat(s3Presigner).isEqualTo(otherS3Presigner);
        assertThat(s3Presigner.hashCode()).isEqualTo(otherS3Presigner.hashCode());
    }

    @Test
    public void equalsAndHashCode_minimal() {
        S3Presigner s3Presigner = generateMinimal();
        S3Presigner otherS3Presigner = generateMinimal();

        assertThat(s3Presigner).isEqualTo(otherS3Presigner);
        assertThat(s3Presigner.hashCode()).isEqualTo(otherS3Presigner.hashCode());
    }

    @Test
    public void equalsAndHashCode_notEqual_credentialsProvider() {
        AwsCredentialsProvider otherCredentialsProvider = mock(AwsCredentialsProvider.class);
        S3Presigner s3Presigner = generateMaximal();
        S3Presigner otherS3Presigner = s3Presigner.toBuilder().credentialsProvider(otherCredentialsProvider).build();

        assertThat(s3Presigner).isNotEqualTo(otherS3Presigner);
        assertThat(s3Presigner.hashCode()).isNotEqualTo(otherS3Presigner.hashCode());
    }

    @Test
    public void equalsAndHashCode_notEqual_region() {
        S3Presigner s3Presigner = generateMaximal();
        S3Presigner otherS3Presigner = s3Presigner.toBuilder().region(Region.US_WEST_2).build();

        assertThat(s3Presigner).isNotEqualTo(otherS3Presigner);
        assertThat(s3Presigner.hashCode()).isNotEqualTo(otherS3Presigner.hashCode());
    }

    @Test
    public void equalsAndHashCode_notEqual_endpointOverride() throws Exception {
        URL otherUrl = new URL("https://foo.bar");
        S3Presigner s3Presigner = generateMaximal();
        S3Presigner otherS3Presigner = s3Presigner.toBuilder().endpointOverride(otherUrl).build();

        assertThat(s3Presigner).isNotEqualTo(otherS3Presigner);
        assertThat(s3Presigner.hashCode()).isNotEqualTo(otherS3Presigner.hashCode());
    }
}