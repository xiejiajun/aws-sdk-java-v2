package software.amazon.awssdk.enhanced.dynamodb.model;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;

@SdkPublicApi
@ThreadSafe
public interface ConvertableItemAttributeValue {
    <T> T as(Class<T> type);
    <T> T as(TypeToken<T> type);
    ItemAttributeValue attributeValue();
}
