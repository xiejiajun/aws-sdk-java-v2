package software.amazon.awssdk.enhanced.dynamodb;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemKey;
import software.amazon.awssdk.enhanced.dynamodb.model.RequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ResponseItem;

@SdkPublicApi
@ThreadSafe
public interface AsyncTable {
    default String name() {
        throw new UnsupportedOperationException();
    }

    default CompletableFuture<ResponseItem> getItem(ItemKey key) {
        throw new UnsupportedOperationException();
    }

    default CompletableFuture<ResponseItem> getItem(Consumer<ItemKey.Builder> key) {
        ItemKey.Builder itemBuilder = ItemKey.builder();
        key.accept(itemBuilder);
        return getItem(itemBuilder.build());
    }

    default CompletableFuture<Void> putItem(RequestItem item) {
        throw new UnsupportedOperationException();
    }

    default CompletableFuture<Void> putItem(Consumer<RequestItem.Builder> item) {
        RequestItem.Builder itemBuilder = RequestItem.builder();
        item.accept(itemBuilder);
        return putItem(itemBuilder.build());
    }
}
