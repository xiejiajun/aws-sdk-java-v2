package software.amazon.awssdk.enhanced.dynamodb.converter.bundled;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ExactInstanceOfConverter;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeConvertingVisitor;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;

/**
 * A converter between {@link Integer} and {@link ItemAttributeValue}.
 */
@SdkPublicApi
@ThreadSafe
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
        return input.convert(new TypeConvertingVisitor<Integer>(Integer.class, IntegerConverter.class) {
            @Override
            public Integer convertString(String value) {
                return Integer.parseInt(value);
            }

            @Override
            public Integer convertNumber(String value) {
                return Integer.parseInt(value);
            }
        });
    }
}
