package software.amazon.awssdk.enhanced.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class EnhancedClientOverrideConfiguration
        implements ToCopyableBuilder<EnhancedClientOverrideConfiguration.Builder, EnhancedClientOverrideConfiguration> {


    /**
     * The builder for the high-level DynamoDB client. This is used by customers to configure the high-level client with default
     * values to be applied across all client operations.
     *
     * This can be created via {@link DynamoDbEnhancedClient#builder()}.
     */
    interface Builder extends CopyableBuilder<EnhancedClientOverrideConfiguration.Builder, EnhancedClientOverrideConfiguration> {
        Builder converters(Iterable<ItemAttributeValueConverter<?>> converters);
        Builder addConverter(ItemAttributeValueConverter<?> converter);
        Builder clearConverters();

        EnhancedClientOverrideConfiguration build();
    }

    private final class DefaultBuilder implements Builder {

    }
}
