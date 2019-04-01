package software.amazon.awssdk.enhanced.dynamodb.internal.converter;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.DefaultConverterChain;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.utils.Validate;

/**
 * A base class that simplifies the process of implementing an {@link ItemAttributeValueConverter} with the
 * {@link ConversionCondition#isExactInstanceOf(Class)} conversion type. This handles casting to/from the mapped type and
 * validates that the converter is being invoked with the correct types.
 */
@SdkInternalApi
@ThreadSafe
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
