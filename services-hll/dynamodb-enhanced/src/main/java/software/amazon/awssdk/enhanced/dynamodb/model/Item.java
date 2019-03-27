package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Map;

public interface Item<AttributeT> {
    Map<String, AttributeT> attributes();
    AttributeT attribute(String attributeKey);

    interface Builder<AttributeT> {
        Builder putAttributes(Map<String, AttributeT> attributeValues);
        Builder putAttribute(String attributeKey, AttributeT attributeValue);
        Builder removeAttribute(String attributeKey);
    }
}
