package software.amazon.awssdk.enhanced.dynamodb.converter;

import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ExactInstanceOfConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.InstanceOfConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.NeverConversionCondition;

public interface ConversionCondition {
    static ConversionCondition isExactInstanceOf(Class<?> clazz) {
        return new ExactInstanceOfConversionCondition(clazz);
    }

    static ConversionCondition isInstanceOf(Class<?> clazz) {
        return new InstanceOfConversionCondition(clazz);
    }

    static ConversionCondition never() {
        return new NeverConversionCondition();
    }
}
