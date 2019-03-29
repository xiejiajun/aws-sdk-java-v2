package software.amazon.awssdk.enhanced.dynamodb.model;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructMap;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
@ThreadSafe
public final class ItemAttributeValue {
    private final ItemAttributeValueType type;
    private final boolean isNull;
    private final Map<String, ItemAttributeValue> mapValue;
    private final String stringValue;
    private final String numberValue;
    private final SdkBytes bytesValue;
    private final Boolean booleanValue;
    private final List<String> setOfStringsValue;
    private final List<String> setOfNumbersValue;
    private final List<SdkBytes> setOfBytesValue;
    private final List<ItemAttributeValue> listOfAttributeValuesValue;

    private ItemAttributeValue(InternalBuilder builder) {
        this.type = builder.type;
        this.isNull = builder.isNull;
        this.stringValue = builder.stringValue;
        this.numberValue = builder.numberValue;
        this.bytesValue = builder.bytesValue;
        this.booleanValue = builder.booleanValue;

        this.mapValue = builder.mapValue == null
                        ? null
                        : Collections.unmodifiableMap(new LinkedHashMap<>(builder.mapValue));
        this.setOfStringsValue = builder.setOfStringsValue == null
                                 ? null
                                 : Collections.unmodifiableList(new ArrayList<>(builder.setOfStringsValue));
        this.setOfNumbersValue = builder.setOfNumbersValue == null
                                 ? null
                                 : Collections.unmodifiableList(new ArrayList<>(builder.setOfNumbersValue));
        this.setOfBytesValue = builder.setOfBytesValue == null
                               ? null
                               : Collections.unmodifiableList(new ArrayList<>(builder.setOfBytesValue));
        this.listOfAttributeValuesValue = builder.listOfAttributeValuesValue == null
                                          ? null
                                          : Collections.unmodifiableList(new ArrayList<>(builder.listOfAttributeValuesValue));
    }

    public static ItemAttributeValue nullValue() {
        return new InternalBuilder().isNull().build();
    }

