/**
 * The entry-point for all DynamoDB client creation. All Java Dynamo features will be accessible through this single class. This
 * enables easy access to the different abstractions that the AWS SDK for Java provides (in exchange for a bigger JAR size).
 *
 * <p>
 *     <b>Maven Module Location</b>
 *     This would be in a separate maven module (software.amazon.awssdk:dynamodb-all) that depends on all other DynamoDB modules
 *     (software.amazon.awssdk:dynamodb, software.amazon.awssdk:dynamodb-document). Customers that only want one specific client
 *     could instead depend directly on the module that contains it.
 * </p>
 */
public interface DynamoDb {
    /**
     * Create a low-level DynamoDB client with default configuration. Equivalent to DynamoDbClient.create().
     * Already GA in module software.amazon.awssdk:dynamodb.
     */
    DynamoDbClient client();

    /**
     * Create a low-level DynamoDB client builder. Equivalent to DynamoDbClient.builder().
     * Already GA in module software.amazon.awssdk:dynamodb.
     */
    DynamoDbClientBuilder clientBuilder();

    /**
     * Create a high-level "document" DynamoDB client with default configuration.
     *
     * Usage Example:
     * <code>
     *     try (DynamoDbDocumentClient client = DynamoDb.documentClient()) {
     *         client.listTables().tables().forEach(System.out::println);
     *     }
     * </code>
     *
     * @see DynamoDbDocumentClient
     */
    DynamoDbDocumentClient documentClient();

    /**
     * Create a high-level "document" DynamoDB client builder that can configure and create high-level "document" DynamoDB
     * clients.
     *
     * Usage Example:
     * <code>
     *     try (DynamoDbClient lowLevelClient = DynamoDb.client();
     *          DynamoDbDocumentClient client = DynamoDb.documentClientBuilder()
     *                                                  .dynamoDbClient(lowLevelClient)
     *                                                  .build()) {
     *         client.listTables().tables().forEach(System.out::println);
     *     }
     * </code>
     *
     * @see DynamoDbDocumentClient.Builder
     */
    DynamoDbDocumentClient.Builder documentClientBuilder();
}

/**
 * A synchronous client for interacting with DynamoDB. While the low-level {@link DynamoDbClient} is generated from a service
 * model, this client is hand-written and provides a richer client experience for DynamoDB.
 *
 * Features:
 * <ol>
 *     <li>Representations of DynamoDB resources, like {@link Table}s and {@link Item}s.</li>
 *     <li>Support for Java-specific types, like {@link Instant} and {@link BigDecimal}.</li>
 *     <li>Support for reading and writing custom objects (eg. Java Beans, POJOs).</li>
 * </ol>
 *
 * All {@link DynamoDbDocumentClient}s should be closed via {@link #close()}.
 */
public interface DynamoDbDocumentClient extends SdkAutoCloseable {
    /**
     * Create a {@link DynamoDbDocumentClient} with default configuration.
     *
     * Equivalent statements:
     * <ol>
     *     <li>{@code DynamoDb.documentClient()}</li>
     *     <li>{@code DynamoDb.documentClientBuilder().build()}</li>
     *     <li>{@code DynamoDbDocumentClient.builder().build()}</li>
     * </ol>
     *
     * Usage Example:
     * <code>
     *     try (DynamoDbDocumentClient client = DynamoDbDocumentClient.create()) {
     *         client.listTables().table().forEach(System.out::println);
     *     }
     * </code>
     */
    static DynamoDbDocumentClient create();

    /**
     * Create a {@link DynamoDbDocumentClient.Builder} that can be used to create a {@link DynamoDbDocumentClient} with custom
     * configuration.
     *
     * Equivalent to {@code DynamoDb.documentClientBuilder()}.
     *
     * Usage Example:
     * <code>
     *     try (DynamoDbClient lowLevelClient = DynamoDbClient.create();
     *          DynamoDbDocumentClient client = DynamoDbDocumentClient.builder()
     *                                                                .dynamoDbClient(lowLevelClient)
     *                                                                .build()) {
     *         client.listTables().tables().forEach(System.out::println);
     *     }
     * </code>
     */
    static DynamoDbDocumentClient.Builder builder();

