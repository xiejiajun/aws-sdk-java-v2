package software.amazon.awssdk.enhanced.dynamodb.converter;

import java.util.Optional;
import software.amazon.awssdk.enhanced.dynamodb.model.Item;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ConversionContext implements ToCopyableBuilder<ConversionContext.Builder, ConversionContext> {
    private final String attributeName;
    private final Item parent;
    private final ItemAttributeValueConverter converter;

    private ConversionContext(Builder builder) {
        this.attributeName = Validate.paramNotNull(builder.attributeName, "attributeName");
        this.converter = Validate.paramNotNull(builder.converter, "converter");
        this.parent = builder.parent;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * The name of the attribute being converted.
     */
    public String attributeName() {
        return this.attributeName;
    }

    public ItemAttributeValueConverter converter() {
        return converter;
    }

    /**
     * The item that contains the attribute being converted, or Optional.empty() if there is no parent (eg. for root items).
     */
    public Optional<Item> parent() {
        return Optional.ofNullable(this.parent);
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public final static class Builder implements CopyableBuilder<ConversionContext.Builder, ConversionContext> {
        private String attributeName;
        private Item parent;
        private ItemAttributeValueConverter converter;

        private Builder() {}

        public Builder(ConversionContext context) {
            this.attributeName = context.attributeName;
            this.parent = context.parent;
        }

        public Builder attributeName(String attributeName) {
            this.attributeName = attributeName;
            return this;
        }

        public Builder converter(ItemAttributeValueConverter converter) {
            this.converter = converter;
            return this;
        }

        public Builder parent(Item parent) {
            this.parent = parent;
            return this;
        }

        public ConversionContext build() {
            return new ConversionContext(this);
        }
    }
}
