package software.amazon.awssdk.services.polly.presigner.model;

import java.time.Duration;
import software.amazon.awssdk.awscore.presigner.PresignRequest;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class SynthesizeSpeechPresignRequest
        extends PresignRequest
        implements ToCopyableBuilder<SynthesizeSpeechPresignRequest.Builder, SynthesizeSpeechPresignRequest> {

    private final SynthesizeSpeechRequest synthesizeSpeechRequest;

    private SynthesizeSpeechPresignRequest(BuilderImpl builder) {
        super(builder);
        this.synthesizeSpeechRequest = builder.synthesizeSpeechRequest;
    }

    public SynthesizeSpeechRequest synthesizeSpeechRequest() {
        return synthesizeSpeechRequest;
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public interface Builder extends PresignRequest.Builder,
            CopyableBuilder<SynthesizeSpeechPresignRequest.Builder, SynthesizeSpeechPresignRequest> {

        Builder synthesizeSpeechRequest(SynthesizeSpeechRequest synthesizeSpeechRequest);

        Builder signatureDuration(Duration signatureDuration);

        /**
         * Build the presigned request, based on the configuration on this builder.
         */
        SynthesizeSpeechPresignRequest build();
    }

    private static class BuilderImpl extends PresignRequest.DefaultBuilder<BuilderImpl> implements Builder {
        private SynthesizeSpeechRequest synthesizeSpeechRequest;

        public BuilderImpl() {
        }

        private BuilderImpl(SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest) {
            super(synthesizeSpeechPresignRequest);
            this.synthesizeSpeechRequest = synthesizeSpeechPresignRequest.synthesizeSpeechRequest();
        }

        @Override
        public Builder synthesizeSpeechRequest(SynthesizeSpeechRequest synthesizeSpeechRequest) {
            this.synthesizeSpeechRequest = synthesizeSpeechRequest;
            return this;
        }

        @Override
        public SynthesizeSpeechPresignRequest build() {
            return new SynthesizeSpeechPresignRequest(this);
        }
    }
}
