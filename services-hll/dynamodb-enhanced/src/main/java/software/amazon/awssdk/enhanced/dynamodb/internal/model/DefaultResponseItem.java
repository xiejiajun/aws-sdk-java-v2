package software.amazon.awssdk.enhanced.dynamodb.internal.model;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.model.ConvertableItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.RequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ResponseItem;

/**
 * The default implementation of {@link ResponseItem}.
 */
@SdkInternalApi
@ThreadSafe
public class DefaultResponseItem extends DefaultItem<ConvertableItemAttributeValue> implements ResponseItem {
    private DefaultResponseItem(Builder builder) {
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
            extends DefaultItem.Builder<ConvertableItemAttributeValue, Builder>
            implements ResponseItem.Builder {
        private Builder() {}

        private Builder(DefaultResponseItem item) {
            super(item);
        }

        @Override
        public DefaultResponseItem build() {
            return new DefaultResponseItem(this);
        }
    }
}
