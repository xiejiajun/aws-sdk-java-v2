package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.function.Consumer;

public interface Table {
    default String name() {
        throw new UnsupportedOperationException();
    }

    default void putItem(ResponseItem item) {
        throw new UnsupportedOperationException();
    }

    default void putItem(Consumer<ResponseItem.Builder> item) {
        ResponseItem.Builder itemBuilder = ResponseItem.builder();
        item.accept(itemBuilder);
        putItem(itemBuilder.build());
    }
}
