package software.amazon.awssdk.enhanced.dynamodb.model;

import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.model.DefaultRequestItem;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public interface RequestItem extends ConverterAwareItem,
                                     Item<Object>,
                                     ToCopyableBuilder<RequestItem.Builder, RequestItem> {
    static Builder builder() {
        return DefaultRequestItem.builder();
    }

    interface Builder extends ConverterAwareItem.Builder,
                              Item.Builder<Object>,
                              CopyableBuilder<RequestItem.Builder, RequestItem> {
        @Override
        Builder addConverter(ItemAttributeValueConverter converter);

        @Override
        Builder clearConverters();

        @Override
        Builder putAttribute(String attributeKey, Object attributeValue);

        @Override
        Builder removeAttribute(String attributeKey);

        RequestItem build();
    }
}
