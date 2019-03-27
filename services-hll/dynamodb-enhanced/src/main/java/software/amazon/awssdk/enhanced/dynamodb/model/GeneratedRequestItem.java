package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.model.DefaultGeneratedItem;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface GeneratedRequestItem extends Item<AttributeValue>,
                                              ToCopyableBuilder<GeneratedRequestItem.Builder, GeneratedRequestItem> {
    static Builder builder() {
        return DefaultGeneratedItem.builder();
    }

    interface Builder extends Item.Builder<AttributeValue>,
                              CopyableBuilder<GeneratedRequestItem.Builder, GeneratedRequestItem>{
        @Override
        Builder putAttributes(Map<String, AttributeValue> attributeValues);

        @Override
        Builder putAttribute(String attributeKey, AttributeValue attributeValue);

        @Override
        Builder removeAttribute(String attributeKey);

        GeneratedRequestItem build();
    }
}
