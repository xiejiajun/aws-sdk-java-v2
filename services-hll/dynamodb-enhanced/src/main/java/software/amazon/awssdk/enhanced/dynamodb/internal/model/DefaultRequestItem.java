package software.amazon.awssdk.enhanced.dynamodb.internal.model;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ItemAttributeValueConverterChain;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedRequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.RequestItem;

@SdkInternalApi
@ThreadSafe
public class DefaultRequestItem extends DefaultItem<Object> implements RequestItem {
    private final ItemAttributeValueConverterChain converter;

    private DefaultRequestItem(Builder builder) {
        super(builder);
        this.converter = ItemAttributeValueConverterChain.create(converters());
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public GeneratedRequestItem toGeneratedRequestItem() {
        ItemAttributeValue itemAttributeValue = converter.toAttributeValue(this, ConversionContext.builder()
                                                                                                  .converter(converter)
                                                                                                  .build());
        return GeneratedRequestItem.builder()
                                   .putAttributes(itemAttributeValue.toGeneratedItem())
                                   .build();
    }

    @Override
    public DefaultRequestItem.Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder extends DefaultItem.Builder<Object, Builder> implements RequestItem.Builder {
        private Builder() {}

        private Builder(DefaultRequestItem item) {
            super(item);
        }

        @Override
        public DefaultRequestItem build() {
            return new DefaultRequestItem(this);
        }
    }
}
