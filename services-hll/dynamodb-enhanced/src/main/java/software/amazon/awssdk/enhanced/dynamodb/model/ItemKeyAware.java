package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;

@SdkPublicApi
@ThreadSafe
public interface ItemKeyAware<AttributeT> {
    Map<String, AttributeT> keyAttributes();
    AttributeT keyAttribute(String attributeKey);

    interface Builder<AttributeT> {
        Builder putKeyAttributes(Map<String, AttributeT> attributeValues);
        Builder putKeyAttribute(String attributeKey, AttributeT attributeValue);
        Builder removeKeyAttribute(String attributeKey);
        Builder clearKeyAttributes();
    }
}
