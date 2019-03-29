package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Collection;
import java.util.List;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.DefaultConverterChain;
import software.amazon.awssdk.utils.Validate;

/**
 * An interface applied to all objects that wish to expose their underlying {@link ItemAttributeValueConverter}s.
 *
 * See {@link ItemAttributeValueConverter} for a detailed explanation of how the enhanced client converts between Java types
 * and DynamoDB types.
 */
@SdkPublicApi
public interface ConverterAware {
    /**
     * Retrieve all converters that were directly configured on this object.
     */
    List<ItemAttributeValueConverter> converters();

    /**
     * An interface applied to all objects that can be configured with {@link ItemAttributeValueConverter}s.
     *
     * See {@link ItemAttributeValueConverter} for a detailed explanation of how the enhanced client converts between Java types
     * and DynamoDB types.
     */
    interface Builder {
        /**
         * Add all of the provided converters to this builder, in the order of the provided collection.
         *
         * Converters earlier in the provided list take precedence over the ones later in the list, even if the later ones
         * refer to a more specific type. Converters should be added in an order from most-specific to least-specific.
         *
         * Converters configured in {@link RequestItem.Builder} always take precedence over the ones configured in
         * {@link DynamoDbEnhancedClient.Builder}.
         *
         * Converters configured in {@link DynamoDbEnhancedClient.Builder} always take precedence over the ones provided by the
         * {@link DefaultConverterChain}.
         *
         * @see ItemAttributeValueConverter
         */
        Builder addConverters(Collection<? extends ItemAttributeValueConverter> converters);

        /**
         * Add a converter to this builder.
         *
         * Converters added earlier take precedence over the ones added later, even if the later ones refer to
         * a more specific type. Converters should be added in an order from most-specific to least-specific.
         *
         * Converters configured in {@link RequestItem.Builder} always take precedence over the ones configured in
         * {@link DynamoDbEnhancedClient.Builder}.
         *
         * Converters configured in {@link DynamoDbEnhancedClient.Builder} always take precedence over the ones provided by the
         * {@link DefaultConverterChain}.
         */
        Builder addConverter(ItemAttributeValueConverter converter);

        /**
         * Reset the converters that were previously added with {@link #addConverters(Collection)} or
         * {@link #addConverter(ItemAttributeValueConverter)}.
         *
         * This <b>does not</b> reset converters configured elsewhere. Converters configured in other locations, such as in the
         * {@link DefaultConverterChain}, will still be used.
         */
        Builder clearConverters();
    }
}
