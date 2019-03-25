package software.amazon.awssdk.enhanced.dynamodb.converter.bundled;

import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;

public class StringConverter implements ItemAttributeValueConverter<String> {
    @Override
    public ConversionCondition defaultConversionCondition() {
        return ConversionCondition.isInstanceOf(String.class);
    }

    @Override
    public ItemAttributeValue toAttributeValue(String input, ConversionContext context) {
        ItemAttributeValue.from()
        return null;
    }

    @Override
    public String fromAttributeValue(ItemAttributeValue input, ConversionContext context) {
        return null;
    }
}