    /**
     * Create a Dynamo DB table that does not already exist. If the table exists already, use {@link #getTable(String)}.
     *
     * Usage Example:
     * <code>
     *     try (DynamoDbDocumentClient client = DynamoDb.documentClient()) {
     *         ProvisionedCapacity tableCapacity = ProvisionedCapacity.builder()
     *                                                                .readCapacity(5)
     *                                                                .writeCapacity(5)
     *                                                                .build();
     *
     *         KeySchema tableKeys = KeySchema.builder()
     *                                        .putKey("partition-key", AttributeIndexType.PARTITION_KEY)
     *                                        .build();
     *
     *         client.createTable(CreateTableRequest.builder()
     *                                              .tableName("my-table")
     *                                              .provisionedCapacity(tableCapacity)
     *                                              .keySchema(tableKeys)
     *                                              .build());
     *
     *         System.out.println("Table created successfully.");
     *     } catch (TableAlreadyExistsException e) {
     *         System.out.println("Table creation failed.");
     *     }
     * </code>
     */
    CreateTableResponse createTable(CreateTableRequest createTableRequest)
            throws TableAlreadyExistsException;

    /**
     * Get a specific DynamoDB table, based on its table name. If the table does not exist, use {@link #createTable}.
     *
     * Usage Example:
     * <code>
     *     try (DynamoDbDocumentClient client = DynamoDb.documentClient()) {
     *         Table table = client.getTable("my-table");
     *         System.out.println(table);
     *     } catch (NoSuchTableException e) {
     *         System.out.println("Table does not exist.");
     *     }
     * </code>
     */
    Table getTable(String tableName)
            throws NoSuchTableException;

    /**
     * Get a lazily-populated iterable over all DynamoDB tables on the current account and region.
     *
     * Usage Example:
     * <code>
     *     try (DynamoDbDocumentClient client = DynamoDb.documentClient()) {
     *         String tables = client.listTables().tables().stream()
     *                               .map(Table::name)
     *                               .collect(Collectors.joining(","));
     *         System.out.println("Current Tables: " + tables);
     *     } catch (NoSuchTableException e) {
     *         System.out.println("Table does not exist.");
     *     }
     * </code>
     */
    ListTablesResponse listTables();

    /**
     * The builder for the high-level DynamoDB client. This is used by customers to configure the high-level client with default
     * values to be applied across all client operations.
     *
     * This can be created via {@link DynamoDb#documentClientBuilder()} or {@link DynamoDbDocumentClient#builder()}.
     */
    interface Builder {
        /**
         * Configure the DynamoDB document client with a low-level DynamoDB client.
         *
         * Default: {@code DynamoDbClient.create()}
         */
        DynamoDbDocumentClient.Builder dynamoDbClient(DynamoDbClient client);

        /**
         * Configure the DynamoDB document client with a specific set of configuration values that override the defaults.
         *
         * Default: {@code DocumentClientConfiguration.create()}
         */
        DynamoDbDocumentClient.Builder documentClientConfiguration(DocumentClientConfiguration configuration);

        /**
         * Create a DynamoDB document client with all of the configured values.
         */
        DynamoDbDocumentClient build();
    }
}

/**
 * Configuration for a {@link DynamoDbDocumentClient}. This specific configuration is applied globally across all tables created
 * by a client builder.
 *
 * @see DynamoDbDocumentClient.Builder#documentClientConfiguration(DocumentClientConfiguration)
 */
public interface DocumentClientConfiguration {
    /**
     * Create document client configuration with default values.
     */
    static DocumentClientConfiguration create();

    /**
     * Create a builder instance, with an intent to override default values.
     */
    static DocumentClientConfiguration.Builder builder();

