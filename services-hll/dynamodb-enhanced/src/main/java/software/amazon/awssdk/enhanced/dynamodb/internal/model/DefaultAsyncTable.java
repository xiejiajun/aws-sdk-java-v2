package software.amazon.awssdk.enhanced.dynamodb.internal.model;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.AsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ItemAttributeValueConverterChain;
import software.amazon.awssdk.enhanced.dynamodb.model.ConverterAware;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedRequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedResponseItem;
import software.amazon.awssdk.enhanced.dynamodb.model.RequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ResponseItem;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.utils.builder.Buildable;

/**
 * The default implementation of {@link AsyncTable}.
 */
@SdkInternalApi
@ThreadSafe
public class DefaultAsyncTable implements AsyncTable {
    private final DynamoDbAsyncClient client;
    private final String tableName;
    private final ItemAttributeValueConverter converter;

    private DefaultAsyncTable(Builder builder) {
        this.client = builder.client;
        this.tableName = builder.tableName;
        this.converter = builder.converter;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String name() {
        return tableName;
    }

    @Override
    public CompletableFuture<ResponseItem> getItem(RequestItem key) {
        ItemAttributeValueConverter itemConverterChain = getConverter(key);
        key = key.toBuilder()
                 .clearConverters()
                 .addConverter(itemConverterChain)
                 .build();

        GeneratedRequestItem generatedKey = key.toGeneratedRequestItem();

        return client.getItem(r -> r.tableName(tableName)
                                    .key(generatedKey.attributes()))
                     .thenApply(response -> {
                         GeneratedResponseItem generatedResponse = GeneratedResponseItem.builder()
                                                                                        .putAttributes(response.item())
                                                                                        .addConverter(itemConverterChain)
                                                                                        .build();
                         return generatedResponse.toResponseItem();
                     });

    }

    @Override
    public CompletableFuture<Void> putItem(RequestItem item) {
        item = item.toBuilder()
                   .clearConverters()
                   .addConverter(getConverter(item))
                   .build();

        GeneratedRequestItem generatedRequest = item.toGeneratedRequestItem();

        return client.putItem(r -> r.tableName(tableName)
                                    .item(generatedRequest.attributes()))
                     .thenApply(r -> null);
    }

    private ItemAttributeValueConverter getConverter(ConverterAware item) {
        return ItemAttributeValueConverterChain.builder()
                                               .parent(converter)
                                               .addConverters(item.converters())
                                               .build();
    }

    public static class Builder implements Buildable {
        private String tableName;
        private DynamoDbAsyncClient client;
        private ItemAttributeValueConverter converter;

        public Builder name(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder dynamoDbAsyncClient(DynamoDbAsyncClient client) {
            this.client = client;
            return this;
        }

        public Builder converter(ItemAttributeValueConverter converter) {
            this.converter = converter;
            return this;
        }

        @Override
        public DefaultAsyncTable build() {
            return new DefaultAsyncTable(this);
        }
    }
}
