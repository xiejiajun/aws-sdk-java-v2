package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;

@SdkPublicApi
@ThreadSafe
public abstract class TypeConvertingVisitor<T> {
    private final Class<? extends ItemAttributeValueConverter> converterClass;
    private final Class<?> targetType;

    protected TypeConvertingVisitor(Class<?> targetType,
                                    Class<? extends ItemAttributeValueConverter> converterClass) {
        this.converterClass = converterClass;
        this.targetType = targetType;
    }

    protected TypeConvertingVisitor(Class<?> targetType) {
        this(targetType, null);
    }

    public final T convert(ItemAttributeValue value) {
        switch (value.type()) {
            case NULL: return convertNull();
            case MAP: return convertMap(value.asMap());
            case STRING: return convertString(value.asString());
            case NUMBER: return convertNumber(value.asNumber());
            case BYTES: return convertBytes(value.asBytes());
            case BOOLEAN: return convertBoolean(value.asBoolean());
            case SET_OF_STRINGS: return convertSetOfStrings(value.asSetOfStrings());
            case SET_OF_NUMBERS: return convertSetOfNumbers(value.asSetOfNumbers());
            case SET_OF_BYTES: return convertSetOfBytes(value.asSetOfBytes());
            case LIST_OF_ATTRIBUTE_VALUES: return convertListOfAttributeValues(value.asListOfAttributeValues());
            default: throw new IllegalStateException("Unsupported type: " + value.type());
        }
    }

    public T convertNull() {
        return null;
    }

    public T convertMap(Map<String, ItemAttributeValue> value) {
        return defaultConvert(ItemAttributeValueType.MAP, value);
    }

    public T convertString(String value) {
        return defaultConvert(ItemAttributeValueType.STRING, value);
    }

    public T convertNumber(String value) {
        return defaultConvert(ItemAttributeValueType.NUMBER, value);
    }

    public T convertBytes(SdkBytes value) {
        return defaultConvert(ItemAttributeValueType.BYTES, value);
    }

    public T convertBoolean(Boolean value) {
        return defaultConvert(ItemAttributeValueType.BOOLEAN, value);
    }

    public T convertSetOfStrings(Set<String> value) {
        return defaultConvert(ItemAttributeValueType.SET_OF_STRINGS, value);
    }

    public T convertSetOfNumbers(Set<String> value) {
        return defaultConvert(ItemAttributeValueType.SET_OF_NUMBERS, value);
    }

    public T convertSetOfBytes(Set<SdkBytes> value) {
        return defaultConvert(ItemAttributeValueType.SET_OF_BYTES, value);
    }

    public T convertListOfAttributeValues(Collection<ItemAttributeValue> value) {
        return defaultConvert(ItemAttributeValueType.LIST_OF_ATTRIBUTE_VALUES, value);
    }

    public T defaultConvert(ItemAttributeValueType type, Object value) {
        if (converterClass != null && targetType != null) {
            throw new IllegalStateException(converterClass.getTypeName() + " cannot convert an attribute of type " + type +
                                            " into the requested type " + targetType);
        }

        throw new IllegalStateException("Cannot convert attribute of type " + type);
    }
}
