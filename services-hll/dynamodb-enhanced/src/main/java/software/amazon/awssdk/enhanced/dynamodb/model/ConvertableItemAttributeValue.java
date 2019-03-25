package software.amazon.awssdk.enhanced.dynamodb.model;

public interface ConvertableItemAttributeValue {
    <T> T as(Class<T> type);
    ItemAttributeValue rawValue();
}
