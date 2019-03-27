package software.amazon.awssdk.enhanced.dynamodb.internal.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.utils.Logger;

public class ItemAttributeValueConverterChain implements ItemAttributeValueConverter {
    private static final Logger log = Logger.loggerFor(ItemAttributeValueConverterChain.class);

    private final List<ItemAttributeValueConverter> converters;
    private final List<ItemAttributeValueConverter> instanceOfConverters = new ArrayList<>();
    private final ConcurrentHashMap<Class<?>, ItemAttributeValueConverter> converterCache = new ConcurrentHashMap<>();
    private final ItemAttributeValueConverterChain parentChain;

    private ItemAttributeValueConverterChain(Builder builder) {
        this.converters = builder.converters;
        for (ItemAttributeValueConverter converter : builder.converters) {
            ConversionCondition condition = converter.defaultConversionCondition();

            if (condition instanceof InstanceOfConversionCondition) {
                this.instanceOfConverters.add(converter);
            }

            if (condition instanceof ExactInstanceOfConversionCondition) {
                ExactInstanceOfConversionCondition exactCondition = (ExactInstanceOfConversionCondition) condition;
                this.converterCache.put(exactCondition.convertedClass(), converter);
            }
        }

        this.parentChain = builder.parentChain;
    }

    public static ItemAttributeValueConverterChain.Builder builder() {
        return new ItemAttributeValueConverterChain.Builder();
    }

    @Override
    public ConversionCondition defaultConversionCondition() {
        return ConversionCondition.never();
    }

    @Override
    public ItemAttributeValue toAttributeValue(Object input, ConversionContext context) {
        return getConverter(input.getClass()).toAttributeValue(input, context);
    }

    @Override
    public Object fromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
        return getConverter(desiredType.representedClass()).fromAttributeValue(input, desiredType, context);
    }

    private <T> ItemAttributeValueConverter getConverter(Class<T> type) {
        log.trace(() -> "Loading converter for " + type.getTypeName() + ".");

        ItemAttributeValueConverter converter = converterCache.get(type);

        if (converter != null) {
            return converter;
        }

        ItemAttributeValueConverter result = getUncachedConverter(type);
        converterCache.put(type, result);
        return result;
    }

    private ItemAttributeValueConverter getUncachedConverter(Class<?> type) {
        log.trace(() -> "Converter not cached for " + type.getTypeName() + ". " +
                        "Checking for an instanceof converter match.");

        for (ItemAttributeValueConverter converter : instanceOfConverters) {
            InstanceOfConversionCondition condition = (InstanceOfConversionCondition)
                    converter.defaultConversionCondition();

            if (condition.converts(type)) {
                return converter;
            }
        }

        if (parentChain != null) {
            log.trace(() -> "Converter not found in this chain for " + type.getTypeName() + ". Checking parent.");
            return parentChain.getConverter(type);
        }

        throw new IllegalArgumentException("Converter not found for " + type.getTypeName() + ".");
    }

    public static class Builder {
        private List<ItemAttributeValueConverter> converters = new ArrayList<>();
        private ItemAttributeValueConverterChain parentChain;

        private Builder() {}

        public Builder addConverters(Collection<? extends ItemAttributeValueConverter> converters) {
            this.converters.addAll(converters);
            return this;
        }

        public Builder addConverter(ItemAttributeValueConverter converter) {
            this.converters.add(converter);
            return this;
        }

        public Builder clearConverters() {
            this.converters.clear();
            return this;
        }

        public Builder parentChain(ItemAttributeValueConverterChain parentChain) {
            this.parentChain = parentChain;
            return this;
        }

        public ItemAttributeValueConverterChain build() {
            return new ItemAttributeValueConverterChain(this);
        }
    }
}
