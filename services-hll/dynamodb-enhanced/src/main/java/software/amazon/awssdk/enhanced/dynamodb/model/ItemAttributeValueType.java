package software.amazon.awssdk.enhanced.dynamodb.model;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * An enum of all types that are supported by DynamoDB's {@link AttributeValue} and the enhanced client's
 * {@link ItemAttributeValue}.
 */
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
