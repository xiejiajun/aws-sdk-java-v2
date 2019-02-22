/**
 * The entry-point for all Dynamo DB client creation. All Java Dynamo features will be accessible through this single class. This
 * enables easy access to the different abstractions that the AWS SDK for Java provides (in exchange for a bigger JAR size).
 *
 * <p>
 *     <b>Maven Module Location</b>
 *     This would be in a separate maven module (software.amazon.awssdk:dynamodb-all) that depends on all other Dynamo DB modules
 *     (software.amazon.awssdk:dynamodb, software.amazon.awssdk:dynamodb-document). Customers that only want one specific client
 *     could instead depend directly on the module that contains it.
 * </p>
 */
public interface DynamoDb {
    /**
     * Create a low-level Dynamo DB client with default configuration. Equivalent to DynamoDbClient.create().
     * Already GA in module software.amazon.awssdk:dynamodb.
     */
    public DynamoDbClient client();

    /**
     * Create a low-level Dynamo DB client builder. Equivalent to DynamoDbClient.builder().
     * Already GA in module software.amazon.awssdk:dynamodb.
     */
    public DynamoDbClientBuilder clientBuilder();

    /**
     * Create a high-level "document" Dynamo DB client with default configuration. See below for API.
     *
     * Usage Example:
     * <code>
     *     TODO
     * </code>
     */
    public DynamoDbDocumentClient documentClient();

    /**
     * Create a high-level "document" Dynamo DB client builder. See below for API.
     *
     * Usage Example:
     * <code>
     *     TODO
     * </code>
     */
    public DynamoDbDocumentClient.Builder documentClientBuilder();
}

public interface DynamoDbDocumentClient extends SdkAutoCloseable {
    /**
     * Create a Dynamo DB document client with default configuration.
     *
     * Equivalent statements:
     * <ol>
     *     <li>DynamoDb.documentClient()</li>
     *     <li>DynamoDb.documentClientBuilder.build()</li>
     *     <li>DynamoDbDocumentClient.builder().build()</li>
     * </ol>
     */
    public static DynamoDbDocumentClient create();

    /**
     * Create a {@link DynamoDbDocumentClient.Builder}.
     */
    public static DynamoDbDocumentClient.Builder builder();

    /**
     * Get a specific Dynamo DB table, based on its table name.
     */
    public static DynamoDbTable createTable(CreateTableRequest createTableRequest);

    /**
     * Get a specific Dynamo DB table, based on its table name.
     */
    public static DynamoDbTable getTable(String tableName);

    /**
     * Get a lazily-populated iterable over all Dynamo DB tables on the current account and region.
     */
    public static ListTablesResponse listTables();

    /**
     * The builder for the high-level Dynamo DB client. This is used by customers to configure the high-level client with default
     * values to be applied across all client operations.
     *
     * This can be created via {@link DynamoDb#documentClientBuilder()} or {@link DynamoDbDocumentClient#builder()}.
     */
    public interface Builder {
        /**
         * Configure the Dynamo DB document client with a low-level Dynamo Db client. This is the only required configuration.
         */
        public DynamoDbDocumentClient.Builder dynamoDbClient(DynamoDbClient client);

        /**
         * Configure the Dynamo DB document client with a specific set of configuration values that override the defaults.
         */
        public DynamoDbDocumentClient.Builder documentOverrideConfiguration(DocumentClientConfiguration configuration);
        public DynamoDbDocumentClient.Builder documentOverrideConfiguration(Consumer<DocumentClientConfiguration.Builder> configuration);

        /**
         * Create a Dynamo DB document client with all of the configured values.
         */
        public DynamoDbDocumentClient build();
    }
}

public interface DocumentClientConfiguration {
    static DocumentClientConfiguration.Builder builder();

    interface Builder {
        /**
         * How long table metadata should be cached in memory before a call to {@link DynamoDbTable#metadata()} should refresh.
         */
        DocumentClientConfiguration.Builder tableMetadataCacheTimeToLive(Duration tableMetadataCacheTimeToLive);

        DocumentClientConfiguration.Builder itemAttributeValueConverters(List<ItemAttributeValueConverter<?>> converters);
        DocumentClientConfiguration.Builder addItemAttributeValueConverter(ItemAttributeValueConverter<?> converter);
        DocumentClientConfiguration.Builder clearItemAttributeValueConverters();

        DocumentClientConfiguration build();
    }
}

