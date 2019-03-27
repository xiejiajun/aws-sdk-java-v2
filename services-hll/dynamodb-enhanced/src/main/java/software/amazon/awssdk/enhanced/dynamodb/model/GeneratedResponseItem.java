package software.amazon.awssdk.enhanced.dynamodb.model;

import software.amazon.awssdk.enhanced.dynamodb.internal.converter.model.DefaultGeneratedItem;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public interface GeneratedResponseItem extends Item<AttributeValue> {
    static GeneratedResponseItem.Builder builder() {
        return DefaultGeneratedItem.builder();
    }

    interface Builder extends Item.Builder<AttributeValue> {
        @Override
        Builder putAttribute(String attributeKey, AttributeValue attributeValue);

        @Override
        Builder removeAttribute(String attributeKey);

        GeneratedResponseItem build();
    }
}
