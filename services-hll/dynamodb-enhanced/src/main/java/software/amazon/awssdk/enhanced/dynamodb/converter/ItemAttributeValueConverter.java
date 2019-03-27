package software.amazon.awssdk.enhanced.dynamodb.converter;

import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;

public interface ItemAttributeValueConverter {
    ConversionCondition defaultConversionCondition();
    ItemAttributeValue toAttributeValue(Object input, ConversionContext context);
    Object fromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context);
}
