package software.amazon.awssdk.enhanced.dynamodb.internal.converter.model;

import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.model.RequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ResponseItem;
import software.amazon.awssdk.enhanced.dynamodb.model.Table;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.utils.builder.Buildable;

public class DefaultTable implements Table {
    private final DynamoDbClient client;
    private final String tableName;
    private final ItemAttributeValueConverter converter;

    private DefaultTable(Builder builder) {
        this.client = builder.client;
        this.tableName = builder.tableName;
        this.converter = builder.converter;
    }

    @Override
    public String name() {
        return tableName;
    }

    @Override
    public void putItem(RequestItem item) {
        item.
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
            return null;
        }
    }
}