public interface ItemAttributeValueConverter<T> {
    public Class<T> convertedClass();
    public ItemAttributeValue toAttributeValue(T input);
    public T fromAttributeValue(ItemAttributeValue input);
}

public interface ListTablesResponse {
    SdkIterable<DynamoDbTable> tables();
}

public interface DynamoDbTable {
    DynamoDbTableMetadata metadata();

    void putItem(Item item);
    void putItem(Consumer<Item.Builder> item);
    void putItem(Object object);
    PutResponse put(PutRequest putRequest);
    PutResponse put(Consumer<PutRequest.Builder> putRequest);

//    Item getItem(ItemKey key);
//    Item getItem(Consumer<ItemKey.Builder> key);
//    Item getItem(Object item);
//
//    GetResponse get(GetRequest getRequest);
//    GetResponse get(Consumer<GetRequest.Builder> getRequest);
}

public interface DynamoDbTableMetadata {
    IndexMetadata indexMetadata();
    List<GlobalSecondaryIndexMetadata> globalSecondaryIndexMetadata();
    List<LocalSecondaryIndexMetadata> localSecondaryIndexMetadata();
}

public interface PutRequest {
    static PutRequest.Builder builder();

    interface Builder {
        PutRequest.Builder item(Item item);
        PutRequest.Builder item(Object item);

        PutRequest.Builder condition(String condition);

        PutRequest.Builder putConditionAttribute(String attributeKey, ItemAttributeValue attributeValue);
        PutRequest.Builder putConditionAttribute(String attributeKey, Object unconvertedAttributeValue);
        PutRequest.Builder removeConditionAttribute(String attributeKey);
        PutRequest.Builder clearConditionAttributes();

        PutRequest.Builder itemAttributeValueConverters(List<ItemAttributeValueConverter<?>> converters);
        PutRequest.Builder addItemAttributeValueConverter(ItemAttributeValueConverter<?> converters);
        PutRequest.Builder clearItemAttributeValueConverters();

        PutRequest build();
    }
}

public interface PutResponse {
    PutItemResponse rawResponse();
}

public interface Item {
    static Item.Builder builder();

    Map<String, ItemAttributeValue> attributes();
    ItemAttributeValue attribute(String attributeKey);

    interface Builder {
        Item.Builder putAttribute(String attributeKey, ItemAttributeValue attributeValue);
        Item.Builder putAttribute(String attributeKey, Object unconvertedAttributeValue);
        Item.Builder removeAttribute(String attributeKey);
        Item.Builder clearAttributes();

        Item build();
    }
}

public interface ItemKey {
    static ItemKey.Builder builder();

    interface Builder {
        ItemKey.Builder partitionKey(String attributeKey, ItemAttributeValue attributeValue);
        ItemKey.Builder partitionKey(String attributeKey, Object unconvertedAttributeValue);

        ItemKey.Builder sortKey(String attributeKey, ItemAttributeValue attributeValue);
        ItemKey.Builder sortKey(String attributeKey, Object unconvertedAttributeValue);

        ItemKey build();
    }
}

public interface ItemAttributeValue {
    static ItemAttributeValue from(Object object);
    static ItemAttributeValue from(T object, ItemAttributeValueConverter<T> converter);
    static ItemAttributeValue fromItem(Item item);
    static ItemAttributeValue fromString(CharSequence string);
    static ItemAttributeValue fromNumber(Number number);
    static ItemAttributeValue fromBytes(SdkBytes bytes);
    static ItemAttributeValue fromBoolean(Boolean bool);
    static ItemAttributeValue fromListOfStrings(List<? extends CharSequence> strings);
    static ItemAttributeValue fromListOfNumbers(List<? extends Number> numbers);
    static ItemAttributeValue fromListOfBytes(List<? extends SdkBytes> bytes);
    static ItemAttributeValue nullValue();

    boolean isItem();
    boolean isString();
    boolean isNumber();
    boolean isBytes();
    boolean isBoolean();
    boolean isListOfStrings();
    boolean isListOfNumbers();
    boolean isListOfBytes();

    <T> T as(Class<T> type);
    <T> T asConvertedBy(ItemAttributeValueConverter<T> converter);
    Item asItem();
    String asString();
    BigDecimal asNumber();
    SdkBytes asBytes();
    Boolean asBoolean();
    List<String> asListOfStrings();
    List<BigDecimal> asListOfNumbers();
    List<SdkBytes> asListOfBytes();
}