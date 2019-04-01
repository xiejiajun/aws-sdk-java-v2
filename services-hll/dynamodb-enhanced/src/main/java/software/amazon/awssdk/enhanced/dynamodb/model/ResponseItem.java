package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Map;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.AsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Table;
import software.amazon.awssdk.enhanced.dynamodb.internal.model.DefaultResponseItem;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

/**
 * An item that is returned by DynamoDB. An item is a single, unique entry in a DynamoDB table.
 *
 * A {@code ResponseItem} is a {@code Map<String, ConvertableItemAttributeValue>}. Each attribute can be converted into a Java
 * type via {@link ConvertableItemAttributeValue#as(Class)} or similar methods. For example:
 * {@code String id = responseItem.attribute("id").asString();}
 *
 * @see Table
 * @see AsyncTable
 */
@SdkPublicApi
@ThreadSafe
@Immutable
public interface ResponseItem extends AttributeAware<ConvertableItemAttributeValue>,
                                      ToCopyableBuilder<ResponseItem.Builder, ResponseItem> {
    /**
     * Create a builder that can be used for configuring and creating a {@link ResponseItem}.
     */
    static Builder builder() {
        return DefaultResponseItem.builder();
    }

    /**
     * A builder that can be used for configuring and creating a {@link ResponseItem}.
     */
    interface Builder extends AttributeAware.Builder<ConvertableItemAttributeValue>,
                              CopyableBuilder<ResponseItem.Builder, ResponseItem> {
        @Override
        Builder putAttributes(Map<String, ConvertableItemAttributeValue> attributeValues);

        @Override
        Builder putAttribute(String attributeKey, ConvertableItemAttributeValue attributeValue);

        @Override
        Builder removeAttribute(String attributeKey);

        @Override
        Builder clearAttributes();

        @Override
        ResponseItem build();
    }
}
