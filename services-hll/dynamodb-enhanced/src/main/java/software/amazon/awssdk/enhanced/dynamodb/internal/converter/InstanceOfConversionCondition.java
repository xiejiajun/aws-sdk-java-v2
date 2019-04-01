package software.amazon.awssdk.enhanced.dynamodb.internal.converter;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.converter.DefaultConverterChain;

/**
 * This is created by {@link ConversionCondition#isInstanceOf(Class)}. The parent is just a marker interface, so
 * {@link DefaultConverterChain} casts this to a concrete type to invoke it.
 *
 * {@link InstanceOfConverter} simplifies the process of implementing converters of this type.
 */
@SdkInternalApi
@ThreadSafe
public class InstanceOfConversionCondition implements ConversionCondition {
    private final Class<?> clazz;

    public InstanceOfConversionCondition(Class<?> clazz) {
        this.clazz = clazz;
    }

    public boolean converts(Class<?> clazz) {
        return this.clazz.isAssignableFrom(clazz);
    }
}
