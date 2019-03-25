package software.amazon.awssdk.enhanced.dynamodb.converter.condition;

import java.util.function.Predicate;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;

public interface ConversionCondition {
    static ConversionCondition contextSatisfies(Predicate<ConversionContext> predicate) {
        throw new UnsupportedOperationException();
    }

    static ConversionCondition isInstanceOf(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    static ConversionCondition never() {
        throw new UnsupportedOperationException();
    }
}