    interface Builder {
        /**
         * Configure the type converters that should be applied globally across all {@link Table}s from the client. This can
         * also be overridden at the Item level.
         *
         * The following type conversions are supported by default:
         * <ul>
         *     <li>{@link null} -> {@link ItemAttributeValueType#NULL}</li>
         *     <li>{@link Map} -> {@link ItemAttributeValueType#ITEM}</li>
         *     <li>{@link Collection} -> {@link ItemAttributeValueType#LIST_OF_*}</li>
         *     <li>{@link Stream} -> {@link ItemAttributeValueType#LIST_OF_*}</li>
         *     <li>{@link Iterable} -> {@link ItemAttributeValueType#LIST_OF_*}</li>
         *     <li>{@link Iterator} -> {@link ItemAttributeValueType#LIST_OF_*}</li>
         *     <li>{@link Enumeration} -> {@link ItemAttributeValueType#LIST_OF_*}</li>
         *     <li>{@link Number} -> {@link ItemAttributeValueType#NUMBER}</li>
         *     <li>{@link Temporal} -> {@link ItemAttributeValueType#NUMBER}</li>
         *     <li>{@link CharSequence} -> {@link ItemAttributeValueType#STRING}</li>
         *     <li>{@link byte[]} -> {@link ItemAttributeValueType#BYTES}</li>
         *     <li>{@link ByteBuffer} -> {@link ItemAttributeValueType#BYTES}</li>
         *     <li>{@link BytesWrapper} -> {@link ItemAttributeValueType#BYTES}</li>
         *     <li>{@link InputStream} -> {@link ItemAttributeValueType#BYTES}</li>
         *     <li>{@link File} -> {@link ItemAttributeValueType#BYTES}</li>
         *     <li>{@link Boolean} -> {@link ItemAttributeValueType#BOOLEAN}</li>
         *     <li>{@link Object} -> {@link ItemAttributeValueType#ITEM}</li>
         * </ul>
         *
         * Usage Example:
         * <code>
         *     DocumentClientConfiguration clientConfiguration =
         *             DocumentClientConfiguration.builder()
         *                                        .addConverter(InstantsAsStringsConverter.create())
         *                                        .build();
         *
         *     try (DynamoDbDocumentClient client = DynamoDb.documentClientBuilder()
         *                                                  .documentClientConfiguration(clientConfiguration)
         *                                                  .build()) {
         *
         *         Table table = client.getTable("my-table");
         *         UUID id = UUID.randomUUID();
         *         table.putItem(Item.builder()
         *                           .putAttribute("partition-key", id)
         *                           .putAttribute("creation-time", Instant.now())
         *                           .build());
         *
         *         Item item = table.getItem(Item.builder()
         *                                       .putAttribute("partition-key", id)
         *                                       .build());
         *
         *         // Items are usually stored as a number, but it was stored as an ISO-8601 string now because of the
         *         // InstantsAsStringsConverter.
         *         assert item.attribute("creation-time").isString();
         *         assert item.attribute("creation-time").as(Instant.class).isBetween(Instant.now().minus(1, MINUTE),
         *                                                                            Instant.now());
         *     }
         * </code>
         */
        DocumentClientConfiguration.Builder converters(List<ItemAttributeValueConverter<?>> converters);
        DocumentClientConfiguration.Builder addConverter(ItemAttributeValueConverter<?> converter);
        DocumentClientConfiguration.Builder clearConverters();

        /**
         * Create the configuration object client with all of the configured values.
         */
        DocumentClientConfiguration build();
    }
}

/**
 * A converter between Java types and DynamoDB types. These can be attached to {@link DynamoDbDocumentClient}s and
 * {@link Item}s, so that types are automatically converted when writing to and reading from DynamoDB.
 *
 * @see DocumentClientConfiguration.Builder#converters(List)
 * @see Item.Builder#converter(ItemAttributeValueConverter)
 *
 * @param T The Java type that is generated by this converter.
 */
