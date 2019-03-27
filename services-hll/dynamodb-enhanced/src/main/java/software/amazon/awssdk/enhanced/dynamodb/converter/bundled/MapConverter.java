package software.amazon.awssdk.enhanced.dynamodb.converter.bundled;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeConvertingVisitor;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.utils.Validate;

public class MapConverter extends ExactInstanceOfConverter<Map<?, ?>> {
    private final Function<Object, String> keyToStringConverter;

    public MapConverter() {
        this(Object::toString);
    }

    public MapConverter(Function<Object, String> keyToStringConverter) {
        super(Map.class);
        this.keyToStringConverter = keyToStringConverter;
    }

    @Override
    protected ItemAttributeValue doToAttributeValue(Map<?, ?> input, ConversionContext context) {
        Map<String, ItemAttributeValue> result = new LinkedHashMap<>();
        input.forEach((key, value) -> result.put(keyToStringConverter.apply(input),
                                                 context.converter().toAttributeValue(value, context)));
        return ItemAttributeValue.fromMap(result);
    }

    @Override
    protected Map<?, ?> doFromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
        List<TypeToken<?>> mapTypeParameters = desiredType.representedClassParameters();

        Validate.isTrue(mapTypeParameters.size() == 2,
                        "The desired Map type appears to be parameterized with more than 2 types: %s", desiredType);
        TypeToken<?> keyType = mapTypeParameters.get(0);
        TypeToken<?> valueType = mapTypeParameters.get(1);

        return input.convert(new TypeConvertingVisitor<Map<?, ?>>() {
            @Override
            public Map<?, ?> convertMap(Map<String, ItemAttributeValue> value) {
                Map<Object, Object> result = new LinkedHashMap<>();
                value.forEach((k, v) -> {
                    result.put(context.converter().fromAttributeValue(ItemAttributeValue.fromString(k), keyType, context),
                               context.converter().fromAttributeValue(v, valueType, context));
                });
                return result;
            }
        });
    }
}
