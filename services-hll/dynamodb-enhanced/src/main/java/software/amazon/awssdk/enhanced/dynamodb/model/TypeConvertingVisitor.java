package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Collection;
import software.amazon.awssdk.core.SdkBytes;

public abstract class TypeConvertingVisitor<T> {
    public T convertNull() {
        return null;
    }

    public T convertItem(ResponseItem value) {
        return defaultConvert(value);
    }

    public T convertString(String value) {
        return defaultConvert(value);
    }

    public T convertNumber(String value) {
        return defaultConvert(value);
    }

    public T convertBytes(SdkBytes value) {
        return defaultConvert(value);
    }

    public T convertBoolean(Boolean value) {
        return defaultConvert(value);
    }

    public T convertListOfStrings(Collection<String> value) {
        return defaultConvert(value);
    }

    public T convertListOfNumbers(Collection<String> value) {
        return defaultConvert(value);
    }

    public T convertListOfBytes(Collection<SdkBytes> value) {
        return defaultConvert(value);
    }

    public T convertListOfAttributeValues(Collection<ItemAttributeValue> value) {
        return defaultConvert(value);
    }

    public T defaultConvert(Object value) {
        throw new IllegalStateException("Value cannot be converted to the requested type: " + value);
    }
}
