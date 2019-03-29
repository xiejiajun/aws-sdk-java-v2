package software.amazon.awssdk.enhanced.dynamodb.internal.model;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ItemAttributeValueConverterChain;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedRequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemKey;

@SdkInternalApi
@ThreadSafe
public class DefaultItemKey extends DefaultItem<Object> implements ItemKey {
    private final ItemAttributeValueConverterChain converter;

    private DefaultItemKey(Builder builder) {
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
                                   .putAttributes(itemAttributeValue.toGeneatedItem())
                                   .build();
    }

    @Override
    public DefaultItemKey.Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder extends DefaultItem.Builder<Object, Builder> implements ItemKey.Builder {
        private Builder() {}

        private Builder(DefaultItemKey item) {
            super(item);
        }

        @Override
        public DefaultItemKey build() {
            return new DefaultItemKey(this);
        }
    }
}