public interface ItemAttributeValueConverter<T> {
    /**
     * The default condition in which this converter is invoked.
     *
     * Even if this condition is not satisfied, it can still be invoked directly via
     * {@link ItemAttributeValue#convert(ItemAttributeValueConverter)}.
     */
    ConversionCondition defaultConversionCondition();

    /**
     * Convert the provided Java type into an {@link ItemAttributeValue}.
     */
    ItemAttributeValue toAttributeValue(T input, ConversionContext context);

    /**
     * Convert the provided {@link ItemAttributeValue} into a Java type.
     */
    T fromAttributeValue(ItemAttributeValue input, ConversionContext context);
}

/**
 * The condition in which a {@link ItemAttributeValueConverter} will be invoked.
 *
 * @see ItemAttributeValueConverter#defaultConversionCondition().
 */
public interface ConversionCondition {
    /**
     * Create a conversion condition that causes an {@link ItemAttributeValueConverter} to be invoked if an attribute value's
     * {@link ConversionContext} matches a specific condition.
     *
     * This condition has a larger overhead than the {@link #isInstanceOf(Class)} and {@link #never()}, because it must be
     * invoked for every attribute value being converted and its result cannot be cached. For this reason, lower-overhead
     * conditions like {@link #isInstanceOf(Class)} and {@link #never()} should be favored where performance is important.
     */
    static ConversionCondition contextSatisfies(Predicate<ConversionContext> contextPredicate);

    /**
     * Create a conversion condition that causes an {@link ItemAttributeValueConverter} to be invoked if the attribute value's
     * Java type matches the provided class.
     *
     * The result of this condition can be cached, and will likely not be invoked for previously-converted types.
     */
    static ConversionCondition isInstanceOf(Class<?> clazz);

    /**
     * Create a conversion condition that causes an {@link ItemAttributeValueConverter} to never be invoked by default, except
     * when directly invoked via {@link ItemAttributeValue#convert(ItemAttributeValueConverter)}.
     *
     * The result of this condition can be cached, and will likely not be invoked for previously-converted types.
     */
    static ConversionCondition never();
}

/**
 * Additional context that can be used in the context of converting between Java types and {@link ItemAttributeValue}s.
 *
 * @see ItemAttributeValueConverter#toAttributeValue(Object, ConversionContext)
 * @see ItemAttributeValueConverter#fromAttributeValue(ItemAttributeValue, ConversionContext)
 */
public interface ConversionContext {
    /**
     * The name of the attribute being converted.
     */
    String attributeName();

    /**
     * The schema of the attribute being converted.
     */
    ItemAttributeSchema attributeSchema();

    /**
     * The item that contains the attribute being converted.
     */
    Item parent();

    /**
     * The schema of the {@link #parent()}.
     */
    ItemSchema parentSchema();
}

/**
 * The result of invoking {@link DynamoDbDocumentClient#listTables()}.
 */
public interface ListTablesResponse {
    /**
     * A lazily-populated iterator over all tables in the current region. This may make multiple service calls in the
     * background when iterating over the full result set.
     */
    SdkIterable<Table> tables();
}

/**
 * A DynamoDB table, containing a collection of {@link Item}s.
 *
 * Currently supported operations:
 * <ul>
 *     <li>Writing objects with {@link #putItem(Item)} and {@link #putObject(Object)}</li>
 *     <li>Reading objects with {@link #getItem(Item)}} and {@link #getObject(Object)}</li>
 *     <li>Accessing the current table configuration with {@link #metadata()}.</li>
 *     <li>Creating new indexes with {@link #createGlobalSecondaryIndex(CreateGlobalSecondaryIndexRequest)}.</li>
 * </ul>
 *
 * The full version will all table operations, including Query, Delete, Update, Scan, etc.
 */
public interface Table {
    /**
     * Retrieve the name of this table.
     */
    String name();

    /**
     * Invoke DynamoDB to retrieve the metadata for this table.
     */
    TableMetadata metadata();