    public static ItemAttributeValue fromMap(Map<String, ItemAttributeValue> mapValue) {
        return new InternalBuilder().mapValue(mapValue).build();
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

    public static ItemAttributeValue fromSetOfStrings(Collection<String> setOfStringsValue) {
        return new InternalBuilder().setOfStringsValue(setOfStringsValue).build();
    }

    public static ItemAttributeValue fromSetOfNumbers(Collection<String> setOfNumbersValue) {
        return new InternalBuilder().setOfNumbersValue(setOfNumbersValue).build();
    }

    public static ItemAttributeValue fromSetOfBytes(Collection<SdkBytes> setOfBytesValue) {
        return new InternalBuilder().setOfBytesValue(setOfBytesValue).build();
    }

    public static ItemAttributeValue fromListOfAttributeValues(List<ItemAttributeValue> ListOfAttributeValuesValue) {
        return new InternalBuilder().listOfAttributeValuesValue(ListOfAttributeValuesValue).build();
    }

    public static ItemAttributeValue fromGeneratedItem(Map<String, AttributeValue> attributeValues) {
        Map<String, ItemAttributeValue> result = new LinkedHashMap<>();
        attributeValues.forEach((k, v) -> result.put(k, fromGeneratedAttributeValue(v)));
        return ItemAttributeValue.fromMap(result);
    }

    public static ItemAttributeValue fromGeneratedAttributeValue(AttributeValue attributeValue) {
        if (attributeValue.s() != null) {
            return ItemAttributeValue.fromString(attributeValue.s());
        }
        if (attributeValue.n() != null) {
            return ItemAttributeValue.fromNumber(attributeValue.n());
        }
        if (attributeValue.bool() != null) {
            return ItemAttributeValue.fromBoolean(attributeValue.bool());
        }
        if (Boolean.TRUE.equals(attributeValue.nul())) {
            return ItemAttributeValue.nullValue();
        }
        if (attributeValue.b() != null) {
            return ItemAttributeValue.fromBytes(attributeValue.b());
        }
        if (attributeValue.m() != null && !(attributeValue.m() instanceof SdkAutoConstructMap)) {
            Map<String, ItemAttributeValue> map = new LinkedHashMap<>();
            attributeValue.m().forEach((k, v) -> map.put(k, ItemAttributeValue.fromGeneratedAttributeValue(v)));
            return ItemAttributeValue.fromMap(map);
        }
        if (attributeValue.l() != null && !(attributeValue.l() instanceof SdkAutoConstructList)) {
            List<ItemAttributeValue> list =
                    attributeValue.l().stream().map(ItemAttributeValue::fromGeneratedAttributeValue).collect(toList());
            return ItemAttributeValue.fromListOfAttributeValues(list);
        }
        if (attributeValue.bs() != null && !(attributeValue.bs() instanceof SdkAutoConstructList)) {
            return ItemAttributeValue.fromSetOfBytes(attributeValue.bs());
        }
        if (attributeValue.ss() != null && !(attributeValue.bs() instanceof SdkAutoConstructList)) {
            return ItemAttributeValue.fromSetOfStrings(attributeValue.ss());
        }
        if (attributeValue.ns() != null && !(attributeValue.ns() instanceof SdkAutoConstructList)) {
            return ItemAttributeValue.fromSetOfNumbers(attributeValue.ns());
        }

        throw new IllegalStateException("Unable to convert attribute value: " + attributeValue);
    }

    public <T> T convert(TypeConvertingVisitor<T> convertingVisitor) {
        return convertingVisitor.convert(this);
    }

    public ItemAttributeValueType type() {
        return type;
    }

    public boolean isMap() {
        return mapValue != null;
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

    public boolean isSetOfStrings() {
        return setOfStringsValue != null;
    }

    public boolean isSetOfNumbers() {
        return setOfNumbersValue != null;
    }

    public boolean isSetOfBytes() {
        return bytesValue != null;
    }

    public boolean isListOfAttributeValues() {
        return listOfAttributeValuesValue != null;
    }

    public boolean isNull() {
        return isNull;
    }

    public Map<String, ItemAttributeValue> asMap() {
        Validate.isTrue(isMap(), "Value is not a map.");
        return mapValue;
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

    public List<String> asSetOfStrings() {
        Validate.isTrue(isSetOfStrings(), "Value is not a list of strings.");
        return setOfStringsValue;
    }

    public List<String> asSetOfNumbers() {
        Validate.isTrue(isSetOfNumbers(), "Value is not a list of numbers.");
        return setOfNumbersValue;
    }

    public List<SdkBytes> asSetOfBytes() {
        Validate.isTrue(isSetOfBytes(), "Value is not a list of bytes.");
        return setOfBytesValue;
    }

    public List<ItemAttributeValue> asListOfAttributeValues() {
        Validate.isTrue(isListOfAttributeValues(), "Value is not a list of attribute values.");
        return listOfAttributeValuesValue;
    }

    public Map<String, AttributeValue> toGeneratedItem() {
        Validate.validState(isMap(), "Cannot convert an attribute value of type %s to a generated item. Must be %s.",
                            type(), ItemAttributeValueType.MAP);

        AttributeValue generatedAttributeValue = toGeneratedAttributeValue();

        Validate.validState(generatedAttributeValue.m() != null && !(generatedAttributeValue.m() instanceof SdkAutoConstructMap),
                            "Map ItemAttributeValue was not converted into a Map AttributeValue.");
        return generatedAttributeValue.m();
    }

    public AttributeValue toGeneratedAttributeValue() {
        return convert(ToGeneratedAttributeValueVisitor.INSTANCE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ItemAttributeValue that = (ItemAttributeValue) o;
        return isNull == that.isNull &&
               type == that.type &&
               Objects.equals(mapValue, that.mapValue) &&
               Objects.equals(stringValue, that.stringValue) &&
               Objects.equals(numberValue, that.numberValue) &&
               Objects.equals(bytesValue, that.bytesValue) &&
               Objects.equals(booleanValue, that.booleanValue) &&
               Objects.equals(setOfStringsValue, that.setOfStringsValue) &&
               Objects.equals(setOfNumbersValue, that.setOfNumbersValue) &&
               Objects.equals(setOfBytesValue, that.setOfBytesValue) &&
               Objects.equals(listOfAttributeValuesValue, that.listOfAttributeValuesValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, isNull, mapValue, stringValue, numberValue, bytesValue, booleanValue, setOfStringsValue,
                            setOfNumbersValue, setOfBytesValue, listOfAttributeValuesValue);
    }

    @Override
    public String toString() {
        return ToString.builder("ItemAttributeValue")
                       .add("type", type)
                       .add("null", isNull)
                       .add("map", mapValue)
                       .add("string", stringValue)
                       .add("number", numberValue)
                       .add("bytes", bytesValue)
                       .add("boolean", booleanValue)
                       .add("setOfStrings", setOfStringsValue)
                       .add("setOfNumbers", setOfNumbersValue)
                       .add("setOfBytes", setOfBytesValue)
                       .add("listOfAttributeValues", listOfAttributeValuesValue)
                       .build();
    }

    private static class ToGeneratedAttributeValueVisitor extends TypeConvertingVisitor<AttributeValue> {
        private static final ToGeneratedAttributeValueVisitor INSTANCE = new ToGeneratedAttributeValueVisitor();

        private ToGeneratedAttributeValueVisitor() {
            super(AttributeValue.class);
        }

        @Override
        public AttributeValue convertNull() {
            return AttributeValue.builder().nul(true).build();
        }

        @Override
        public AttributeValue convertMap(Map<String, ItemAttributeValue> value) {
            Map<String, AttributeValue> map = new LinkedHashMap<>();
            value.forEach((k, v) -> map.put(k, v.toGeneratedAttributeValue()));
            return AttributeValue.builder().m(map).build();
        }

        @Override
        public AttributeValue convertString(String value) {
            return AttributeValue.builder().s(value).build();
        }

        @Override
        public AttributeValue convertNumber(String value) {
            return AttributeValue.builder().n(value).build();
        }

        @Override
        public AttributeValue convertBytes(SdkBytes value) {
            return AttributeValue.builder().b(value).build();
        }

        @Override
        public AttributeValue convertBoolean(Boolean value) {
            return AttributeValue.builder().bool(value).build();
        }

        @Override
        public AttributeValue convertSetOfStrings(List<String> value) {
            return AttributeValue.builder().ss(value).build();
        }

        @Override
        public AttributeValue convertSetOfNumbers(List<String> value) {
            return AttributeValue.builder().ns(value).build();
        }

        @Override
        public AttributeValue convertSetOfBytes(List<SdkBytes> value) {
            return AttributeValue.builder().bs(value).build();
        }

        @Override
        public AttributeValue convertListOfAttributeValues(Collection<ItemAttributeValue> value) {
            return AttributeValue.builder()
                                 .l(value.stream().map(ItemAttributeValue::toGeneratedAttributeValue).collect(toList()))
                                 .build();
        }
    }

    private static class InternalBuilder {
        private ItemAttributeValueType type;
        private boolean isNull = false;
        private Map<String, ItemAttributeValue> mapValue;
        private String stringValue;
        private String numberValue;
        private SdkBytes bytesValue;
        private Boolean booleanValue;
        private Collection<String> setOfStringsValue;
        private Collection<String> setOfNumbersValue;
        private Collection<SdkBytes> setOfBytesValue;
        private Collection<ItemAttributeValue> listOfAttributeValuesValue;

        public InternalBuilder isNull() {
            this.type = ItemAttributeValueType.NULL;
            this.isNull = true;
            return this;
        }

        private InternalBuilder mapValue(Map<String, ItemAttributeValue> mapValue) {
            this.type = ItemAttributeValueType.MAP;
            this.mapValue = mapValue;
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

        private InternalBuilder setOfStringsValue(Collection<String> setOfStringsValue) {
            this.type = ItemAttributeValueType.SET_OF_STRINGS;
            this.setOfStringsValue = setOfStringsValue;
            return this;
        }

        private InternalBuilder setOfNumbersValue(Collection<String> setOfNumbersValue) {
            this.type = ItemAttributeValueType.SET_OF_NUMBERS;
            this.setOfNumbersValue = setOfNumbersValue;
            return this;
        }

        private InternalBuilder setOfBytesValue(Collection<SdkBytes> setOfBytesValue) {
            this.type = ItemAttributeValueType.SET_OF_BYTES;
            this.setOfBytesValue = setOfBytesValue;
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
