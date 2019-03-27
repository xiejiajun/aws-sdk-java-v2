package software.amazon.awssdk.enhanced.dynamodb.converter;

import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ConversionContext implements ToCopyableBuilder<ConversionContext.Builder, ConversionContext> {
    private final String attributeName;
    private final ItemAttributeValueConverter converter;

    private ConversionContext(Builder builder) {
        this.attributeName = Validate.paramNotNull(builder.attributeName, "attributeName");
        this.converter = Validate.paramNotNull(builder.converter, "converter");
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * The name of the attribute being converted.
     */
    public String attributeName() {
        return this.attributeName;
    }

    public ItemAttributeValueConverter converter() {
        return converter;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public final static class Builder implements CopyableBuilder<ConversionContext.Builder, ConversionContext> {
        private String attributeName;
        private ItemAttributeValueConverter converter;

        private Builder() {}

        public Builder(ConversionContext context) {
            this.attributeName = context.attributeName;
        }

        public Builder attributeName(String attributeName) {
            this.attributeName = attributeName;
            return this;
        }

        public Builder converter(ItemAttributeValueConverter converter) {
            this.converter = converter;
            return this;
        }

        public ConversionContext build() {
            return new ConversionContext(this);
        }
    }
}
