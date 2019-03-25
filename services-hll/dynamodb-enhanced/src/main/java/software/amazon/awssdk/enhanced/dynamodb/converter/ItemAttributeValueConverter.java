package software.amazon.awssdk.enhanced.dynamodb.converter;

import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;

public interface ItemAttributeValueConverter {
    ConversionCondition defaultConversionCondition();
    ItemAttributeValue toAttributeValue(Object input, ConversionContext context);
    <U> U fromAttributeValue(ItemAttributeValue input, ConversionContext context, Class<U> desiredType);
}
