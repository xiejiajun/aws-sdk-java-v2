package software.amazon.awssdk.enhanced.dynamodb.converter.bundled;

import static java.util.stream.Collectors.toList;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeConvertingVisitor;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;

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
        return input.convert(new Visitor());
    }

    private class Visitor extends TypeConvertingVisitor<String> {
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
            return value.asString(StandardCharsets.UTF_8);
        }

        @Override
        public String convertBoolean(Boolean value) {
            return value.toString();
        }

        @Override
        public String convertSetOfStrings(Set<String> value) {
            return join(value);
        }

        @Override
        public String convertSetOfNumbers(Set<String> value) {
            return join(value);
        }

        @Override
        public String convertSetOfBytes(Set<SdkBytes> value) {
            Collection<String> values = value.stream()
                                             .map(b -> b.asString(StandardCharsets.UTF_8))
                                             .collect(toList());
            return String.join(", ", values);
        }

        private String join(Iterable<String> input) {
            return String.join(", ", input);
        }
    }
}
