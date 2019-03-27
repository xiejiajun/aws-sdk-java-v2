package software.amazon.awssdk.enhanced.dynamodb.converter.bundled;

import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeConvertingVisitor;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;

public class IntegerConverter extends ExactInstanceOfConverter<Integer> {
    public IntegerConverter() {
        super(Integer.class);
    }

    @Override
    protected ItemAttributeValue doToAttributeValue(Integer input, ConversionContext context) {
        return ItemAttributeValue.fromNumber(input.toString());
    }

    @Override
    protected Integer doFromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
        return input.convert(new Visitor());
    }

    private class Visitor extends TypeConvertingVisitor<Integer> {
        @Override
        public Integer convertString(String value) {
            return Integer.parseInt(value);
        }

        @Override
        public Integer convertNumber(String value) {
            return Integer.parseInt(value);
        }
    }
}
