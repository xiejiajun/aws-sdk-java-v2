package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;

public final class ResponseItem extends Item {
    private final Map<String, ConvertableItemAttributeValue> attributes;

    public ResponseItem(Builder builder) {
        super(builder);
        this.attributes = new HashMap<>(builder.attributes);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, ConvertableItemAttributeValue> attributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public ConvertableItemAttributeValue attribute(String attributeKey) {
        return attributes.get(attributeKey);
    }

    public static final class Builder extends Item.Builder {
        private Map<String, ConvertableItemAttributeValue> attributes = new HashMap<>();

        public Builder putAttribute(String attributeKey, ConvertableItemAttributeValue attributeValue) {
            this.attributes.put(attributeKey, attributeValue);
            return this;
        }

        public Builder removeAttribute(String attributeKey) {
            this.attributes.remove(attributeKey);
            return this;
        }

        @Override
        public Builder addConverter(ItemAttributeValueConverter converter) {
            super.addConverter(converter);
            return this;
        }

        @Override
        public Builder clearConverters() {
            super.clearConverters();
            return this;
        }

        public ResponseItem build() {
            return new ResponseItem(this);
        }
    }
//    private final ItemAttributeValueConverterChain converters;
//    private final Map<String, Object> inputUnconvertedAttributes;
//    private final Map<String, ConvertableItemAttributeValue> inputConvertedAttributes;
//    private volatile Map<String, ConvertableItemAttributeValue> allConvertedAttributes = null;
//
//    public ResponseItem(Builder builder) {
//        this.inputUnconvertedAttributes = new HashMap<>(builder.unconvertedAttributes);
//        this.inputConvertedAttributes = new HashMap<>(convertItemAttributeValues(builder.convertedAttributes));
//        this.converters = builder.converters.build();
//    }
//
//    public static ResponseItem.Builder builder() {
//        return new Builder();
//    }
//
//    public Map<String, ConvertableItemAttributeValue> attributes() {
//
//    }
//
//    public ConvertableItemAttributeValue attribute(String attributeKey) {
//    }
//
//    private void ensureAllAttributesIsInitialized() {
//        if (allConvertedAttributes == null) {
//            synchronized (this) {
//                if (allConvertedAttributes == null) {
//                    initializeAllAttributes();
//                }
//            }
//        }
//    }
//
//    private void initializeAllAttributes() {
//        allConvertedAttributes = inputConvertedAttributes;
//        if (!inputUnconvertedAttributes.isEmpty()) {
//            allConvertedAttributes.putAll(convertObjects(inputUnconvertedAttributes));
//        }
//    }
//
//    private Map<String, ConvertableItemAttributeValue> convertObjects(Map<String, Object> attributes) {
//        Map<String, ConvertableItemAttributeValue> result = new HashMap<>();
//        attributes.forEach((key, value) -> result.put(key, toConvertable(key, value)));
//        return result;
//    }
//
//    private Map<String, ConvertableItemAttributeValue> convertItemAttributeValues(Map<String, ItemAttributeValue> attributes) {
//        Map<String, ConvertableItemAttributeValue> result = new HashMap<>();
//        attributes.forEach((key, value) -> {
//            ConversionContext context = ConversionContext.builder()..build()
//            result.put(key, toConvertable(key, value));
//        });
//        return result;
//    }
//
//    private ConvertableItemAttributeValue toConvertable(String key, Object value) {
//        ItemAttributeValue itemAttributeValue = converters.toAttributeValue(value);
//        return DefaultConvertableItemAttributeValue.builder()
//                                                   .attributeValue(itemAttributeValue
//                                                                             .conversionContext(cc -> cc.attributeName(key)
//                                                                              .converter(converters)
//                                                                              .parent(this)
//                                                                              .build())
//                                                                             .converter(converters)
//                                                                             .build();
//    }
//
//    private ConvertableItemAttributeValue toConvertable(String key, ItemAttributeValue value) {
//        return DefaultConvertableItemAttributeValue.builder()
//                                                   .attributeValue(value)
//                                                   .conversionContext(cc -> cc.attributeName(key)
//                                                                              .converter(converters)
//                                                                              .parent(this)
//                                                                              .build())
//                                                   .converter(converters)
//                                                   .build();
//    }
//
//    public static final class Builder {
//        private Map<String, Object> unconvertedAttributes = new HashMap<>();
//        private Map<String, ItemAttributeValue> convertedAttributes = new HashMap<>();
//        private ItemAttributeValueConverterChain.Builder converters = ItemAttributeValueConverterChain.builder();
//
//        public ResponseItem.Builder putAttribute(String attributeKey, Object attributeValue) {
//            this.convertedAttributes.remove(attributeKey);
//            this.unconvertedAttributes.put(attributeKey, attributeValue);
//            return this;
//        }
//
//        public ResponseItem.Builder putAttribute(String attributeKey, ItemAttributeValue attributeValue) {
//            this.convertedAttributes.put(attributeKey, attributeValue);
//            this.unconvertedAttributes.remove(attributeKey);
//            return this;
//        }
//
//        public ResponseItem.Builder removeAttribute(String attributeKey) {
//            this.convertedAttributes.remove(attributeKey);
//            this.unconvertedAttributes.remove(attributeKey);
//            return this;
//        }
//
//        public ResponseItem.Builder clearAttributes() {
//            this.convertedAttributes.clear();
//            this.unconvertedAttributes.clear();
//            return this;
//        }
//
//        public ResponseItem.Builder addConverter(ItemAttributeValueConverter converter) {
//            this.converters.addConverter(converter);
//            return this;
//        }
//
//        public ResponseItem.Builder clearConverters() {
//            this.converters.clearConverters();
//            return this;
//        }
//
//        public ResponseItem build() {
//            return new ResponseItem(this);
//        }
//    }
}
