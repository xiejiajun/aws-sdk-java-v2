package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.function.Consumer;

public interface Table {
    default String name() {
        throw new UnsupportedOperationException();
    }

    default void putItem(Item item) {
        throw new UnsupportedOperationException();
    }

    default void putItem(Consumer<Item.Builder> item) {
        Item.Builder itemBuilder = Item.builder();
        item.accept(itemBuilder);
        putItem(itemBuilder.build());
    }
}
