package software.amazon.awssdk.enhanced.dynamodb.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.Test;
import software.amazon.awssdk.core.SdkBytes;

public class ItemAttributeValueTest {
    @Test
    public void fromMethodsCreateCorrectTypes() {
        assertThat(ItemAttributeValue.nullValue()).satisfies(v -> {
            assertThat(v.isNull()).isTrue();
            assertThat(v.type()).isEqualTo(ItemAttributeValueType.NULL);
        });

        assertThat(ItemAttributeValue.fromString("foo")).satisfies(v -> {
            assertThat(v.isString()).isTrue();
            assertThat(v.asString()).isEqualTo("foo");
            assertThat(v.type()).isEqualTo(ItemAttributeValueType.STRING);
        });

        assertThat(ItemAttributeValue.fromNumber("1")).satisfies(v -> {
            assertThat(v.isNumber()).isTrue();
            assertThat(v.asNumber()).isEqualTo("1");
            assertThat(v.type()).isEqualTo(ItemAttributeValueType.NUMBER);
        });

        assertThat(ItemAttributeValue.fromBoolean(true)).satisfies(v -> {
            assertThat(v.isBoolean()).isTrue();
            assertThat(v.asBoolean()).isEqualTo(true);
            assertThat(v.type()).isEqualTo(ItemAttributeValueType.BOOLEAN);
        });

        assertThat(ItemAttributeValue.fromBytes(SdkBytes.fromUtf8String("foo"))).satisfies(v -> {
            assertThat(v.isBytes()).isTrue();
            assertThat(v.asBytes().asUtf8String()).isEqualTo("foo");
            assertThat(v.type()).isEqualTo(ItemAttributeValueType.BYTES);
        });

        assertThat(ItemAttributeValue.fromSetOfStrings(Arrays.asList("a", "b"))).satisfies(v -> {
            assertThat(v.isSetOfStrings()).isTrue();
            assertThat(v.asSetOfStrings()).containsExactly("a", "b");
            assertThat(v.type()).isEqualTo(ItemAttributeValueType.SET_OF_STRINGS);
        });

        assertThat(ItemAttributeValue.fromSetOfNumbers(Arrays.asList("1", "2"))).satisfies(v -> {
            assertThat(v.isSetOfNumbers()).isTrue();
            assertThat(v.asSetOfNumbers()).containsExactly("1", "2");
            assertThat(v.type()).isEqualTo(ItemAttributeValueType.SET_OF_NUMBERS);
        });

        assertThat(ItemAttributeValue.fromSetOfBytes(Arrays.asList(SdkBytes.fromUtf8String("foo"),
                                                                   SdkBytes.fromUtf8String("foo2"))))
                .satisfies(v -> {
            assertThat(v.isSetOfBytes()).isTrue();
            assertThat(v.asSetOfBytes().get(0).asUtf8String()).isEqualTo("foo");
            assertThat(v.asSetOfBytes().get(1).asUtf8String()).isEqualTo("foo2");
            assertThat(v.type()).isEqualTo(ItemAttributeValueType.SET_OF_BYTES);
        });
    }
}