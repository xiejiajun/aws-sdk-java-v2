package software.amazon.awssdk.enhanced.dynamodb.model;

public interface ConvertableItemAttributeValue {
    <T> T as(Class<T> type);
    <T> T as(TypeToken<T> type);
    ItemAttributeValue attributeValue();
}