    /**
     * Invoke DynamoDB to create a new global secondary index for this table.
     *
     * Usage Example:
     * <code>
     *     try (DynamoDbDocumentClient client = DynamoDb.documentClient()) {
     *         ProvisionedCapacity indexCapacity = ProvisionedCapacity.builder()
     *                                                                .readCapacity(5)
     *                                                                .writeCapacity(5)
     *                                                                .build();
     *
     *         KeySchema indexKeys = KeySchema.builder()
     *                                        .putKey("extra-partition-key", AttributeIndexType.PARTITION_KEY)
     *                                        .build();
     *
     *         Table table = client.getTable("my-table");
     *
     *         table.createGlobalSecondaryIndex(CreateGlobalSecondaryIndexRequest.builder()
     *                                                                           .indexName("my-new-index")
     *                                                                           .provisionedCapacity(tableCapacity)
     *                                                                           .keySchema(tableKeys)
     *                                                                           .build());
     *     }
     * </code>
     */
    CreateGlobalSecondaryIndexResponse createGlobalSecondaryIndex(CreateGlobalSecondaryIndexRequest createRequest);

    /**
     * Invoke DynamoDB to create a new {@link Item} in this table.
     *
     * Usage Example:
     * <code>
     *     try (DynamoDbDocumentClient client = DynamoDb.documentClient()) {
     *         Table table = client.getTable("my-table");
     *         UUID id = UUID.randomUUID();
     *         table.putItem(Item.builder()
     *                           .putAttribute("partition-key", id)
     *                           .putAttribute("creation-time", Instant.now())
     *                           .build());
     *     }
     * </code>
     */
    void putItem(Item item);
    PutItemResponse putItem(PutItemRequest putRequest);

    void putObject(Object item);
    <T> PutObjectResponse<T> putObject(T item, PutObjectRequest<T> putRequest);

    Item getItem(Item item);
    GetItemResponse getItem(GetItemRequest getRequest);

    <T> T getObject(T item);
    <T> GetObjectResponse<T> getObject(GetObjectRequest<T> getRequest);
}

public interface TableMetadata {
    List<GlobalSecondaryIndexMetadata> globalSecondaryIndexMetadata();
    List<LocalSecondaryIndexMetadata> localSecondaryIndexMetadata();
}

public interface PutItemRequest extends PutRequest {
    static PutItemRequest.Builder builder();

    interface Builder extends PutRequest.Builder {
        PutItemRequest.Builder item(Item item);
        PutItemRequest build();
    }
}

public interface PutObjectRequest<T> extends PutRequest {
    static PutObjectRequest.Builder builder(T object);
    static PutObjectRequest.Builder builder(Class<T> objectClass);

    interface Builder<T> extends PutRequest.Builder {
        PutItemRequest.Builder object(T item);
        PutObjectRequest build();
    }
}

public interface PutRequest extends DynamoDbRequest {
    interface Builder {
        PutRequest.Builder condition(String condition);

        PutRequest.Builder putConditionAttribute(String attributeKey, ItemAttributeValue attributeValue);
        PutRequest.Builder removeConditionAttribute(String attributeKey);
        PutRequest.Builder clearConditionAttributes();

        PutRequest build();
    }
}

public interface GetItemRequest extends PutRequest {
    static GetItemRequest.Builder builder();

    interface Builder extends PutRequest.Builder {
        GetItemRequest.Builder item(Item item);
        GetItemRequest build();
    }
}

public interface GetObjectRequest<T> extends PutRequest {
    static GetObjectRequest.Builder builder(T object);
    static GetObjectRequest.Builder builder(Class<T> objectClass);

    interface Builder<T> extends PutRequest.Builder {
        GetObjectRequest.Builder object(T item);
        GetObjectRequest build();
    }
}

public interface GetRequest extends DynamoDbRequest {
    interface Builder {
        PutRequest.Builder condition(String condition);

