package software.amazon.awssdk.enhanced.dynamodb.model;

import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.model.DefaultResponseItem;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface ResponseItem extends ConverterAwareItem,
                                      Item<ConvertableItemAttributeValue>,
                                      ToCopyableBuilder<ResponseItem.Builder, ResponseItem> {
    static Builder builder() {
        return DefaultResponseItem.builder();
    }

    interface Builder extends ConverterAwareItem.Builder,
                              Item.Builder<ConvertableItemAttributeValue>,
                              CopyableBuilder<ResponseItem.Builder, ResponseItem> {
        @Override
        Builder addConverter(ItemAttributeValueConverter converter);

        @Override
        Builder clearConverters();

        @Override
        Builder putAttribute(String attributeKey, ConvertableItemAttributeValue attributeValue);

        @Override
        Builder removeAttribute(String attributeKey);

        ResponseItem build();
    }
}
