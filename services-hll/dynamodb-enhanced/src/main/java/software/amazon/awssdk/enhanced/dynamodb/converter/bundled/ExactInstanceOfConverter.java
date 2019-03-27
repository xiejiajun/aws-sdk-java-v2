package software.amazon.awssdk.enhanced.dynamodb.converter.bundled;

import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.utils.Validate;

public abstract class ExactInstanceOfConverter<T> implements ItemAttributeValueConverter {
    private final Class<T> type;

    protected ExactInstanceOfConverter(Class<?> type) {
        this.type = (Class<T>) type;
    }

    @Override
    public ConversionCondition defaultConversionCondition() {
        return ConversionCondition.isExactInstanceOf(type);
    }

    @Override
    public ItemAttributeValue toAttributeValue(Object input, ConversionContext context) {
        Validate.isTrue(type.equals(input.getClass()),
                        "The input %s does not equal %s.", input.getClass(), type);

        return doToAttributeValue(type.cast(input), context);
    }

    @Override
    public Object fromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
        Validate.isTrue(type.equals(desiredType.representedClass()),
                        "The desired type %s does not equal %s.", desiredType, type);

        return doFromAttributeValue(input, desiredType, context);
    }

    protected Class<T> type() {
        return type;
    }

    protected abstract ItemAttributeValue doToAttributeValue(T input, ConversionContext context);
    protected abstract T doFromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context);
}
