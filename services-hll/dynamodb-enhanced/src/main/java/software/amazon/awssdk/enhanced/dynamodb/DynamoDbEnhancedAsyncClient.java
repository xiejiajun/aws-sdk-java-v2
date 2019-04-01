package software.amazon.awssdk.enhanced.dynamodb;

import java.util.Collection;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.DefaultDynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.ConverterAware;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

/**
 * A synchronous client for interacting with DynamoDB. This can be created using the static {@link #builder()} method.
 *
 * In most cases, this is the correct client to use for interacting with DynamoDB. The low-level {@link DynamoDbClient} is
 * generated automatically and includes the latest service features. This client is hand-written and provides a richer,
 * Java-optimized experience for DynamoDB.
 */
@SdkPublicApi
@ThreadSafe
@Immutable
public interface DynamoDbEnhancedAsyncClient extends ToCopyableBuilder<DynamoDbEnhancedAsyncClient.Builder, DynamoDbEnhancedAsyncClient>,
                                                     SdkAutoCloseable {
    /**
     * Create a {@link DynamoDbEnhancedAsyncClient} with default configuration.
     *
     * Equivalent statements:
     * <ol>
     *     <li>{@code DynamoDbEnhancedAsyncClient.builder().build()}</li>
     * </ol>
     */
    static DynamoDbEnhancedAsyncClient create() {
        return builder().build();
    }

    /**
     * Create a {@link DynamoDbEnhancedAsyncClient.Builder} that can be used to create a {@link DynamoDbEnhancedAsyncClient} with custom
     * configuration.
     */
    static Builder builder() {
        return DefaultDynamoDbEnhancedAsyncClient.builder();
    }

    AsyncTable table(String tableName);

    /**
     * The builder for the high-level DynamoDB client. This is used by customers to configure the high-level client with default
     * values to be applied across all client operations.
     *
     * This can be created via {@link DynamoDbEnhancedAsyncClient#builder()}.
     */
    interface Builder extends CopyableBuilder<Builder, DynamoDbEnhancedAsyncClient>, ConverterAware.Builder {
        Builder dynamoDbClient(DynamoDbAsyncClient client);

        @Override
        Builder addConverters(Collection<? extends ItemAttributeValueConverter> converters);

        @Override
        Builder addConverter(ItemAttributeValueConverter converter);

        @Override
        Builder clearConverters();

        DynamoDbEnhancedAsyncClient build();
    }
}
