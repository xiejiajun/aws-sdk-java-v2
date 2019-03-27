package software.amazon.awssdk.enhanced.dynamodb.internal.converter.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.model.ConverterAwareItem;
import software.amazon.awssdk.enhanced.dynamodb.model.Item;

public abstract class DefaultItem<AttributeT> implements ConverterAwareItem, Item<AttributeT> {
    private final Map<String, AttributeT> attributes;
    private final List<ItemAttributeValueConverter> converters;

    protected DefaultItem(Builder<AttributeT, ?> builder) {
        this.converters = new ArrayList<>(builder.converters);
        this.attributes = new HashMap<>(builder.attributes);
    }

    @Override
    public Map<String, AttributeT> attributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public AttributeT attribute(String attributeKey) {
        return attributes.get(attributeKey);
    }

    @Override
    public List<ItemAttributeValueConverter> converters() {
        return Collections.unmodifiableList(converters);
    }

    public static abstract class Builder<AttributeT, BuilderT extends Builder<AttributeT, BuilderT>>
            implements ConverterAwareItem.Builder,
                       Item.Builder<AttributeT> {
        private Map<String, AttributeT> attributes = new HashMap<>();
        private List<ItemAttributeValueConverter> converters = new ArrayList<>();

        protected Builder() {}

        protected Builder(DefaultItem<AttributeT> item) {
            this.attributes.putAll(item.attributes);
            this.converters.addAll(item.converters);
        }

        @Override
        public BuilderT putAttribute(String attributeKey, AttributeT attributeValue) {
            this.attributes.put(attributeKey, attributeValue);
            return (BuilderT) this;
        }

        @Override
        public BuilderT removeAttribute(String attributeKey) {
            this.attributes.remove(attributeKey);
            return (BuilderT) this;
        }

        @Override
        public BuilderT addConverter(ItemAttributeValueConverter converter) {
            this.converters.add(converter);
            return (BuilderT) this;
        }

        @Override
        public BuilderT clearConverters() {
            this.converters.clear();
            return (BuilderT) this;
        }
    }
}
