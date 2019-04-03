package software.amazon.awssdk.enhanced.dynamodb.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;

import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bundled.InstantConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bundled.IntegerConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bundled.StringConverter;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class ResponseItemTest {
    @Test
    public void allConfigurationMethodsWork() {
        ConvertableItemAttributeValue attributeValue = Mockito.mock(ConvertableItemAttributeValue.class);
        ConvertableItemAttributeValue attributeValue2 = Mockito.mock(ConvertableItemAttributeValue.class);

        ResponseItem item = ResponseItem.builder()
                                        .putAttribute("toremove", attributeValue)
                                        .clearAttributes()
                                        .putAttribute("toremove2", attributeValue)
                                        .removeAttribute("toremove2")
                                        .putAttributes(Collections.singletonMap("foo", attributeValue))
                                        .putAttribute("foo2", attributeValue2)
                                        .build();

        assertThat(item.attributes()).hasSize(2);
        assertThat(item.attribute("foo")).isEqualTo(attributeValue);
        assertThat(item.attribute("foo2")).isEqualTo(attributeValue2);
    }
}