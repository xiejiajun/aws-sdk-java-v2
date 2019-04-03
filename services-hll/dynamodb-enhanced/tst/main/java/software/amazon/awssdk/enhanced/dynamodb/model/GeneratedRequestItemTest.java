package software.amazon.awssdk.enhanced.dynamodb.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class GeneratedRequestItemTest {
    @Test
    public void allConfigurationMethodsWork() {
        AttributeValue attributeValue = AttributeValue.builder().s("a").build();
        AttributeValue attributeValue2 = AttributeValue.builder().s("b").build();

        GeneratedRequestItem item = GeneratedRequestItem.builder()
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