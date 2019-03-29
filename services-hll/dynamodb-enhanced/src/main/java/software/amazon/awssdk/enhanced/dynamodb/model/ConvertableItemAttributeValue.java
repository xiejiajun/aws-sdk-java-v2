package software.amazon.awssdk.enhanced.dynamodb.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;

@SdkPublicApi
@ThreadSafe
public interface ConvertableItemAttributeValue {
    <T> T as(Class<T> type);
    <T> T as(TypeToken<T> type);

    default String asString() {
        return as(String.class);
    }

    default Integer asInteger() {
        return as(Integer.class);
    }

    default Instant asInstant() {
        return as(Instant.class);
    }

    default <T> List<T> asList(Class<T> listParameter) {
        return as(TypeToken.listOf(listParameter));
    }

    default <K, V> Map<K, V> asMap(Class<K> keyType, Class<V> valueType) {
        return as(TypeToken.mapOf(keyType, valueType));
    }

    ItemAttributeValue attributeValue();
}
