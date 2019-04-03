package software.amazon.awssdk.enhanced.dynamodb.internal.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;

import org.junit.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;

public class ItemAttributeValueConverterChainTest {
    @Test
    public void exactInstanceOfConvertersAreHighestPriority() {
        ItemAttributeValueConverter exactInstanceConverter = converter(ConversionCondition.isExactInstanceOf(String.class));
        ItemAttributeValueConverter instanceConverter = converter(ConversionCondition.isInstanceOf(CharSequence.class));

        ItemAttributeValueConverterChain chain =
                ItemAttributeValueConverterChain.builder()
                                                .addConverter(instanceConverter)
                                                .addConverter(exactInstanceConverter)
                                                .build();

        chain.toAttributeValue("", ConversionContext.builder().converter(chain).build());

        Mockito.verify(exactInstanceConverter).toAttributeValue(any(), any());
        Mockito.verify(instanceConverter, never()).toAttributeValue(any(), any());
    }

    @Test
    public void instanceOfConvertersWorkForSubtypes() {
        ItemAttributeValueConverter instanceConverter = converter(ConversionCondition.isInstanceOf(CharSequence.class));

        ItemAttributeValueConverterChain chain =
                ItemAttributeValueConverterChain.builder()
                                                .addConverter(instanceConverter)
                                                .build();

        chain.toAttributeValue("", ConversionContext.builder().converter(chain).build());

        Mockito.verify(instanceConverter).toAttributeValue(any(), any());
    }

    @Test
    public void matchingInstanceOfConvertersFavorFirstAdded() {
        ItemAttributeValueConverter instanceConverter = converter(ConversionCondition.isInstanceOf(CharSequence.class));
        ItemAttributeValueConverter instanceConverter2 = converter(ConversionCondition.isInstanceOf(CharSequence.class));

        ItemAttributeValueConverterChain chain =
                ItemAttributeValueConverterChain.builder()
                                                .addConverter(instanceConverter)
                                                .addConverter(instanceConverter2)
                                                .build();

        chain.toAttributeValue("", ConversionContext.builder().converter(chain).build());

        Mockito.verify(instanceConverter).toAttributeValue(any(), any());
        Mockito.verify(instanceConverter2, never()).toAttributeValue(any(), any());
    }

    @Test
    public void matchingExactInstanceOfConvertersFavorFirstAdded() {
        ItemAttributeValueConverter instanceConverter = converter(ConversionCondition.isExactInstanceOf(String.class));
        ItemAttributeValueConverter instanceConverter2 = converter(ConversionCondition.isExactInstanceOf(String.class));

        ItemAttributeValueConverterChain chain =
                ItemAttributeValueConverterChain.builder()
                                                .addConverter(instanceConverter)
                                                .addConverter(instanceConverter2)
                                                .build();

        chain.toAttributeValue("", ConversionContext.builder().converter(chain).build());

        Mockito.verify(instanceConverter).toAttributeValue(any(), any());
        Mockito.verify(instanceConverter2, never()).toAttributeValue(any(), any());
    }

    @Test
    public void noMatchingConverterDelegatesToParent() {
        ItemAttributeValueConverter exactInstanceConverter = converter(ConversionCondition.isExactInstanceOf(String.class));
        ItemAttributeValueConverter instanceConverter = converter(ConversionCondition.isInstanceOf(String.class));
        ItemAttributeValueConverter parent = converter(ConversionCondition.isInstanceOf(Object.class));

        ItemAttributeValueConverterChain chain =
                ItemAttributeValueConverterChain.builder()
                                                .addConverter(exactInstanceConverter)
                                                .addConverter(instanceConverter)
                                                .parent(parent)
                                                .build();

        chain.toAttributeValue(1, ConversionContext.builder().converter(chain).build());

        Mockito.verify(parent).toAttributeValue(any(), any());
        Mockito.verify(exactInstanceConverter, never()).toAttributeValue(any(), any());
        Mockito.verify(instanceConverter, never()).toAttributeValue(any(), any());
    }

    @Test
    public void noMatchingConverterWithNullParentFails() {
        ItemAttributeValueConverter exactInstanceConverter = converter(ConversionCondition.isExactInstanceOf(String.class));
        ItemAttributeValueConverter instanceConverter = converter(ConversionCondition.isInstanceOf(String.class));

        ItemAttributeValueConverterChain chain =
                ItemAttributeValueConverterChain.builder()
                                                .addConverter(exactInstanceConverter)
                                                .addConverter(instanceConverter)
                                                .build();

        assertThatThrownBy(() -> chain.toAttributeValue(1, ConversionContext.builder().converter(chain).build()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void nestedChainsDoNotRewrap() {
        ItemAttributeValueConverterChain chain = ItemAttributeValueConverterChain.builder().build();
        ItemAttributeValueConverterChain chain2 = ItemAttributeValueConverterChain.builder().addConverter(chain).build();
        assertThat(chain == chain2).isTrue();
    }

    private ItemAttributeValueConverter converter(ConversionCondition condition) {
        ItemAttributeValueConverter converter = Mockito.mock(ItemAttributeValueConverter.class);
        Mockito.when(converter.defaultConversionCondition()).thenReturn(condition);
        return converter;
    }
}