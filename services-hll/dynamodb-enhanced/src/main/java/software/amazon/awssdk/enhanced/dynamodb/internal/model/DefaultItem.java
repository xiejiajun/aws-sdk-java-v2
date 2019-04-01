package software.amazon.awssdk.enhanced.dynamodb.internal.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ItemAttributeValueConverterChain;
import software.amazon.awssdk.enhanced.dynamodb.model.AttributeAware;
import software.amazon.awssdk.enhanced.dynamodb.model.ConverterAware;

/**
 * A base class from which {@link DefaultGeneratedRequestItem}, {@link DefaultGeneratedResponseItem},
 * {@link DefaultRequestItem} and {@link DefaultResponseItem} derive their implementations.
 */
@SdkInternalApi
@ThreadSafe
public abstract class DefaultItem<AttributeT> implements ConverterAware,
                                                         AttributeAware<AttributeT> {
    private final Map<String, AttributeT> attributes;
    private final List<ItemAttributeValueConverter> converters;
    protected final ItemAttributeValueConverterChain converterChain;

    protected DefaultItem(Builder<AttributeT, ?> builder) {
        this.converters = new ArrayList<>(builder.converters);
        this.converterChain = ItemAttributeValueConverterChain.create(converters());
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(builder.attributes));
    }

    @Override
    public Map<String, AttributeT> attributes() {
        return attributes;
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
            implements ConverterAware.Builder,
                       AttributeAware.Builder<AttributeT> {
        private Map<String, AttributeT> attributes = new LinkedHashMap<>();
        private List<ItemAttributeValueConverter> converters = new ArrayList<>();

        protected Builder() {}

        protected Builder(DefaultItem<AttributeT> item) {
            this.attributes.putAll(item.attributes);
            this.converters.addAll(item.converters);
        }

        @Override
        public BuilderT putAttributes(Map<String, AttributeT> attributeValues) {
            this.attributes.putAll(attributeValues);
            return (BuilderT) this;
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
        public BuilderT clearAttributes() {
            this.attributes.clear();
            return (BuilderT) this;
        }

        @Override
        public BuilderT addConverters(Collection<? extends ItemAttributeValueConverter> converters) {
            this.converters.addAll(converters);
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
