package software.amazon.awssdk.enhanced.dynamodb.internal.converter.model;

import java.util.LinkedHashMap;
import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ItemAttributeValueConverterChain;
import software.amazon.awssdk.enhanced.dynamodb.model.ConvertableItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedRequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedResponseItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.ResponseItem;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DefaultGeneratedItem extends DefaultItem<AttributeValue> implements GeneratedRequestItem, GeneratedResponseItem {
    private DefaultGeneratedItem(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ResponseItem toResponseItem() {
        return ResponseItem.builder()
                           .putAttributes(toConvertableAttributes())
                           .build();
    }

    private Map<String, ConvertableItemAttributeValue> toConvertableAttributes() {
        Map<String, ConvertableItemAttributeValue> result = new LinkedHashMap<>();
        attributes().forEach((key, value) -> result.put(key, toConvertableAttribute(key, value)));
        return result;
    }

    private ConvertableItemAttributeValue toConvertableAttribute(String key, AttributeValue value) {
        ItemAttributeValueConverterChain converterChain = ItemAttributeValueConverterChain.create(converters());
        return DefaultConvertableItemAttributeValue.builder()
                                                   .conversionContext(cc -> cc.converter(converterChain).attributeName(key))
                                                   .attributeValue(ItemAttributeValue.fromGeneratedAttributeValue(value))
                                                   .build();
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder
            extends DefaultItem.Builder<AttributeValue, Builder>
            implements GeneratedRequestItem.Builder, GeneratedResponseItem.Builder {
        private Builder() {}

        private Builder(DefaultGeneratedItem item) {
            super(item);
        }

        @Override
        public DefaultGeneratedItem build() {
            return new DefaultGeneratedItem(this);
        }
    }
}
