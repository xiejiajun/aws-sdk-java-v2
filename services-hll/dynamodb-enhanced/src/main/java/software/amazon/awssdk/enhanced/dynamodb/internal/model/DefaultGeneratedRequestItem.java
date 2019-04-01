package software.amazon.awssdk.enhanced.dynamodb.internal.model;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedRequestItem;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * The default implementation of {@link GeneratedRequestItem}.
 */
@SdkInternalApi
@ThreadSafe
public class DefaultGeneratedRequestItem extends DefaultItem<AttributeValue> implements GeneratedRequestItem {
    private DefaultGeneratedRequestItem(Builder builder) {
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
            implements GeneratedRequestItem.Builder {
        private Builder() {}

        private Builder(DefaultGeneratedRequestItem item) {
            super(item);
        }

        @Override
        public DefaultGeneratedRequestItem build() {
            return new DefaultGeneratedRequestItem(this);
        }
    }
}
