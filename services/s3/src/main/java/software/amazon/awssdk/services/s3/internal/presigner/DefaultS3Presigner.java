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

import static software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute.PRESIGNER_EXPIRATION;
import static software.amazon.awssdk.utils.CollectionUtils.mergeLists;
import static software.amazon.awssdk.utils.FunctionalUtils.invokeSafely;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.signer.AwsS3V4Signer;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.awscore.AwsExecutionAttribute;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.awscore.presigner.DefaultSdkPresigner;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.client.builder.SdkDefaultClientBuilder;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.interceptor.ClasspathInterceptorChainFactory;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptorChain;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.protocols.xml.AwsS3ProtocolFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.transform.GetObjectRequestMarshaller;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultS3Presigner extends DefaultSdkPresigner implements S3Presigner {
    private static final AwsS3V4Signer SIGNER = AwsS3V4Signer.create();
    private static final String SERVICE_NAME = "s3";
    private static final String SIGNING_NAME = "s3";
    private static GetObjectRequestMarshaller GET_OBJECT_REQUEST_MARSHALLER = initializeGetObjectRequestMarshaller();

    private final List<ExecutionInterceptor> clientInterceptors;

    private DefaultS3Presigner(Builder b) {
        super(b);
        this.clientInterceptors = initializeInterceptors();
    }

    /**
     * Copied from {@code DefaultS3Client}.
     */
    private static GetObjectRequestMarshaller initializeGetObjectRequestMarshaller() {
        // Copied from DefaultS3Client#init
        AwsS3ProtocolFactory protocolFactory = AwsS3ProtocolFactory.builder().build();

        // Copied from DefaultS3Client#getObject
        return new GetObjectRequestMarshaller(protocolFactory);
    }

    /**
     * Copied from {@code DefaultS3BaseClientBuilder} and {@link SdkDefaultClientBuilder}.
     * @return
     */
    private List<ExecutionInterceptor> initializeInterceptors() {
        ClasspathInterceptorChainFactory interceptorFactory = new ClasspathInterceptorChainFactory();
        List<ExecutionInterceptor> s3Interceptors =
                interceptorFactory.getInterceptors("software/amazon/awssdk/services/s3/execution.interceptors");
        return mergeLists(interceptorFactory.getGlobalInterceptors(), s3Interceptors);
    }

    public static S3Presigner create() {
        return new Builder().build();
    }

    public static S3Presigner.Builder builder() {
        return new Builder();
    }

    @Override
    public PresignedGetObjectRequest presignGetObject(GetObjectPresignRequest request) {
        ExecutionContext execCtx = createExecutionContext(request.getObjectRequest(), "GetObject");

        Instant signatureExpiration = Instant.now().plus(request.signatureDuration());
        execCtx.executionAttributes().putAttribute(PRESIGNER_EXPIRATION, signatureExpiration);

        execCtx.interceptorContext(execCtx.interceptorChain().modifyRequest(execCtx.interceptorContext(),
                                                                            execCtx.executionAttributes()));

        GetObjectRequest getObjectRequest = Validate.isInstanceOf(GetObjectRequest.class, execCtx.interceptorContext().request(),
                                                                  "Interceptor modified request type from a GetObjectRequest.");

        SdkHttpFullRequest marshalledRequest = GET_OBJECT_REQUEST_MARSHALLER.marshall(getObjectRequest);

        execCtx.interceptorContext(execCtx.interceptorContext().copy(r -> r.httpRequest(marshalledRequest)));
        execCtx.interceptorContext(execCtx.interceptorChain().modifyHttpRequestAndHttpContent(execCtx.interceptorContext(),
                                                                                              execCtx.executionAttributes()));

        SdkHttpFullRequest finalizedRequest = (SdkHttpFullRequest) execCtx.interceptorContext().httpRequest();
        Optional<RequestBody> bodyFromInterceptor = execCtx.interceptorContext().requestBody();
        if (bodyFromInterceptor.isPresent()) {
            // TODO: Why does SdkHttpFullRequest.copy take a SdkHttpRequest.Builder ?
            finalizedRequest = finalizedRequest.toBuilder()
                                               .contentStreamProvider(bodyFromInterceptor.get().contentStreamProvider())
                                               .build();
        }

        SdkHttpFullRequest presignedRequest = SIGNER.presign(finalizedRequest, execCtx.executionAttributes());

        // TODO: not all of these are signed!
        Map<String, List<String>> presignedHeaders = presignedRequest.headers();

        return PresignedGetObjectRequest.builder()
                                        .expiration(signatureExpiration)
                                        .url(invokeSafely(presignedRequest.getUri()::toURL))
                                        .httpRequest(presignedRequest)
                                        .signedHeaders(presignedHeaders)
                                        .signedPayload()
                                        .build()
    }

    /**
     * Copied from {@code AwsClientHandlerUtils#createExecutionContext}.
     */
    private ExecutionContext createExecutionContext(SdkRequest sdkRequest, String operationName) {
        AwsCredentialsProvider clientCredentials = credentialsProvider;
        AwsCredentialsProvider credentialsProvider = sdkRequest.overrideConfiguration()
                                                               .filter(c -> c instanceof AwsRequestOverrideConfiguration)
                                                               .map(c -> (AwsRequestOverrideConfiguration) c)
                                                               .flatMap(AwsRequestOverrideConfiguration::credentialsProvider)
                                                               .orElse(clientCredentials);

        Signer signer = sdkRequest.overrideConfiguration().flatMap(RequestOverrideConfiguration::signer).orElse(SIGNER);

        AwsCredentials credentials = credentialsProvider.resolveCredentials();
        Validate.validState(credentials != null, "Credential providers must never return null.");

        ExecutionAttributes executionAttributes = new ExecutionAttributes()
//                .putAttribute(AwsSignerExecutionAttribute.SERVICE_CONFIG, clientConfig.option(SdkClientOption.SERVICE_CONFIGURATION))
                .putAttribute(AwsSignerExecutionAttribute.AWS_CREDENTIALS, credentials)
                .putAttribute(AwsSignerExecutionAttribute.SERVICE_SIGNING_NAME, SIGNING_NAME)
                .putAttribute(AwsExecutionAttribute.AWS_REGION, region)
                .putAttribute(AwsSignerExecutionAttribute.SIGNING_REGION, region)
                .putAttribute(SdkInternalExecutionAttribute.IS_FULL_DUPLEX, false)
                .putAttribute(SdkExecutionAttribute.CLIENT_TYPE, ClientType.SYNC)
                .putAttribute(SdkExecutionAttribute.SERVICE_NAME, SERVICE_NAME)
                .putAttribute(SdkExecutionAttribute.OPERATION_NAME, operationName);

        ExecutionInterceptorChain executionInterceptorChain = new ExecutionInterceptorChain(clientInterceptors);
        return ExecutionContext.builder()
                               .interceptorChain(executionInterceptorChain)
                               .interceptorContext(InterceptorContext.builder()
                                                                     .request(sdkRequest)
                                                                     .build())
                               .executionAttributes(executionAttributes)
                               .signer(signer)
                               .build();
    }

    public static final class Builder extends DefaultSdkPresigner.Builder implements S3Presigner.Builder {
        private Region region;
        private AwsCredentialsProvider credentialsProvider;
        private URL endpointOverride;

        private Builder() {}

        @Override
        public Builder region(Region region) {
            super.region(region);
            return this;
        }

        @Override
        public Builder credentialsProvider(AwsCredentialsProvider credentialsProvider) {
            super.credentialsProvider(credentialsProvider);
            return this;
        }

        @Override
        public Builder endpointOverride(URL endpointOverride) {
            super.endpointOverride(endpointOverride);
            return this;
        }

        @Override
        public S3Presigner build() {
            return new DefaultS3Presigner(this);
        }
    }
}
