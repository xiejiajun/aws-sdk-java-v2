package software.amazon.awssdk.enhanced.dynamodb.internal.converter;

import java.util.Optional;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.utils.Validate;

public class DefaultConversionContext implements ConversionContext {
    private final String attributeName;
    private final ItemAttributeValueConverter converter;

    private DefaultConversionContext(DefaultConversionContext.Builder builder) {
        this.attributeName = builder.attributeName;
        this.converter = Validate.paramNotNull(builder.converter, "converter");
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * The name of the attribute being converted.
     */
    public Optional<String> attributeName() {
        return Optional.ofNullable(this.attributeName);
    }

    public ItemAttributeValueConverter converter() {
        return converter;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public final static class Builder implements ConversionContext.Builder {
        private String attributeName;
        private ItemAttributeValueConverter converter;

        private Builder() {}

        public Builder(DefaultConversionContext context) {
            this.attributeName = context.attributeName;
            this.converter = context.converter;
        }

        public ConversionContext.Builder attributeName(String attributeName) {
            this.attributeName = attributeName;
            return this;
        }

        public ConversionContext.Builder converter(ItemAttributeValueConverter converter) {
            this.converter = converter;
            return this;
        }

        public ConversionContext build() {
            return new DefaultConversionContext(this);
        }
    }
}
