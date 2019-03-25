package software.amazon.awssdk.enhanced.dynamodb.internal.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

public class ItemAttributeValueConverterChain implements ItemAttributeValueConverter<Object> {
    private static final Logger log = Logger.loggerFor(ItemAttributeValueConverterChain.class);

    private final List<ItemAttributeValueConverter<?>> exactInstanceOfConverters = new ArrayList<>();
    private final List<ItemAttributeValueConverter<?>> instanceOfConverters = new ArrayList<>();
    private final ConcurrentHashMap<Class<?>, ItemAttributeValueConverter<?>> converterCache = new ConcurrentHashMap<>();
    private final ItemAttributeValueConverterChain parentChain;

    private ItemAttributeValueConverterChain(Builder builder) {
        for (ItemAttributeValueConverter<?> converter : builder.converters) {
            ConversionCondition condition = converter.defaultConversionCondition();

            if (condition instanceof InstanceOfConversionCondition) {
                this.instanceOfConverters.add(converter);
            }

            if (condition instanceof ExactInstanceOfConversionCondition) {
                this.exactInstanceOfConverters.add(converter);
            }
        }

        this.parentChain = builder.parentChain;
    }

    @Override
    public ConversionCondition defaultConversionCondition() {
        return ConversionCondition.never();
    }

    @Override
    public ItemAttributeValue toAttributeValue(Object input, ConversionContext context) {
        return null;
    }

    @Override
    public <U> U fromAttributeValue(ItemAttributeValue input, ConversionContext context, Class<U> desiredType) {
        ItemAttributeValueConverter<?> converter = getConverter(desiredType);
        Object converted = converter.fromAttributeValue(input, context, desiredType);
        return Validate.isInstanceOf(type, converted,
                                     "Converter %s converted input to type %s, where %s was desired.",
                                     );
    }

    private <T> ItemAttributeValueConverter<? super T> getConverter(Class<T> type) {
        log.trace(() -> "Loading converter for " + type.getTypeName() + ".");

        ItemAttributeValueConverter<?> converter = converterCache.get(type);

        if (converter != null) {
            return converter;
        }

        ItemAttributeValueConverter<?> result = getUncachedConverter(type);
        converterCache.put(type, result);
        return result;
    }

    private ItemAttributeValueConverter<?> getUncachedConverter(Class<?> type) {
        log.trace(() -> "Converter not cached for " + type.getTypeName() + ". " +
                        "Checking for an exact instanceof converter match.");

        for (ItemAttributeValueConverter<?> converter : exactInstanceOfConverters) {
            ExactInstanceOfConversionCondition condition = (ExactInstanceOfConversionCondition)
                    converter.defaultConversionCondition();

            if (condition.converts(type)) {
                return converter;
            }
        }

        log.trace(() -> "Exact instanceof converter not found for " + type.getTypeName() + ". " +
                        "Checking for an instanceof converter match.");

        for (ItemAttributeValueConverter<?> converter : instanceOfConverters) {
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

        throw new IllegalArgumentException("Converter not found for " + type.getTypeName() + ".")
    }

    public static class Builder {
        private List<ItemAttributeValueConverter<?>> converters = new ArrayList<>();
        private ItemAttributeValueConverterChain parentChain;

        private Builder() {}

        public Builder addConverter(ItemAttributeValueConverter<?> converter) {
            this.converters.add(converter);
            return this;
        }

        public Builder addConverters(ItemAttributeValueConverter<?> converter) {
            this.converters.add(converter);
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
