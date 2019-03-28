package software.amazon.awssdk.enhanced.dynamodb.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bundled.AttributeConverter;
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

@SdkInternalApi
@ThreadSafe
public class DefaultConverterChain implements ItemAttributeValueConverter {
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
                                                .addConverter(new AttributeConverter())
                                                .build();
    }

    private DefaultConverterChain() {}

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
