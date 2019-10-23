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

import static java.util.stream.Collectors.toMap;
import static software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute.PRESIGNER_EXPIRATION;
import static software.amazon.awssdk.utils.CollectionUtils.mergeLists;
import static software.amazon.awssdk.utils.FunctionalUtils.invokeSafely;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.signer.AwsS3V4Signer;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.awscore.AwsExecutionAttribute;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.awscore.client.builder.AwsDefaultClientBuilder;
import software.amazon.awssdk.awscore.endpoint.DefaultServiceEndpointBuilder;
import software.amazon.awssdk.awscore.presigner.DefaultSdkPresigner;
import software.amazon.awssdk.awscore.presigner.PresignRequest;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.client.builder.SdkDefaultClientBuilder;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.interceptor.ClasspathInterceptorChainFactory;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptorChain;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.signer.Presigner;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.protocols.xml.AwsS3ProtocolFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.transform.GetObjectRequestMarshaller;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultS3Presigner extends DefaultSdkPresigner implements S3Presigner {
    private static final AwsS3V4Signer SIGNER = AwsS3V4Signer.create();
    private static final String SERVICE_NAME = "s3";
    private static final String SIGNING_NAME = "s3";

    private final List<ExecutionInterceptor> clientInterceptors;
    private final GetObjectRequestMarshaller getObjectRequestMarshaller;

    private DefaultS3Presigner(Builder b) {
        super(b);
        this.clientInterceptors = initializeInterceptors();
        this.getObjectRequestMarshaller = initializeGetObjectRequestMarshaller();
    }

    public static S3Presigner.Builder builder() {
        return new Builder();
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

    /**
     * Copied from {@code DefaultS3Client}.
     */
    private GetObjectRequestMarshaller initializeGetObjectRequestMarshaller() {
        // Copied from DefaultS3Client#init
        AwsS3ProtocolFactory protocolFactory = AwsS3ProtocolFactory.builder()
                                                                   .clientConfiguration(createClientConfiguration())
                                                                   .build();
        // Copied from DefaultS3Client#getObject
        return new GetObjectRequestMarshaller(protocolFactory);
    }

    /**
     * Copied from {@link AwsDefaultClientBuilder}.
     */
    private SdkClientConfiguration createClientConfiguration() {
        return SdkClientConfiguration.builder()
                                     .option(SdkClientOption.ENDPOINT, resolveEndpoint())
                                     .build();
    }

    private URI resolveEndpoint() {
        if (endpointOverride != null) {
            return endpointOverride;
        }

        // TODO: Is this API actually protected? It's pretty gross.
        return new DefaultServiceEndpointBuilder(SERVICE_NAME, "https")
            .withRegion(region)
            .getServiceEndpoint();
    }

    @Override
    public PresignedGetObjectRequest presignGetObject(GetObjectPresignRequest request) {
        return presign(PresignedGetObjectRequest.builder(), request, request.getObjectRequest(), "GetObject").build();
    }

    private <T extends PresignedRequest.Builder> T presign(T presignedRequest,
                                                           PresignRequest presignRequest,
                                                           SdkRequest requestToPresign,
                                                           String operationName) {
        ExecutionContext execCtx = createExecutionContext(presignRequest, requestToPresign, operationName);

        execCtx.interceptorChain().beforeExecution(execCtx.interceptorContext(), execCtx.executionAttributes());
        execCtx.interceptorContext(execCtx.interceptorChain().modifyRequest(execCtx.interceptorContext(),
                                                                            execCtx.executionAttributes()));
        marshalRequestAndUpdateContext(execCtx);
        execCtx.interceptorContext(execCtx.interceptorChain().modifyHttpRequestAndHttpContent(execCtx.interceptorContext(),
                                                                                              execCtx.executionAttributes()));

        SdkHttpFullRequest finalizedRequest = (SdkHttpFullRequest) execCtx.interceptorContext().httpRequest();
        Optional<RequestBody> bodyFromInterceptor = execCtx.interceptorContext().requestBody();
        if (bodyFromInterceptor.isPresent()) {
            finalizedRequest = finalizedRequest.toBuilder()
                                               .contentStreamProvider(bodyFromInterceptor.get().contentStreamProvider())
                                               .build();
        }

        Presigner presigner = Validate.isInstanceOf(Presigner.class, execCtx.signer(),
                                                    "Configured signer (%s) does not support presigning (must implement %s).",
                                                    execCtx.signer().getClass(), Presigner.class);

        SdkHttpFullRequest signedHttpRequest = presigner.presign(finalizedRequest, execCtx.executionAttributes());

        SdkBytes signedPayload = signedHttpRequest.contentStreamProvider()
                                                  .map(p -> SdkBytes.fromInputStream(p.newStream()))
                                                  .orElse(null);

        // TODO: This only works with SigV4
        List<String> signedHeadersQueryParam = signedHttpRequest.rawQueryParameters().get("X-Amz-SignedHeaders");
        Validate.validState(signedHeadersQueryParam != null,
                            "Only SigV4 presigning is supported at this time, but the configured "
                            + "presigner (%s) did not generate a SigV4 signature.", presigner.getClass());
        Map<String, List<String>> signedHeaders =
            signedHeadersQueryParam.stream()
                                   .flatMap(h -> Stream.of(h.split(";")))
                                   .collect(toMap(h -> h, h -> signedHttpRequest.firstMatchingHeader(h)
                                                                               .map(Collections::singletonList)
                                                                               .orElseGet(ArrayList::new)));

        boolean isBrowserCompatible = signedHttpRequest.method() == SdkHttpMethod.GET &&
                                      signedPayload == null &&
                                      (signedHeaders.isEmpty() ||
                                       (signedHeaders.size() == 1 && signedHeaders.containsKey("host")));

        presignedRequest.expiration(execCtx.executionAttributes().getAttribute(PRESIGNER_EXPIRATION))
                        .url(invokeSafely(signedHttpRequest.getUri()::toURL))
                        .isBrowserCompatible(isBrowserCompatible)
                        .httpRequest(signedHttpRequest)
                        .signedHeaders(signedHeaders)
                        .signedPayload(signedPayload);

        return presignedRequest;
    }

    private void marshalRequestAndUpdateContext(ExecutionContext execCtx) {
        GetObjectRequest getObjectRequest = Validate.isInstanceOf(GetObjectRequest.class, execCtx.interceptorContext().request(),
                                                                  "Interceptor modified request type from a GetObjectRequest.");

        execCtx.interceptorChain().beforeMarshalling(execCtx.interceptorContext(), execCtx.executionAttributes());
        SdkHttpFullRequest marshalledRequest = getObjectRequestMarshaller.marshall(getObjectRequest);
        execCtx.interceptorChain().afterMarshalling(execCtx.interceptorContext(), execCtx.executionAttributes());

        SdkHttpFullRequest marshalledRequestWithRequestOverrides =
            marshalledRequest.toBuilder()
                             .applyMutation(b -> addRequestHeaders(b, getObjectRequest))
                             .applyMutation(b -> addRequestQueryParams(b,  getObjectRequest))
                             .build();

        execCtx.interceptorContext(execCtx.interceptorContext().copy(r -> r.httpRequest(marshalledRequestWithRequestOverrides)));
    }

    private void addRequestHeaders(SdkHttpRequest.Builder builder, GetObjectRequest request) {
        request.overrideConfiguration().ifPresent(overrideConfig -> {
            if (!overrideConfig.headers().isEmpty()) {
                overrideConfig.headers().forEach(builder::putHeader);
            }
        });
    }

    private void addRequestQueryParams(SdkHttpRequest.Builder builder, GetObjectRequest request) {
        request.overrideConfiguration().ifPresent(overrideConfig -> {
            if (!overrideConfig.rawQueryParameters().isEmpty()) {
                overrideConfig.rawQueryParameters().forEach(builder::putRawQueryParameter);
            }
        });
    }

    /**
     * Copied from {@code AwsClientHandlerUtils#createExecutionContext}.
     */
    private ExecutionContext createExecutionContext(PresignRequest presignRequest, SdkRequest sdkRequest, String operationName) {
        AwsCredentialsProvider clientCredentials = credentialsProvider;
        AwsCredentialsProvider credentialsProvider = sdkRequest.overrideConfiguration()
                                                               .filter(c -> c instanceof AwsRequestOverrideConfiguration)
                                                               .map(c -> (AwsRequestOverrideConfiguration) c)
                                                               .flatMap(AwsRequestOverrideConfiguration::credentialsProvider)
                                                               .orElse(clientCredentials);

        Signer signer = sdkRequest.overrideConfiguration().flatMap(RequestOverrideConfiguration::signer).orElse(SIGNER);
        Instant signatureExpiration = Instant.now().plus(presignRequest.signatureDuration());

        AwsCredentials credentials = credentialsProvider.resolveCredentials();
        Validate.validState(credentials != null, "Credential providers must never return null.");

        ExecutionAttributes executionAttributes = new ExecutionAttributes()
                .putAttribute(AwsSignerExecutionAttribute.AWS_CREDENTIALS, credentials)
                .putAttribute(AwsSignerExecutionAttribute.SERVICE_SIGNING_NAME, SIGNING_NAME)
                .putAttribute(AwsExecutionAttribute.AWS_REGION, region)
                .putAttribute(AwsSignerExecutionAttribute.SIGNING_REGION, region)
                .putAttribute(SdkInternalExecutionAttribute.IS_FULL_DUPLEX, false)
                .putAttribute(SdkExecutionAttribute.CLIENT_TYPE, ClientType.SYNC)
                .putAttribute(SdkExecutionAttribute.SERVICE_NAME, SERVICE_NAME)
                .putAttribute(SdkExecutionAttribute.OPERATION_NAME, operationName)
                .putAttribute(AwsSignerExecutionAttribute.SERVICE_CONFIG, S3Configuration.builder()
                                                                                         .checksumValidationEnabled(false)
                                                                                         .build())
                .putAttribute(PRESIGNER_EXPIRATION, signatureExpiration);

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
        private URI endpointOverride;

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
        public Builder endpointOverride(URI endpointOverride) {
            super.endpointOverride(endpointOverride);
            return this;
        }

        @Override
        public S3Presigner build() {
            return new DefaultS3Presigner(this);
        }
    }
}
