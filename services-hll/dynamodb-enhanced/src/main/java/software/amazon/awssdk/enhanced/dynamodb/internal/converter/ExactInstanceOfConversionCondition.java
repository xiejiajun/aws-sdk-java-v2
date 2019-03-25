package software.amazon.awssdk.enhanced.dynamodb.internal.converter;

import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;

public class ExactInstanceOfConversionCondition implements ConversionCondition {
    private final Class<?> clazz;

    public ExactInstanceOfConversionCondition(Class<?> clazz) {
        this.clazz = clazz;
    }

    public boolean converts(Class<?> clazz) {
        return this.clazz.equals(clazz);
    }
}
