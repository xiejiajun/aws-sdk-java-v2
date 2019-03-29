package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Collection;
import java.util.Map;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.model.DefaultRequestItem;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

/**
 * An item that can be sent to DynamoDB. An item is a single, unique entry in a DynamoDB table.
 *
 * A {@code ItemKey} is a {@code Map<String, Object>} that can be converted into a {@code Map<String, ItemAttributeValue>}
 * using the configured {@link #converters()}.
 *
 * @see Table
 */
@SdkPublicApi
@ThreadSafe
public interface ItemKey extends ConverterAware,
                                 ItemKeyAware<Object>,
                                 ToCopyableBuilder<ItemKey.Builder, ItemKey> {
    static Builder builder() {
        return DefaultRequestItem.builder();
    }

    GeneratedRequestItem toGeneratedRequestItem();

    @NotThreadSafe
    interface Builder extends ConverterAware.Builder,
                              ItemKeyAware.Builder<Object>,
                              CopyableBuilder<Builder, ItemKey> {
        @Override
        Builder addConverters(Collection<? extends ItemAttributeValueConverter> converters);

        @Override
        Builder addConverter(ItemAttributeValueConverter converter);

        @Override
        Builder clearConverters();

        @Override
        Builder putKeyAttributes(Map<String, Object> attributeValues);

        @Override
        Builder putKeyAttribute(String attributeKey, Object attributeValue);

        @Override
        Builder removeKeyAttribute(String attributeKey);

        @Override
        Builder clearKeyAttributes();

        ItemKey build();
    }
}
