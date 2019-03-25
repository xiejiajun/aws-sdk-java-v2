package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.function.Consumer;

public interface Table {
    String name();
    void putItem(Item item);
    void putItem(Consumer<Item.Builder> item);
}
