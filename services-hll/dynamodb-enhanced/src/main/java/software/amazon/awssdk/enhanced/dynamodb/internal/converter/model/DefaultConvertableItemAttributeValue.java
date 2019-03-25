package software.amazon.awssdk.enhanced.dynamodb.internal.converter.model;

import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ItemAttributeValueConverterChain;
import software.amazon.awssdk.enhanced.dynamodb.model.ConvertableItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.utils.Validate;

public final class DefaultConvertableItemAttributeValue implements ConvertableItemAttributeValue {
    private final ItemAttributeValue attributeValue;
    private final ItemAttributeValueConverterChain converterChain;
    private final ConversionContext conversionContext;

    private DefaultConvertableItemAttributeValue(Builder builder) {
        this.attributeValue = Validate.paramNotNull(builder.attributeValue, "attributeValue");
        this.converterChain = Validate.paramNotNull(builder.converterChain, "converterChain");
        this.conversionContext = Validate.paramNotNull(builder.conversionContext, "conversionContext");
    }

    @Override
    public <T> T as(Class<T> type) {
        return converterChain.convert(attributeValue, type);
    }

    @Override
    public ItemAttributeValue rawValue() {
        return attributeValue;
    }

    public static class Builder {
        private ItemAttributeValue attributeValue;
        private ItemAttributeValueConverterChain converterChain;
        private ConversionContext conversionContext;

        private Builder() {}

        public Builder attributeValue(ItemAttributeValue attributeValue) {
            this.attributeValue = attributeValue;
            return this;
        }

        public Builder converterChain(ItemAttributeValueConverterChain converterChain) {
            this.converterChain = converterChain;
            return this;
        }

        public Builder conversionContext(ConversionContext conversionContext) {
            this.conversionContext = conversionContext;
            return this;
        }

        public DefaultConvertableItemAttributeValue build() {
            return new DefaultConvertableItemAttributeValue(this);
        }
    }
}
