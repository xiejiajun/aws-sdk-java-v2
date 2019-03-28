package software.amazon.awssdk.enhanced.dynamodb.internal.model;

import java.util.LinkedHashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ItemAttributeValueConverterChain;
import software.amazon.awssdk.enhanced.dynamodb.model.ConvertableItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedRequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedResponseItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.ResponseItem;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@SdkInternalApi
@ThreadSafe
public class DefaultGeneratedItem extends DefaultItem<AttributeValue> implements GeneratedRequestItem, GeneratedResponseItem {
    private final ItemAttributeValueConverter converter;

    private DefaultGeneratedItem(Builder builder) {
        super(builder);
        this.converter = ItemAttributeValueConverterChain.create(converters());
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
        ItemAttributeValue attributeValue = ItemAttributeValue.fromGeneratedItem(attributes());

        Map<String, ConvertableItemAttributeValue> result = new LinkedHashMap<>();
        attributeValue.asMap().forEach((k, v) -> result.put(k, toConvertableAttribute(k, v)));
        return result;
    }

    private ConvertableItemAttributeValue toConvertableAttribute(String key, ItemAttributeValue value) {
        return DefaultConvertableItemAttributeValue.builder()
                                                   .conversionContext(cc -> cc.attributeName(key)
                                                                              .converter(converter))
                                                   .attributeValue(value)
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
