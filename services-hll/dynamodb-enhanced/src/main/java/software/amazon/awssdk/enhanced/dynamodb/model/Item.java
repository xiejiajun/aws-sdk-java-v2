package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.List;
import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;

public final class Item {
    public static Item.Builder builder() {
        return new Builder();
    }

    public Map<String, ItemAttributeValue> attributes() {
        throw new UnsupportedOperationException();
    }

    public ItemAttributeValue attribute(String attributeKey) {
        throw new UnsupportedOperationException();
    }

    public Item convertJavaTypeAttributes(ItemAttributeValueConverter<?> conversionChain) {
        throw new UnsupportedOperationException();
    }

    public static final class Builder {
        public Item.Builder attributes(Map<String, ItemAttributeValue> attributes) {
            throw new UnsupportedOperationException();
        }

        public Item.Builder putAttribute(String attributeKey, ItemAttributeValue attributeValue) {
            throw new UnsupportedOperationException();
        }

        public Item.Builder putAttribute(String attributeKey, Object attributeValue) {
            throw new UnsupportedOperationException();
        }

        public Item.Builder removeAttribute(String attributeKey) {
            throw new UnsupportedOperationException();
        }

        public Item.Builder clearAttributes() {
            throw new UnsupportedOperationException();
        }

        public Item.Builder converters(List<ItemAttributeValueConverter<?>> converters) {
            throw new UnsupportedOperationException();
        }

        public Item.Builder addConverter(ItemAttributeValueConverter<?> converter) {
            throw new UnsupportedOperationException();
        }

        public Item.Builder clearConverters() {
            throw new UnsupportedOperationException();
        }

        public Item build() {
            throw new UnsupportedOperationException();
        }
    }
}
