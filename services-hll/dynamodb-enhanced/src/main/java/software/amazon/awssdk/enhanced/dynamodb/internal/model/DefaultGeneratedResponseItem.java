package software.amazon.awssdk.enhanced.dynamodb.internal.model;

import java.util.LinkedHashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.model.ConvertableItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedResponseItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.ResponseItem;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.Validate;

/**
 * The default implementation of {@link GeneratedResponseItem}.
 */
@SdkInternalApi
@ThreadSafe
public class DefaultGeneratedResponseItem extends DefaultItem<AttributeValue> implements GeneratedResponseItem {
    private DefaultGeneratedResponseItem(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ResponseItem toResponseItem() {
        ItemAttributeValue attributeValue = ItemAttributeValue.fromGeneratedItem(attributes());
        Object result = converterChain.fromAttributeValue(attributeValue,
                                                          TypeToken.from(ResponseItem.class),
                                                          ConversionContext.builder().converter(converterChain).build());
        return Validate.isInstanceOf(ResponseItem.class, result, "Conversion chain did not generated a ResponseItem.");
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder
            extends DefaultItem.Builder<AttributeValue, Builder>
            implements GeneratedResponseItem.Builder {
        private Builder() {}

        private Builder(DefaultGeneratedResponseItem item) {
            super(item);
        }

        @Override
        public DefaultGeneratedResponseItem build() {
            return new DefaultGeneratedResponseItem(this);
        }
    }
}
