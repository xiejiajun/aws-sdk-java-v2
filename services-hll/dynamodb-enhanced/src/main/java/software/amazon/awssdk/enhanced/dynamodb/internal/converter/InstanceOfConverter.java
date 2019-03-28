package software.amazon.awssdk.enhanced.dynamodb.internal.converter;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
@ThreadSafe
public abstract class InstanceOfConverter<T> implements ItemAttributeValueConverter {
    private final Class<T> type;

    protected InstanceOfConverter(Class<?> type) {
        this.type = (Class<T>) type;
    }

    @Override
    public final ConversionCondition defaultConversionCondition() {
        return ConversionCondition.isInstanceOf(type);
    }

    @Override
    public final ItemAttributeValue toAttributeValue(Object input, ConversionContext context) {
        T typedInput = Validate.isInstanceOf(type, input,
                                             "Input type %s could not be converted to a %s.",
                                             input.getClass(), type);

        return doToAttributeValue(typedInput, context);
    }

    @Override
    public final Object fromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
        Validate.isAssignableFrom(type, desiredType.representedClass(),
                                  "Requested type %s is not a subtype of %s.",
                                  desiredType, type);

        return doFromAttributeValue(input, desiredType, context);
    }

    protected Class<T> type() {
        return type;
    }

    protected abstract ItemAttributeValue doToAttributeValue(T input,
                                                             ConversionContext conversionContext);

    protected abstract T doFromAttributeValue(ItemAttributeValue input,
                                              TypeToken<?> desiredType,
                                              ConversionContext context);
}
