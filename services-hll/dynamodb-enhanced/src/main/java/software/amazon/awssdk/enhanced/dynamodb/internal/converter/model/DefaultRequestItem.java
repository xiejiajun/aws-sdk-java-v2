package software.amazon.awssdk.enhanced.dynamodb.internal.converter.model;

import software.amazon.awssdk.enhanced.dynamodb.model.RequestItem;

public class DefaultRequestItem extends DefaultItem<Object> implements RequestItem {
    private DefaultRequestItem(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public DefaultRequestItem.Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder extends DefaultItem.Builder<Object, Builder> implements RequestItem.Builder {
        private Builder() {}

        private Builder(DefaultRequestItem item) {
            super(item);
        }

        @Override
        public DefaultRequestItem build() {
            return new DefaultRequestItem(this);
        }
    }
}
