package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Optional;
import java.util.function.Consumer;
import software.amazon.awssdk.enhanced.dynamodb.exception.NoSuchItemException;
import software.amazon.awssdk.services.dynamodb.model.TableAlreadyExistsException;

public interface Table {
    String name();
    EnhancedCreateTableResponse create(EnhancedCreateTableRequest request) throws TableAlreadyExistsException;
    Optional<EnhancedCreateTableResponse> createIfMissing(EnhancedCreateTableRequest request);

    void putItem(Item item);
    void putItem(Consumer<Item.Builder> item);
    EnhancedPutItemResponse putItemAdvanced(EnhancedPutItemRequest request);
    EnhancedPutItemResponse putItemAdvanced(Consumer<EnhancedPutItemRequest.Builder> request);

    Item getItem(Item item) throws NoSuchItemException;
    Item getItem(Consumer<Item.Builder> item) throws NoSuchItemException;
    EnhancedGetItemResponse getItemAdvanced(EnhancedGetItemRequest request) throws NoSuchItemException;
    EnhancedGetItemResponse getItemAdvanced(Consumer<EnhancedGetItemRequest.Builder> request) throws NoSuchItemException;
}
