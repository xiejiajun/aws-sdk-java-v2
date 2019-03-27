package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;

public abstract class Item {
    private final List<ItemAttributeValueConverter> converters;

    public Item(Builder builder) {
        this.converters = new ArrayList<>(builder.converters);
    }

    public List<ItemAttributeValueConverter> converters() {
        return Collections.unmodifiableList(converters);
    }

    public static abstract class Builder {
        private List<ItemAttributeValueConverter> converters = new ArrayList<>();

        public Builder addConverter(ItemAttributeValueConverter converter) {
            this.converters.add(converter);
            return this;
        }

        public Builder clearConverters() {
            this.converters.clear();
            return this;
        }
    }
}
