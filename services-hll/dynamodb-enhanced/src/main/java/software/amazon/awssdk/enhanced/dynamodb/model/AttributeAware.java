package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;

@SdkPublicApi
@ThreadSafe
public interface AttributeAware<AttributeT> {
    Map<String, AttributeT> attributes();
    AttributeT attribute(String attributeKey);

    interface Builder<AttributeT> {
        Builder putAttributes(Map<String, AttributeT> attributeValues);
        Builder putAttribute(String attributeKey, AttributeT attributeValue);
        Builder removeAttribute(String attributeKey);
        Builder clearAttributes();
    }
}
