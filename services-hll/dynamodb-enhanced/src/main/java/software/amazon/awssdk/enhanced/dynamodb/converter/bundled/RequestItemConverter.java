package software.amazon.awssdk.enhanced.dynamodb.converter.bundled;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.InstanceOfConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ItemAttributeValueConverterChain;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.RequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;

/**
 * A converter between {@link RequestItem} and {@link ItemAttributeValue}.
 */
@SdkPublicApi
@ThreadSafe
public class RequestItemConverter extends InstanceOfConverter<RequestItem> {
    public RequestItemConverter() {
        super(RequestItem.class);
    }

    @Override
    protected ItemAttributeValue doToAttributeValue(RequestItem input, ConversionContext conversionContext) {
        ItemAttributeValueConverter converter = ItemAttributeValueConverterChain.builder()
                                                                                .addConverters(input.converters())
                                                                                .parent(conversionContext.converter())
                                                                                .build();

        ConversionContext.Builder conversionContextBuilder = conversionContext.toBuilder()
                                                                              .converter(converter);

        Map<String, ItemAttributeValue> result = new LinkedHashMap<>();
        input.attributes().forEach((key, value) -> result.put(key, toItemAttributeValue(conversionContextBuilder, key, value)));
        return ItemAttributeValue.fromMap(result);
    }

    private ItemAttributeValue toItemAttributeValue(ConversionContext.Builder contextBuilder, String key, Object value) {
        ConversionContext context = contextBuilder.attributeName(key).build();
        return context.converter().toAttributeValue(value, context);
    }

    @Override
    protected RequestItem doFromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
        throw new UnsupportedOperationException("Cannot convert an ItemAttributeValue to a RequestItem.");
    }
}
