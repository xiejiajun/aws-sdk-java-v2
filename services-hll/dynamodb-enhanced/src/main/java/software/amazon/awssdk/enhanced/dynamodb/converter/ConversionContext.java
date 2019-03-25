package software.amazon.awssdk.enhanced.dynamodb.converter;

import java.util.Optional;
import software.amazon.awssdk.enhanced.dynamodb.model.Item;

public final class ConversionContext {
    /**
     * The name of the attribute being converted.
     */
    String attributeName();

    /**
     * The item that contains the attribute being converted, or Optional.empty() if there is no parent (eg. for root items).
     */
    Optional<Item> parent();
}
