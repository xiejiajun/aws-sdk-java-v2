package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.model.DefaultResponseItem;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface ResponseItem extends Item<ConvertableItemAttributeValue>,
                                      ToCopyableBuilder<ResponseItem.Builder, ResponseItem> {
    static Builder builder() {
        return DefaultResponseItem.builder();
    }

    interface Builder extends Item.Builder<ConvertableItemAttributeValue>,
                              CopyableBuilder<ResponseItem.Builder, ResponseItem> {
        @Override
        Builder putAttributes(Map<String, ConvertableItemAttributeValue> attributeValues);

        @Override
        Builder putAttribute(String attributeKey, ConvertableItemAttributeValue attributeValue);

        @Override
        Builder removeAttribute(String attributeKey);

        ResponseItem build();
    }
}
