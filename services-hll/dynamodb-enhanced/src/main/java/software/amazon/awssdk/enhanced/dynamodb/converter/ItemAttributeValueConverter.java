package software.amazon.awssdk.enhanced.dynamodb.converter;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;

@SdkPublicApi
@ThreadSafe
public interface ItemAttributeValueConverter {
    ConversionCondition defaultConversionCondition();
    ItemAttributeValue toAttributeValue(Object input, ConversionContext context);
    Object fromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context);
}
