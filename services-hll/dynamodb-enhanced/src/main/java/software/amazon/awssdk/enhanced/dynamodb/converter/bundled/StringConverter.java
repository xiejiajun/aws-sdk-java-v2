package software.amazon.awssdk.enhanced.dynamodb.converter.bundled;

import static java.util.stream.Collectors.toList;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ExactInstanceOfConverter;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeConvertingVisitor;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.utils.BinaryUtils;

/**
 * A converter between {@link String} and {@link ItemAttributeValue}.
 */
@SdkPublicApi
@ThreadSafe
@Immutable
public class StringConverter extends ExactInstanceOfConverter<String> {
    public StringConverter() {
        super(String.class);
    }

    @Override
    protected ItemAttributeValue doToAttributeValue(String input, ConversionContext context) {
        return ItemAttributeValue.fromString(input);
    }

    @Override
    protected String doFromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
        return input.convert(new TypeConvertingVisitor<String>(String.class, StringConverter.class) {
            @Override
            public String convertString(String value) {
                return value;
            }

            @Override
            public String convertNumber(String value) {
                return value;
            }

            @Override
            public String convertBytes(SdkBytes value) {
                return "0x" + BinaryUtils.toHex(value.asByteArray());
            }

            @Override
            public String convertBoolean(Boolean value) {
                return value.toString();
            }

            @Override
            public String convertSetOfStrings(List<String> value) {
                return value.toString();
            }

            @Override
            public String convertSetOfNumbers(List<String> value) {
                return value.toString();
            }

            @Override
            public String convertSetOfBytes(List<SdkBytes> value) {
                return value.stream()
                            .map(this::convertBytes)
                            .collect(toList())
                            .toString();
            }

            @Override
            public String convertMap(Map<String, ItemAttributeValue> value) {
                BinaryOperator<Object> throwingMerger = (l, r) -> {
                    // Should not happen: we're converting from map.
                    throw new IllegalStateException();
                };

                return value.entrySet().stream()
                            .collect(Collectors.toMap(i -> i.getKey(), i -> convert(i.getValue()),
                                                      throwingMerger, LinkedHashMap::new))
                            .toString();
            }

            @Override
            public String convertListOfAttributeValues(Collection<ItemAttributeValue> value) {
                return value.stream()
                            .map(this::convert)
                            .collect(toList())
                            .toString();
            }
        });
    }
}
