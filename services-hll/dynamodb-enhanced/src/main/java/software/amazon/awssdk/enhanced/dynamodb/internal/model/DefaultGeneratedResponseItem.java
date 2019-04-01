package software.amazon.awssdk.enhanced.dynamodb.internal.model;

import java.util.LinkedHashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.model.ConvertableItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedResponseItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.ResponseItem;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * The default implementation of {@link GeneratedResponseItem}.
 */
@SdkInternalApi
@ThreadSafe
public class DefaultGeneratedResponseItem extends DefaultItem<AttributeValue> implements GeneratedResponseItem {
    private DefaultGeneratedResponseItem(Builder builder) {
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
        ItemAttributeValue attributeValue = ItemAttributeValue.fromGeneratedItem(attributes());

        Map<String, ConvertableItemAttributeValue> result = new LinkedHashMap<>();
        attributeValue.asMap().forEach((k, v) -> result.put(k, toConvertableAttribute(k, v)));
        return result;
    }

    private ConvertableItemAttributeValue toConvertableAttribute(String key, ItemAttributeValue value) {
        return DefaultConvertableItemAttributeValue.builder()
                                                   .conversionContext(cc -> cc.attributeName(key)
                                                                              .converter(converterChain))
                                                   .attributeValue(value)
                                                   .build();
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder
            extends DefaultItem.Builder<AttributeValue, Builder>
            implements GeneratedResponseItem.Builder {
        private Builder() {}

        private Builder(DefaultGeneratedResponseItem item) {
            super(item);
        }

        @Override
        public DefaultGeneratedResponseItem build() {
            return new DefaultGeneratedResponseItem(this);
        }
    }
}
