package software.amazon.awssdk.enhanced.dynamodb.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.Collections;
import org.junit.Test;
import software.amazon.awssdk.core.SdkBytes;

public class TypeConvertingVisitorTest {
    @Test
    public void defaultConvertersThrowExceptions() {
        assertThat(DefaultVisitor.INSTANCE.convert(ItemAttributeValue.nullValue())).isEqualTo(null);

        assertDefaultConversionFails(ItemAttributeValue.fromString("foo"));
        assertDefaultConversionFails(ItemAttributeValue.fromNumber("1"));
        assertDefaultConversionFails(ItemAttributeValue.fromBoolean(true));
        assertDefaultConversionFails(ItemAttributeValue.fromBytes(SdkBytes.fromUtf8String("")));
        assertDefaultConversionFails(ItemAttributeValue.fromSetOfStrings(Collections.emptyList()));
        assertDefaultConversionFails(ItemAttributeValue.fromSetOfNumbers(Collections.emptyList()));
        assertDefaultConversionFails(ItemAttributeValue.fromSetOfBytes(Collections.emptyList()));
        assertDefaultConversionFails(ItemAttributeValue.fromListOfAttributeValues(Collections.emptyList()));
        assertDefaultConversionFails(ItemAttributeValue.fromMap(Collections.emptyMap()));
    }

    private void assertDefaultConversionFails(ItemAttributeValue attributeValue) {
        assertThatThrownBy(() -> DefaultVisitor.INSTANCE.convert(attributeValue)).isInstanceOf(IllegalStateException.class);
    }


    private static class DefaultVisitor extends TypeConvertingVisitor<Void> {
        private static final DefaultVisitor INSTANCE = new DefaultVisitor();

        protected DefaultVisitor() {
            super(Void.class);
        }
    }

}