package software.amazon.awssdk.enhanced.dynamodb.converter;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.converter.bundled.IdentityConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bundled.InstantConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bundled.IntegerConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bundled.ListConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bundled.MapConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bundled.RequestItemConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bundled.ResponseItemConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bundled.StringConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ItemAttributeValueConverterChain;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;

/**
 * A {@link ItemAttributeValueConverter} that includes all of the converters built into the SDK.
 *
 * This is the root converter for all created {@link DynamoDbEnhancedClient}s and {@link DynamoDbEnhancedAsyncClient}s.
 *
 * This can be created via {@link #create()}.
 */
@SdkPublicApi
@ThreadSafe
public final class DefaultConverterChain implements ItemAttributeValueConverter {
    private static final ItemAttributeValueConverter CHAIN;

    static {
        CHAIN = ItemAttributeValueConverterChain.builder()
                                                .addConverter(new InstantConverter())
                                                .addConverter(new IntegerConverter())
                                                .addConverter(new StringConverter())
                                                .addConverter(new ListConverter())
                                                .addConverter(new MapConverter())
                                                .addConverter(new RequestItemConverter())
                                                .addConverter(new ResponseItemConverter())
                                                .addConverter(new IdentityConverter())
                                                .build();
    }

    private DefaultConverterChain() {}

    /**
     * Create a default convert chain that contains all of the converters built into the SDK.
     */
    public static DefaultConverterChain create() {
        return new DefaultConverterChain();
    }

    @Override
    public ConversionCondition defaultConversionCondition() {
        return CHAIN.defaultConversionCondition();
    }

    @Override
    public ItemAttributeValue toAttributeValue(Object input, ConversionContext context) {
        return CHAIN.toAttributeValue(input, context);
    }

    @Override
    public Object fromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
        return CHAIN.fromAttributeValue(input, desiredType, context);
    }
}
