package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.utils.Validate;

@ThreadSafe
public class ItemAttributeValue {
    private final ItemAttributeValueType type;
    private final boolean isNull;
    private final ResponseItem itemValue;
    private final String stringValue;
    private final String numberValue;
    private final SdkBytes bytesValue;
    private final Boolean booleanValue;
    private final List<String> listOfStringsValue;
    private final List<String> listOfNumbersValue;
    private final List<SdkBytes> listOfBytesValue;
    private final List<ItemAttributeValue> listOfAttributeValuesValue;

    private ItemAttributeValue(InternalBuilder builder) {
        this.type = builder.type;
        this.isNull = builder.isNull;
        this.itemValue = builder.itemValue;
        this.stringValue = builder.stringValue;
        this.numberValue = builder.numberValue;
        this.bytesValue = builder.bytesValue;
        this.booleanValue = builder.booleanValue;

        this.listOfStringsValue = builder.listOfStringsValue == null
                                  ? null
                                  : Collections.unmodifiableList(new ArrayList<>(builder.listOfStringsValue));
        this.listOfNumbersValue = builder.listOfNumbersValue == null
                                  ? null
                                  : Collections.unmodifiableList(new ArrayList<>(builder.listOfNumbersValue));
        this.listOfBytesValue = builder.listOfBytesValue == null
                                ? null
                                : Collections.unmodifiableList(new ArrayList<>(builder.listOfBytesValue));
        this.listOfAttributeValuesValue = builder.listOfAttributeValuesValue == null
                                          ? null
                                          : Collections.unmodifiableList(new ArrayList<>(builder.listOfAttributeValuesValue));
    }

    public static ItemAttributeValue nullValue() {
        return new InternalBuilder().isNull().build();
    }

    public static ItemAttributeValue fromItem(ResponseItem itemValue) {
        return new InternalBuilder().itemValue(itemValue).build();
    }

    public static ItemAttributeValue fromString(String stringValue) {
        return new InternalBuilder().stringValue(stringValue).build();
    }

    public static ItemAttributeValue fromNumber(String numberValue) {
        return new InternalBuilder().numberValue(numberValue).build();
    }

    public static ItemAttributeValue fromBytes(SdkBytes bytesValue) {
        return new InternalBuilder().bytesValue(bytesValue).build();
    }

    public static ItemAttributeValue fromBoolean(Boolean booleanValue) {
        return new InternalBuilder().booleanValue(booleanValue).build();
    }

    public static ItemAttributeValue fromListOfStrings(List<String> listOfStringsValue) {
        return new InternalBuilder().listOfStringsValue(listOfStringsValue).build();
    }

    public static ItemAttributeValue fromListOfNumbers(List<String> listOfNumbersValue) {
        return new InternalBuilder().listOfNumbersValue(listOfNumbersValue).build();
    }

    public static ItemAttributeValue fromListOfBytes(List<SdkBytes> listOfBytesValue) {
        return new InternalBuilder().listOfBytesValue(listOfBytesValue).build();
    }

    public static ItemAttributeValue fromListOfAttributeValues(List<ItemAttributeValue> listOfAttributeValuesValue) {
        return new InternalBuilder().listOfAttributeValuesValue(listOfAttributeValuesValue).build();
    }

