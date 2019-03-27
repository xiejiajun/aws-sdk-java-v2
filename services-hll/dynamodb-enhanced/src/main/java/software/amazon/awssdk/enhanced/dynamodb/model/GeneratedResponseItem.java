package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.model.DefaultGeneratedItem;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public interface GeneratedResponseItem extends Item<AttributeValue>, ConverterAwareItem {
    static GeneratedResponseItem.Builder builder() {
        return DefaultGeneratedItem.builder();
    }

    ResponseItem toResponseItem();

    interface Builder extends Item.Builder<AttributeValue>, ConverterAwareItem.Builder {
        @Override
        Builder putAttributes(Map<String, AttributeValue> attributeValues);

        @Override
        Builder putAttribute(String attributeKey, AttributeValue attributeValue);

        @Override
        Builder removeAttribute(String attributeKey);

        @Override
        Builder addConverter(ItemAttributeValueConverter converter);

        @Override
        Builder clearConverters();

        GeneratedResponseItem build();
    }
}
