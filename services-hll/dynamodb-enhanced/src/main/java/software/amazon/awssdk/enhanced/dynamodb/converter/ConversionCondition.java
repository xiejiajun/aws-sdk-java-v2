package software.amazon.awssdk.enhanced.dynamodb.converter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ExactInstanceOfConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.InstanceOfConversionCondition;

/**
 * A condition under which a {@link ItemAttributeValueConverter} is invoked by the SDK.
 *
 * This interface should not be implemented directly. Instead, the {@link #isExactInstanceOf(Class)} and
 * {@link #isInstanceOf(Class)} methods should be used to create instances of this class.
 *
 * See {@link ItemAttributeValueConverter} for more details regarding converter priority.
 */
@SdkPublicApi
@ThreadSafe
public interface ConversionCondition {
    /**
     * Create a condition that resolves to true when the Java type being converted exactly matches the provided type.
     *
     * For example a {@code ConversionCondition.isExactInstanceOf(HashMap.class)} {@link ItemAttributeValueConverter} will only
     * be invoked when the customer requests or provides a {@link HashMap}. Subtypes like {@link LinkedHashMap} will not be
     * handled by this converter.
     */
    static ConversionCondition isExactInstanceOf(Class<?> clazz) {
        return new ExactInstanceOfConversionCondition(clazz);
    }

    /**
     * Create a condition that resolves to true when the Java type being converted matches or extends the provided type.
     *
     * For example a {@code ConversionCondition.isInstanceOf(Map.class)} {@link ItemAttributeValueConverter} will
     * be invoked when the customer requests or provides any implementation of {@link Map}.
     */
    static ConversionCondition isInstanceOf(Class<?> clazz) {
        return new InstanceOfConversionCondition(clazz);
    }
}