        PutRequest.Builder putConditionAttribute(String attributeKey, ItemAttributeValue attributeValue);
        PutRequest.Builder removeConditionAttribute(String attributeKey);
        PutRequest.Builder clearConditionAttributes();

        PutRequest build();
    }
}

public interface DynamoDbRequest {
    interface Builder {
        PutRequest.Builder converters(List<ItemAttributeValueConverter<?>> converters);
        PutRequest.Builder addConverter(ItemAttributeValueConverter<?> converter);
        PutRequest.Builder clearConverters();
    }
}

public interface PutItemResponse extends PutResponse {
    Item item();
}

public interface PutObjectResponse<T> extends PutResponse {
    T object();
}

public interface PutResponse {
    PutItemResponse rawResponse();
}

public interface ItemSchema {
    static ItemSchema.Builder builder();

    interface Builder {
        ItemSchema.Builder attributeSchemas(Map<String, ItemAttributeSchema> attributeSchemas);
        ItemSchema.Builder putAttributeSchema(String attributeName, ItemAttributeSchema attributeSchema);
        ItemSchema.Builder removeAttributeSchema(String attributeName);
        ItemSchema.Builder clearAttributeSchemas();

        ItemSchema.Builder converter(ItemAttributeValueConverter<?> converter);

        ItemSchema build();
    }
}

public interface ItemAttributeSchema {
    static ItemAttributeSchema.Builder builder();

    interface Builder {
        ItemAttributeSchema.Builder indexType(AttributeIndexType attributeIndexType);
        ItemAttributeSchema.Builder javaType(Class<?> attributeJavaType);
        ItemAttributeSchema.Builder dynamoType(ItemAttributeValueType attributeDynamoType);
        ItemAttributeSchema.Builder converter(ItemAttributeValueConverter<?> converter);

        ItemAttributeSchema build();
    }
}

public enum AttributeIndexType {
    PARTITION_KEY,
    SORT_KEY
}

public interface Item {
    static Item.Builder builder();

    Map<String, ItemAttributeValue> attributes();
    ItemAttributeValue attribute(String attributeKey);

    interface Builder {
        Item.Builder putAttribute(String attributeKey, ItemAttributeValue attributeValue);
        Item.Builder putAttribute(String attributeKey, ItemAttributeValue attributeValue, ItemAttributeSchema attributeSchema);
        Item.Builder putAttribute(String attributeKey, Object attributeValue);
        Item.Builder putAttribute(String attributeKey, Object attributeValue, ItemAttributeSchema attributeSchema);
        Item.Builder removeAttribute(String attributeKey);
        Item.Builder clearAttributes();

        Item.Builder converter(ItemAttributeValueConverter<?> converter);

        Item build();
    }
}

public interface ItemAttributeValue {
    static ItemAttributeValue from(Object object);
    static ItemAttributeValue nullValue();

    boolean isConverted();
    Object unconvertedValue();
    ItemAttributeValue convert(ItemAttributeValueConverter<?> converter);

    ItemAttributeValueType type();
    boolean isItem();
    boolean isString();
    boolean isNumber();
    boolean isBytes();
    boolean isBoolean();
    boolean isListOfStrings();
    boolean isListOfNumbers();
    boolean isListOfBytes();
    boolean isListOfAttributeValues();
    boolean isNull();

    <T> T as(Class<T> type);
    Item asItem();
    String asString();
    BigDecimal asNumber();
    SdkBytes asBytes();
    Boolean asBoolean();

    List<String> asListOfStrings();
    List<BigDecimal> asListOfNumbers();
    List<SdkBytes> asListOfBytes();
    List<ItemAttributeValue> asListOfAttributeValues();
}

public enum ItemAttributeValueType {
    ITEM,
    STRING,
    NUMBER,
    BYTES,
    BOOLEAN,
    LIST_OF_STRINGS,
    LIST_OF_NUMBERS,
    LIST_OF_BYTES,
    LIST_OF_ATTRIBUTE_VALUES,
    NULL
}