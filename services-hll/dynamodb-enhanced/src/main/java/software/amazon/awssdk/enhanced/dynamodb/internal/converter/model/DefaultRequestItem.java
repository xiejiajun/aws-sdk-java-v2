package software.amazon.awssdk.enhanced.dynamodb.internal.converter.model;

import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ItemAttributeValueConverterChain;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedRequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.RequestItem;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DefaultRequestItem extends DefaultItem<Object> implements RequestItem {
    private DefaultRequestItem(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public GeneratedRequestItem toGeneratedRequestItem() {
        Map<String, ItemAttributeValue> convertedItem = convertItemAttributes();
        Map<String, AttributeValue> generatedItem = toGeneratedItem(convertedItem);

        return GeneratedRequestItem.builder()
                                   .putAttributes(generatedItem)
                                   .build();
    }

    private Map<String, ItemAttributeValue> convertItemAttributes() {
        ItemAttributeValueConverter converter = ItemAttributeValueConverterChain.create(converters());

        Map<String, ItemAttributeValue> result = new LinkedHashMap<>();
        attributes().forEach((key, value) -> result.put(key, toItemAttributeValue(converter, key, value)));
        return result;
    }

    private ItemAttributeValue toItemAttributeValue(ItemAttributeValueConverter converter, String key, Object value) {
        return converter.toAttributeValue(value, ConversionContext.builder()
                                                                  .attributeName(key)
                                                                  .converter(converter)
                                                                  .build());
    }

    private Map<String, AttributeValue> toGeneratedItem(Map<String, ItemAttributeValue> convertedItem) {
        Map<String, AttributeValue> result = new LinkedHashMap<>();
        convertedItem.forEach((key, value) -> result.put(key, value.toGeneratedAttributeValue()));
        return result;
    }

    @Override
    public DefaultRequestItem.Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder extends DefaultItem.Builder<Object, Builder> implements RequestItem.Builder {
        private Builder() {}

        private Builder(DefaultRequestItem item) {
            super(item);
        }

        @Override
        public DefaultRequestItem build() {
            return new DefaultRequestItem(this);
        }
    }
}
