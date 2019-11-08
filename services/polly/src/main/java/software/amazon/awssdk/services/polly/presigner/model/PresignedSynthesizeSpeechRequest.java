package software.amazon.awssdk.services.polly.presigner.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class PresignedSynthesizeSpeechRequest
        extends PresignedRequest
        implements ToCopyableBuilder<PresignedSynthesizeSpeechRequest.Builder, PresignedSynthesizeSpeechRequest> {


    private PresignedSynthesizeSpeechRequest(BuilderImpl builder) {
        super(builder);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public interface Builder extends PresignedRequest.Builder,
            CopyableBuilder<Builder, PresignedSynthesizeSpeechRequest> {

        @Override
        Builder expiration(Instant expiration);

        @Override
        Builder isBrowserExecutable(Boolean isBrowserExecutable);

        @Override
        Builder signedHeaders(Map<String, List<String>> signedHeaders);

        @Override
        Builder signedPayload(SdkBytes signedPayload);

        @Override
        Builder httpRequest(SdkHttpRequest httpRequest);

        @Override
        PresignedSynthesizeSpeechRequest build();
    }

    private static class BuilderImpl extends PresignedRequest.DefaultBuilder<BuilderImpl> implements Builder {

        public BuilderImpl() {
        }

        public BuilderImpl(PresignedSynthesizeSpeechRequest presignedSynthesizeSpeechRequest) {
            super(presignedSynthesizeSpeechRequest);
        }

        @Override
        public PresignedSynthesizeSpeechRequest build() {
            return new PresignedSynthesizeSpeechRequest(this);
        }
    }
}
