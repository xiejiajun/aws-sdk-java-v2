package software.amazon.awssdk.enhanced.dynamodb.internal.converter;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;

@SdkInternalApi
@ThreadSafe
public class ExactInstanceOfConversionCondition implements ConversionCondition {
    private final Class<?> clazz;

    public ExactInstanceOfConversionCondition(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> convertedClass() {
        return clazz;
    }
}
