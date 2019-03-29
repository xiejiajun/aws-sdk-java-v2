package software.amazon.awssdk.enhanced.dynamodb.internal.model;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.ItemAttributeValueConverterChain;
import software.amazon.awssdk.enhanced.dynamodb.model.ConverterAware;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedRequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.GeneratedResponseItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemKey;
import software.amazon.awssdk.enhanced.dynamodb.model.RequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ResponseItem;
import software.amazon.awssdk.enhanced.dynamodb.Table;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.utils.builder.Buildable;

@SdkInternalApi
@ThreadSafe
public class DefaultTable implements Table {
    private final DynamoDbClient client;
    private final String tableName;
    private final ItemAttributeValueConverter converter;

    private DefaultTable(Builder builder) {
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
    public ResponseItem getItem(ItemKey key) {
        key = key.toBuilder()
                 .clearConverters()
                 .addConverter(getConverter(key))
                 .build();

        GeneratedRequestItem generatedKey = key.toGeneratedRequestItem();

        GetItemResponse response = client.getItem(r -> r.tableName(tableName)
                                                        .key(generatedKey.attributes()));

        GeneratedResponseItem generatedResponse = GeneratedResponseItem.builder()
                                                                       .putAttributes(response.item())
                                                                       .addConverter(converter)
                                                                       .build();

        return generatedResponse.toResponseItem();
    }

    @Override
    public void putItem(RequestItem item) {
        item = item.toBuilder()
                   .clearConverters()
                   .addConverter(getConverter(item))
                   .build();

        GeneratedRequestItem generatedRequest = item.toGeneratedRequestItem();

        client.putItem(r -> r.tableName(tableName)
                             .item(generatedRequest.attributes()));
    }

    private ItemAttributeValueConverter getConverter(ConverterAware item) {
        return ItemAttributeValueConverterChain.builder()
                                               .parent(converter)
                                               .addConverters(item.converters())
                                               .build();
    }

    public static class Builder implements Buildable {
        private String tableName;
        private DynamoDbClient client;
        private ItemAttributeValueConverter converter;

        public Builder name(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder dynamoDbClient(DynamoDbClient client) {
            this.client = client;
            return this;
        }

        public Builder converter(ItemAttributeValueConverter converter) {
            this.converter = converter;
            return this;
        }

        @Override
        public DefaultTable build() {
            return new DefaultTable(this);
        }
    }
}
