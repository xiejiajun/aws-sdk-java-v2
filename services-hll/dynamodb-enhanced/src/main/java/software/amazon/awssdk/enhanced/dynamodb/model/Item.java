package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Map;

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

        public Item build() {
            throw new UnsupportedOperationException();
        }
    }
}
