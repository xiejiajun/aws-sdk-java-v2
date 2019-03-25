package software.amazon.awssdk.enhanced.dynamodb.model;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;

public class EnhancedListTablesResponse {
    SdkIterable<Table> tables();
}
