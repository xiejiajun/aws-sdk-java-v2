package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Map;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;

/**
 * An interface shared by all types that have attributes.
 *
 * This allows sharing of attribute retrieval code and documentation between types with attributes.
 */
@SdkPublicApi
@ThreadSafe
@Immutable
public interface AttributeAware<AttributeT> {
    /**
     * Retrieve an unmodifiable view of all attributes, indexed by the attribute key.
     */
    Map<String, AttributeT> attributes();

    /**
     * Retrieve the attribute matching the provided attribute key, or null if no such attribute exists.
     */
    AttributeT attribute(String attributeKey);

    /**
     * An interface shared by all builders that have attributes.
     *
     * This allows sharing of attribute population code and documentation between types with attributes.
     */
    @NotThreadSafe
    interface Builder<AttributeT> {
        /**
         * Add all of the provided attributes, overriding any existing attributes that share the same keys.
         */
        Builder putAttributes(Map<String, AttributeT> attributeValues);

        /**
         * Add the requested attribute, overriding any existing attribute that shares the same key.
         */
        Builder putAttribute(String attributeKey, AttributeT attributeValue);

        /**
         * Remove the attribute that matches the provided key. If no such attribute exists, this does nothing.
         */
        Builder removeAttribute(String attributeKey);

        /**
         * Remove all attributes in this object, leaving the attributes empty.
         */
        Builder clearAttributes();
    }
}
