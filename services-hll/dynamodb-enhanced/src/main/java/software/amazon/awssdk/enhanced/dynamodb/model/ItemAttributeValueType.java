package software.amazon.awssdk.enhanced.dynamodb.model;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;

@SdkPublicApi
@ThreadSafe
@Immutable
public enum ItemAttributeValueType {
    MAP,
    STRING,
    NUMBER,
    BYTES,
    BOOLEAN,
    NULL,
    SET_OF_STRINGS,
    SET_OF_NUMBERS,
    SET_OF_BYTES,
    LIST_OF_ATTRIBUTE_VALUES
}
