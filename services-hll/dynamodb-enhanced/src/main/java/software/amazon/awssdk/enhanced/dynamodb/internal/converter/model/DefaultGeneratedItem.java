package software.amazon.awssdk.enhanced.dynamodb.internal.converter.model;

import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedRequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedResponseItem;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DefaultGeneratedItem extends DefaultItem<AttributeValue> implements GeneratedRequestItem, GeneratedResponseItem {
    private DefaultGeneratedItem(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder
            extends DefaultItem.Builder<AttributeValue, Builder>
            implements GeneratedRequestItem.Builder, GeneratedResponseItem.Builder {
        private Builder() {}

        private Builder(DefaultGeneratedItem item) {
            super(item);
        }

        @Override
        public DefaultGeneratedItem build() {
            return new DefaultGeneratedItem(this);
        }
    }
}
