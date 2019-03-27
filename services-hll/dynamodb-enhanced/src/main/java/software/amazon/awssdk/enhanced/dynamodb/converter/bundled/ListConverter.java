package software.amazon.awssdk.enhanced.dynamodb.converter.bundled;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeConvertingVisitor;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.utils.Validate;

public class ListConverter extends ExactInstanceOfConverter<List<?>> {
    public ListConverter() {
        super(List.class);
    }

    @Override
    protected ItemAttributeValue doToAttributeValue(List<?> input, ConversionContext context) {
        List<ItemAttributeValue> attributeValues = new ArrayList<>();
        for (Object object : input) {
            attributeValues.add(context.converter().toAttributeValue(object, context));
        }
        return ItemAttributeValue.fromListOfAttributeValues(attributeValues);
    }

    @Override
    protected List<?> doFromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
        List<TypeToken<?>> listTypeParameters = desiredType.representedClassParameters();

        Validate.isTrue(listTypeParameters.size() == 1,
                        "The desired List type appears to be parameterized with more than 1 type: %s", desiredType);
        TypeToken<?> parameterType = listTypeParameters.get(0);

        return input.convert(new TypeConvertingVisitor<List<?>>() {
            @Override
            public List<?> convertSetOfStrings(Set<String> value) {
                return convertCollection(value, ItemAttributeValue::fromString);
            }

            @Override
            public List<?> convertSetOfNumbers(Set<String> value) {
                return convertCollection(value, ItemAttributeValue::fromNumber);
            }

            @Override
            public List<?> convertSetOfBytes(Set<SdkBytes> value) {
                return convertCollection(value, ItemAttributeValue::fromBytes);
            }

            @Override
            public List<?> convertListOfAttributeValues(Collection<ItemAttributeValue> value) {
                return convertCollection(value, Function.identity());
            }

            private <T> List<?> convertCollection(Collection<T> collection,
                                                  Function<T, ItemAttributeValue> toAttributeValueFunction) {
                return collection.stream()
                                .map(toAttributeValueFunction)
                                .map(v -> context.converter().fromAttributeValue(v, parameterType, context))
                                .collect(Collectors.toList());
            }
        });
    }
}
