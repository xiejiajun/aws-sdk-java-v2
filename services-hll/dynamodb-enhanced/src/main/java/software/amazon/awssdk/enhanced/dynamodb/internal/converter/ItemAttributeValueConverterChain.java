package software.amazon.awssdk.enhanced.dynamodb.internal.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.model.ConverterAware;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
@ThreadSafe
public final class ItemAttributeValueConverterChain implements ItemAttributeValueConverter {
    private static final Logger log = Logger.loggerFor(ItemAttributeValueConverterChain.class);

    private final List<ItemAttributeValueConverter> converters;
    private final List<ItemAttributeValueConverter> instanceOfConverters = new ArrayList<>();
    private final ConcurrentHashMap<Class<?>, ItemAttributeValueConverter> converterCache = new ConcurrentHashMap<>();
    private final ItemAttributeValueConverter parent;

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

        this.parent = builder.parent;
    }

    public static ItemAttributeValueConverterChain.Builder builder() {
        return new ItemAttributeValueConverterChain.Builder();
    }

    public static ItemAttributeValueConverterChain create(Collection<? extends ItemAttributeValueConverter> converters) {
        return builder().addConverters(converters).build();
    }

    @Override
    public ConversionCondition defaultConversionCondition() {
        return ConversionCondition.isInstanceOf(Object.class);
    }

    @Override
    public ItemAttributeValue toAttributeValue(Object input, ConversionContext context) {
        return invokeConverter(input.getClass(), c -> c.toAttributeValue(input, context));
    }

    @Override
    public Object fromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
        return invokeConverter(desiredType.representedClass(), c -> c.fromAttributeValue(input, desiredType, context));
    }

    private <T> T invokeConverter(Class<?> type, Function<ItemAttributeValueConverter, T> converterInvoker) {
        log.debug(() -> "Loading converter for " + type.getTypeName() + ".");

        ItemAttributeValueConverter converter = converterCache.get(type);

        if (converter != null) {
            return converterInvoker.apply(converter);
        }

        converter = findConverter(type);

        if (converter == null && parent != null) {
            log.debug(() -> "Converter not found in this chain for " + type.getTypeName() + ". Parent will be used.");
            converter = parent;
        }

        if (converter == null) {
            throw new IllegalArgumentException("Converter not found for " + type.getTypeName() + ".");
        }


        T result = converterInvoker.apply(converter);

        if (shouldCache(type)) {
            // Only cache after successful conversion, to prevent leaking memory.
            this.converterCache.put(type, converter);
        }

        return result;
    }

    private boolean shouldCache(Class<?> type) {
        return !type.isAnonymousClass();
    }

    private ItemAttributeValueConverter findConverter(Class<?> type) {
        log.debug(() -> "Converter not cached for " + type.getTypeName() + ". " +
                        "Checking for an instanceof converter match.");

        for (ItemAttributeValueConverter converter : instanceOfConverters) {
            InstanceOfConversionCondition condition = (InstanceOfConversionCondition)
                    converter.defaultConversionCondition();

            if (condition.converts(type)) {
                return converter;
            }
        }

        return null;
    }

    public static class Builder implements ConverterAware.Builder {
        private List<ItemAttributeValueConverter> converters = new ArrayList<>();
        private ItemAttributeValueConverter parent;

        private Builder() {}

        @Override
        public Builder addConverters(Collection<? extends ItemAttributeValueConverter> converters) {
            this.converters.addAll(converters);
            return this;
        }

        @Override
        public Builder addConverter(ItemAttributeValueConverter converter) {
            this.converters.add(converter);
            return this;
        }

        @Override
        public Builder clearConverters() {
            this.converters.clear();
            return this;
        }

        public Builder parent(ItemAttributeValueConverter parent) {
            this.parent = parent;
            return this;
        }

        public ItemAttributeValueConverterChain build() {
            if (converters.size() == 1 && converters.get(0) instanceof ItemAttributeValueConverterChain && parent == null) {
                // Optimization: Don't wrap chains in chains
                return (ItemAttributeValueConverterChain) converters.get(0);
            }

            return new ItemAttributeValueConverterChain(this);
        }
    }
}
