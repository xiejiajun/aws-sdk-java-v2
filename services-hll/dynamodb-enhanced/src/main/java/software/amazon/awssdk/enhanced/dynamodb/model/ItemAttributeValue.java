package software.amazon.awssdk.enhanced.dynamodb.model;

import java.math.BigDecimal;
import java.util.List;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;

@ThreadSafe
public class ItemAttributeValue {
    public static ItemAttributeValue from(Object object) {
        throw new UnsupportedOperationException();
    }
    public static ItemAttributeValue from(Object object, ItemAttributeValueConverter<?> converter) {
        throw new UnsupportedOperationException();
    }

    <T> T as(Class<T> type) {
        throw new UnsupportedOperationException();
    }
    ItemAttributeValueType type() {
        throw new UnsupportedOperationException();
    }

    boolean isItem() {
        throw new UnsupportedOperationException();
    }
    boolean isString() {
        throw new UnsupportedOperationException();
    }
    boolean isNumber() {
        throw new UnsupportedOperationException();
    }
    boolean isBytes() {
        throw new UnsupportedOperationException();
    }
    boolean isBoolean() {
        throw new UnsupportedOperationException();
    }
    boolean isListOfStrings() {
        throw new UnsupportedOperationException();
    }
    boolean isListOfNumbers() {
        throw new UnsupportedOperationException();
    }
    boolean isListOfBytes() {
        throw new UnsupportedOperationException();
    }
    boolean isListOfAttributeValues() {
        throw new UnsupportedOperationException();
    }
    boolean isNull() {
        throw new UnsupportedOperationException();
    }

    Item asItem() {
        throw new UnsupportedOperationException();
    }
    String asString() {
        throw new UnsupportedOperationException();
    }
    BigDecimal asNumber() {
        throw new UnsupportedOperationException();
    }
    SdkBytes asBytes() {
        throw new UnsupportedOperationException();
    }
    Boolean asBoolean() {
        throw new UnsupportedOperationException();
    }
    List<String> asListOfStrings() {
        throw new UnsupportedOperationException();
    }
    List<BigDecimal> asListOfNumbers() {
        throw new UnsupportedOperationException();
    }
    List<SdkBytes> asListOfBytes() {
        throw new UnsupportedOperationException();
    }
    List<ItemAttributeValue> asListOfAttributeValues() {
        throw new UnsupportedOperationException();
    }

    boolean isJavaType() {
        throw new UnsupportedOperationException();
    }
    Object asJavaType() {
        throw new UnsupportedOperationException();
    }
    ItemAttributeValue convertFromJavaType(ItemAttributeValueConverter<?> converter) {
        throw new UnsupportedOperationException();
    }
}
