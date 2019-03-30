package software.amazon.awssdk.enhanced.dynamodb.converter.bundled;

import java.util.LinkedHashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ExactInstanceOfConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.model.DefaultConvertableItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.ConvertableItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.ResponseItem;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeConvertingVisitor;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;

/**
 * A converter between {@link ResponseItem} and {@link ItemAttributeValue}.
 */
@SdkPublicApi
@ThreadSafe
public class ResponseItemConverter extends ExactInstanceOfConverter<ResponseItem> {
    public ResponseItemConverter() {
        super(ResponseItem.class);
    }

    @Override
    protected ItemAttributeValue doToAttributeValue(ResponseItem input, ConversionContext context) {
        throw new UnsupportedOperationException("Cannot convert a ResponseItem to an ItemAttributeValue.");
    }

    @Override
    protected ResponseItem doFromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
        return input.convert(new TypeConvertingVisitor<ResponseItem>(ResponseItem.class, ResponseItemConverter.class) {
            @Override
            public ResponseItem convertMap(Map<String, ItemAttributeValue> value) {
                Map<String, ConvertableItemAttributeValue> attributes = new LinkedHashMap<>();
                value.forEach((k, v) -> attributes.put(k, toConvertableAttribute(context, k, v)));

                return ResponseItem.builder()
                                   .putAttributes(attributes)
                                   .build();
            }
        });
    }

    private ConvertableItemAttributeValue toConvertableAttribute(ConversionContext context, String key, ItemAttributeValue value) {
        return DefaultConvertableItemAttributeValue.builder()
                                                   .conversionContext(context.toBuilder()
                                                                             .attributeName(key)
                                                                             .build())
                                                   .attributeValue(value)
                                                   .build();
    }
}
