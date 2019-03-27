package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;

public class RequestItem extends Item {
    private final Map<String, Object> attributes;

    public RequestItem(Builder builder) {
        super(builder);
        this.attributes = new HashMap<>(builder.attributes);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, Object> attributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public Object attribute(String attributeKey) {
        return attributes.get(attributeKey);
    }

    public GeneratedRequestItem toGeneratedItem() {

    }

    public static final class Builder extends Item.Builder {
        private Map<String, Object> attributes = new HashMap<>();

        public Builder putAttribute(String attributeKey, Object attributeValue) {
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

        public RequestItem build() {
            return new RequestItem(this);
        }
    }
}
