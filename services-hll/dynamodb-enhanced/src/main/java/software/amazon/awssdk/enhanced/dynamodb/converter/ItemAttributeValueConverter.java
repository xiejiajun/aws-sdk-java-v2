package software.amazon.awssdk.enhanced.dynamodb.converter;

import software.amazon.awssdk.enhanced.dynamodb.converter.condition.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;

public interface ItemAttributeValueConverter<T> {
    ConversionCondition defaultConversionCondition();
    ItemAttributeValue toAttributeValue(T input, ConversionContext context);
    T fromAttributeValue(ItemAttributeValue input, ConversionContext context);
}
