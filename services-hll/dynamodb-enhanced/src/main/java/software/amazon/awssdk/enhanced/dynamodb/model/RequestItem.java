package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.model.DefaultRequestItem;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@ThreadSafe
public interface RequestItem extends ConverterAware,
                                     AttributeAware<Object>,
                                     ToCopyableBuilder<RequestItem.Builder, RequestItem> {
    static Builder builder() {
        return DefaultRequestItem.builder();
    }

    GeneratedRequestItem toGeneratedRequestItem();

    @NotThreadSafe
    interface Builder extends ConverterAware.Builder,
                              AttributeAware.Builder<Object>,
                              CopyableBuilder<RequestItem.Builder, RequestItem> {
        @Override
        Builder addConverters(Collection<? extends ItemAttributeValueConverter> converters);

        @Override
        Builder addConverter(ItemAttributeValueConverter converter);

        @Override
        Builder clearConverters();

        @Override
        Builder putAttributes(Map<String, Object> attributeValues);

        @Override
        Builder putAttribute(String attributeKey, Object attributeValue);

        default Builder putAttribute(String attributeKey, Consumer<RequestItem.Builder> subItemAttribute) {
            RequestItem.Builder requestItemBuilder = RequestItem.builder();
            subItemAttribute.accept(requestItemBuilder);
            return putAttribute(attributeKey, requestItemBuilder.build());
        }

        @Override
        Builder removeAttribute(String attributeKey);

        @Override
        Builder clearAttributes();

        RequestItem build();
    }
}
