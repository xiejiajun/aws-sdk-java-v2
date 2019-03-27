package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.function.Consumer;

public interface Table {
    default String name() {
        throw new UnsupportedOperationException();
    }

    default ResponseItem getItem(RequestItem key) {
        throw new UnsupportedOperationException();
    }

    default ResponseItem getItem(Consumer<RequestItem.Builder> item) {
        RequestItem.Builder itemBuilder = RequestItem.builder();
        item.accept(itemBuilder);
        return getItem(itemBuilder.build());
    }

    default void putItem(RequestItem item) {
        throw new UnsupportedOperationException();
    }

    default void putItem(Consumer<RequestItem.Builder> item) {
        RequestItem.Builder itemBuilder = RequestItem.builder();
        item.accept(itemBuilder);
        putItem(itemBuilder.build());
    }
}
