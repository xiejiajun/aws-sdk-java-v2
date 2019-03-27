package software.amazon.awssdk.enhanced.dynamodb.converter.bundled;

import java.time.Instant;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeConvertingVisitor;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;

public class InstantConverter extends ExactInstanceOfConverter<Instant> {
    public InstantConverter() {
        super(Instant.class);
    }

    @Override
    protected ItemAttributeValue doToAttributeValue(Instant input, ConversionContext context) {
        return ItemAttributeValue.fromNumber(Long.toString(input.toEpochMilli()));
    }

    @Override
    protected Instant doFromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
        return input.convert(new TypeConvertingVisitor<Instant>() {
            @Override
            public Instant convertString(String value) {
                return Instant.parse(value);
            }

            @Override
            public Instant convertNumber(String value) {
                return Instant.ofEpochMilli(Long.parseLong(value));
            }
        });
    }
}
