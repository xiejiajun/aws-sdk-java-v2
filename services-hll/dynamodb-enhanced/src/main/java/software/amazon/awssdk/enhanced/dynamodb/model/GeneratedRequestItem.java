package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Map;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.internal.model.DefaultGeneratedRequestItem;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

/**
 * A user-friendly {@code Map<String, AttributeValue>} that represents a requested item to DynamoDB.
 *
 * An instance of this is usually accessed through {@link RequestItem#toGeneratedRequestItem()}.
 */
@SdkPublicApi
@ThreadSafe
@Immutable
public interface GeneratedRequestItem extends AttributeAware<AttributeValue>,
                                              ToCopyableBuilder<GeneratedRequestItem.Builder, GeneratedRequestItem> {
    /**
     * Create a builder for configuring and creating a {@link GeneratedRequestItem}.
     */
    static Builder builder() {
        return DefaultGeneratedRequestItem.builder();
    }

    /**
     * A builder for configuring and creating a {@link GeneratedRequestItem}.
     */
    interface Builder extends AttributeAware.Builder<AttributeValue>,
                              CopyableBuilder<GeneratedRequestItem.Builder, GeneratedRequestItem>{
        @Override
        Builder putAttributes(Map<String, AttributeValue> attributeValues);

        @Override
        Builder putAttribute(String attributeKey, AttributeValue attributeValue);

        @Override
        Builder removeAttribute(String attributeKey);

        @Override
        Builder clearAttributes();

        GeneratedRequestItem build();
    }
}
