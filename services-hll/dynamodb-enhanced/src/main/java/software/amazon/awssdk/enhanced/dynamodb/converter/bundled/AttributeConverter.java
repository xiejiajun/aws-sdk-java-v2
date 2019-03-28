package software.amazon.awssdk.enhanced.dynamodb.converter.bundled;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ExactInstanceOfConverter;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;

@SdkPublicApi
@ThreadSafe
public class AttributeConverter extends ExactInstanceOfConverter<ItemAttributeValue> {
    public AttributeConverter() {
        super(ItemAttributeValue.class);
    }

    @Override
    protected ItemAttributeValue doToAttributeValue(ItemAttributeValue input, ConversionContext context) {
        return input;
    }

    @Override
    protected ItemAttributeValue doFromAttributeValue(ItemAttributeValue input,
                                                      TypeToken<?> desiredType,
                                                      ConversionContext context) {
        return input;
    }
}