    public <T> T convert(TypeConvertingVisitor<T> convertingVisitor) {
        switch (type()) {
            case NULL: return convertingVisitor.convertNull();
            case ITEM: return convertingVisitor.convertItem(itemValue);
            case STRING: return convertingVisitor.convertString(stringValue);
            case NUMBER: return convertingVisitor.convertNumber(numberValue);
            case BYTES: return convertingVisitor.convertBytes(bytesValue);
            case BOOLEAN: return convertingVisitor.convertBoolean(booleanValue);
            case LIST_OF_STRINGS: return convertingVisitor.convertListOfStrings(listOfStringsValue);
            case LIST_OF_NUMBERS: return convertingVisitor.convertListOfNumbers(listOfNumbersValue);
            case LIST_OF_BYTES: return convertingVisitor.convertListOfBytes(listOfBytesValue);
            case LIST_OF_ATTRIBUTE_VALUES: return convertingVisitor.convertListOfAttributeValues(listOfAttributeValuesValue);
            default: throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    public ItemAttributeValueType type() {
        return type;
    }

    public boolean isItem() {
        return itemValue != null;
    }

    public boolean isString() {
        return stringValue != null;
    }

    public boolean isNumber() {
        return numberValue != null;
    }

    public boolean isBytes() {
        return bytesValue!= null;
    }

    public boolean isBoolean() {
        return booleanValue != null;
    }

    public boolean isListOfStrings() {
        return listOfStringsValue != null;
    }

    public boolean isListOfNumbers() {
        return listOfNumbersValue != null;
    }

    public boolean isListOfBytes() {
        return bytesValue != null;
    }

    public boolean isListOfAttributeValues() {
        return listOfAttributeValuesValue != null;
    }

    public boolean isNull() {
        return isNull;
    }

    public ResponseItem asItem() {
        Validate.isTrue(isItem(), "Value is not an item.");
        return itemValue;
    }

    public String asString() {
        Validate.isTrue(isString(), "Value is not a string.");
        return stringValue;
    }

    public String asNumber() {
        Validate.isTrue(isNumber(), "Value is not a number.");
        return numberValue;
    }

    public SdkBytes asBytes() {
        Validate.isTrue(isBytes(), "Value is not bytes.");
        return bytesValue;
    }

    public Boolean asBoolean() {
        Validate.isTrue(isBoolean(), "Value is not a boolean.");
        return booleanValue;
    }

    public List<String> asListOfStrings() {
        Validate.isTrue(isListOfStrings(), "Value is not a list of strings.");
        return listOfStringsValue;
    }

    public List<String> asListOfNumbers() {
        Validate.isTrue(isListOfNumbers(), "Value is not a list of numbers.");
        return listOfNumbersValue;
    }

    public List<SdkBytes> asListOfBytes() {
        Validate.isTrue(isListOfBytes(), "Value is not a list of bytes.");
        return listOfBytesValue;
    }

    public List<ItemAttributeValue> asListOfAttributeValues() {
        Validate.isTrue(isListOfAttributeValues(), "Value is not a list of attribute values.");
        return listOfAttributeValuesValue;
    }

    private static class InternalBuilder {
        private ItemAttributeValueType type;
        private boolean isNull = false;
        private ResponseItem itemValue;
        private String stringValue;
        private String numberValue;
        private SdkBytes bytesValue;
        private Boolean booleanValue;
        private Collection<String> listOfStringsValue;
        private Collection<String> listOfNumbersValue;
        private Collection<SdkBytes> listOfBytesValue;
        private Collection<ItemAttributeValue> listOfAttributeValuesValue;

        public InternalBuilder isNull() {
            this.type = ItemAttributeValueType.NULL;
            this.isNull = true;
            return this;
        }

        private InternalBuilder itemValue(ResponseItem itemValue) {
            this.type = ItemAttributeValueType.ITEM;
            this.itemValue = itemValue;
            return this;
        }

        private InternalBuilder stringValue(String stringValue) {
            this.type = ItemAttributeValueType.STRING;
            this.stringValue = stringValue;
            return this;
        }

        private InternalBuilder numberValue(String numberValue) {
            this.type = ItemAttributeValueType.NUMBER;
            this.numberValue = numberValue;
            return this;
        }

        private InternalBuilder bytesValue(SdkBytes bytesValue) {
            this.type = ItemAttributeValueType.BYTES;
            this.bytesValue = bytesValue;
            return this;
        }

        private InternalBuilder booleanValue(Boolean booleanValue) {
            this.type = ItemAttributeValueType.BOOLEAN;
            this.booleanValue = booleanValue;
            return this;
        }

        private InternalBuilder listOfStringsValue(Collection<String> listOfStringsValue) {
            this.type = ItemAttributeValueType.LIST_OF_STRINGS;
            this.listOfStringsValue = listOfStringsValue;
            return this;
        }

        private InternalBuilder listOfNumbersValue(Collection<String> listOfNumbersValue) {
            this.type = ItemAttributeValueType.LIST_OF_NUMBERS;
            this.listOfNumbersValue = listOfNumbersValue;
            return this;
        }

        private InternalBuilder listOfBytesValue(Collection<SdkBytes> listOfBytesValue) {
            this.type = ItemAttributeValueType.LIST_OF_BYTES;
            this.listOfBytesValue = listOfBytesValue;
            return this;
        }

        private InternalBuilder listOfAttributeValuesValue(Collection<ItemAttributeValue> listOfAttributeValuesValue) {
            this.type = ItemAttributeValueType.LIST_OF_ATTRIBUTE_VALUES;
            this.listOfAttributeValuesValue = listOfAttributeValuesValue;
            return this;
        }

        private ItemAttributeValue build() {
            return new ItemAttributeValue(this);
        }
    }
}
