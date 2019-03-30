package software.amazon.awssdk.enhanced.dyamodb;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.junit.BeforeClass;
import org.junit.Test;
import software.amazon.awssdk.enhanced.dynamodb.AsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Table;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionCondition;
import software.amazon.awssdk.enhanced.dynamodb.converter.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.enhanced.dynamodb.model.ConvertableItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.model.RequestItem;
import software.amazon.awssdk.enhanced.dynamodb.model.ResponseItem;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeConvertingVisitor;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;
import software.amazon.awssdk.testutils.Waiter;
import software.amazon.awssdk.utils.Validate;

public class ClientSurfaceAreaIntegrationTest {
    private static final String TABLE = "books";
    private static final DynamoDbClient dynamo = DynamoDbClient.create();

    @BeforeClass
    public static void setup() {
        try {
            dynamo.createTable(r -> r.tableName(TABLE)
                                     .keySchema(k -> k.attributeName("isbn").keyType(KeyType.HASH))
                                     .attributeDefinitions(a -> a.attributeName("isbn").attributeType(ScalarAttributeType.S))
                                     .provisionedThroughput(t -> t.readCapacityUnits(5L)
                                                                  .writeCapacityUnits(5L)));
        } catch (ResourceInUseException e) {
            // Table already exists. Awesome.
        }

        System.out.println("Waiting for table to be active...");

        Waiter.run(() -> dynamo.describeTable(r -> r.tableName(TABLE)))
              .until(r -> r.table().tableStatus().equals(TableStatus.ACTIVE))
              .orFail();
    }



    @Test
    public void showSyncSurfaceArea() {
        // CLIENT CREATION

        DynamoDbEnhancedClient client = DynamoDbEnhancedClient.create();

        DynamoDbEnhancedClient builtClient = DynamoDbEnhancedClient.builder()
                                                                   .dynamoDbClient(DynamoDbClient.create())
                                                                   .addConverters(Collections.emptyList())
                                                                   .addConverter(new InstantAsStringConverter())
                                                                   .clearConverters()
                                                                   .build();



        // CLIENT USAGE

        Table booksTable = client.table("books");
        client.close();



        // TABLE USAGE

        String tableName = booksTable.name(); // "books"

        // Put Item

        booksTable.putItem(RequestItem.builder()

                                      // Attribute Methods
                                      .putAttributes(Collections.singletonMap("isbn", "0-330-25864-8"))
                                      .putAttribute("isbn", "0-330-25864-8")
                                      .putAttribute("publicationDate", p -> p.putAttribute("UK", "1979-10-12T00:00:00Z")
                                                                             .putAttribute("US", "1980-01-01T00:00:00Z"))
                                      .removeAttribute("isbn")
                                      .clearAttributes()

                                      // Converter Methods
                                      .addConverters(Collections.emptyList())
                                      .addConverter(new InstantAsStringConverter())
                                      .clearConverters()

                                      .build());

        booksTable.putItem(item -> item.putAttribute("isbn", "0-330-25864-8"));

        // Get Item

        booksTable.getItem(RequestItem.builder()

                                  // Same methods as PutItem
                                  .putAttribute("isbn", "0-330-25864-8")
                                  .addConverter(new InstantAsStringConverter())

                                  .build());

        ResponseItem book = booksTable.getItem(key -> key.putAttribute("isbn", "0-330-25864-8"));


        // RESPONSE ITEM USAGE

        Map<String, ConvertableItemAttributeValue> attributes = book.attributes();

        ConvertableItemAttributeValue isbnAttribute = book.attribute("isbn");


        // Types supported by default have asType() methods.

        String isbn = book.attribute("isbn").asString();
        Integer version = book.attribute("version").asInteger();
        Instant lastUpdateDate = book.attribute("lastUpdateDate").asInstant();
        List<String> authors = book.attribute("").asList(String.class);
        Map<String, Instant> publicationDates = book.attribute("publicationDates").asMap(String.class, Instant.class);
            // ... etc for all default-supported types.


        // Custom types have as(Class) and as(TypeToken<T>) methods.

        CustomType customType = book.attribute("customTypeField").as(CustomType.class);

        CustomCollection<String> customCollection = book.attribute("customCollection").as(new TypeToken<CustomCollection<String>>(){});


        // The mostly-raw DynamoDB type is also available

        ItemAttributeValue dynamoDbType = book.attribute("isbn").attributeValue();



        // TYPE TOKEN USAGE

        // Creation

        TypeToken<String> stringTypeToken = TypeToken.from(String.class);

        TypeToken<List<String>> listTypeToken = TypeToken.listOf(String.class);

        TypeToken<Map<String, Instant>> mapTypeToken = TypeToken.mapOf(String.class, Instant.class);

        TypeToken<CustomCollection<String>> customCollectionTypeToken = new TypeToken<CustomCollection<String>>() {};


        // Reflection
        Class<?> customCollectionClass = customCollectionTypeToken.representedClass();
        List<TypeToken<?>> customCollectionTypeParameters = customCollectionTypeToken.representedClassParameters();
    }



    @Test
    public void showAsyncSurfaceArea() {
        // CLIENT CREATION

        DynamoDbEnhancedAsyncClient client = DynamoDbEnhancedAsyncClient.create();

        DynamoDbEnhancedAsyncClient builtClient = DynamoDbEnhancedAsyncClient.builder()
                                                                             .dynamoDbClient(DynamoDbAsyncClient.create())
                                                                             // ...
                                                                             .build();



        // CLIENT USAGE

        AsyncTable booksTable = client.table("books");
        client.close();



        // ASYNC TABLE USAGE

        String tableName = booksTable.name(); // "books"

        // Put Item

        CompletableFuture<Void> putCompleteFuture =
                booksTable.putItem(RequestItem.builder()
                                              // ...
                                              .build());

        // Get Item

        CompletableFuture<ResponseItem> getCompleteFuture =
                booksTable.getItem(RequestItem.builder()
                                          // ...
                                          .build());
    }



    /**
     * A custom type converter that stores Instants as Strings (by default, they're numbers so they can be sorted).
     */
    private static class InstantAsStringConverter implements ItemAttributeValueConverter {
        @Override
        public ConversionCondition defaultConversionCondition() {
            return ConversionCondition.isExactInstanceOf(Instant.class);
        }

        @Override
        public ItemAttributeValue toAttributeValue(Object input, ConversionContext context) {
            Instant instant = Validate.isInstanceOf(Instant.class, input,
                                                    "Cannot convert non-Instant objects. Got %s.", input.getClass());
            return ItemAttributeValue.fromString(instant.toString());
        }

        @Override
        public Object fromAttributeValue(ItemAttributeValue input, TypeToken<?> desiredType, ConversionContext context) {
            return input.convert(new TypeConvertingVisitor<Instant>(Instant.class, InstantAsStringConverter.class) {
                @Override
                public Instant convertString(String value) {
                    return Instant.parse(value);
                }
            });
        }
    }




    private static class ShowConverterSurfaceArea implements ItemAttributeValueConverter {
        @Override
        public ConversionCondition defaultConversionCondition() {

            // This converter can convert between ItemAttributeValue and Instant, plus all of its subtypes.
            ConversionCondition.isInstanceOf(Instant.class);

            // This converter can convert between ItemAttributeValue and Instant (no subtypes are supported).
            ConversionCondition.isExactInstanceOf(Instant.class);

            return null;
        }

        @Override
        public ItemAttributeValue toAttributeValue(Object input, ConversionContext context) {

            // CONVERSION CONTEXT USAGE

            ItemAttributeValueConverter converterChain = context.converter();

            Optional<String> attributeName = context.attributeName();

            ConversionContext modifiedContext = context.toBuilder()
                                                       .converter(converterChain)
                                                       .attributeName("isbn")
                                                       .build();



            // ITEM ATTRIBUTE VALUE CREATION

            ItemAttributeValue.fromString("string");
            ItemAttributeValue.fromNumber("1");
            // ... etc for all DynamoDB types.

            return null;
        }

        @Override
        public Object fromAttributeValue(ItemAttributeValue attributeValue, TypeToken<?> desiredType, ConversionContext context) {

            // ITEM ATTRIBUTE VALUE (DynamoDB types)

            // Check DynamoDB type
            boolean isString = attributeValue.type() == ItemAttributeValueType.STRING;


            // Union-type methods
            String string = attributeValue.isString() ? attributeValue.asString() : null;
            List<String> setOfStrings = attributeValue.isSetOfStrings() ? attributeValue.asSetOfStrings() : null;
                // ... etc for all DynamoDB types.


            // Visitor-type methods (useful for type converters)
            String convertedValue = attributeValue.convert(new TypeConvertingVisitor<String>(String.class) {
                @Override
                public String convertString(String value) {
                    return null;
                }

                @Override
                public String convertSetOfStrings(List<String> value) {
                    return null;
                }

                // ... etc for all DynamoDB types.
            });


            // Conversion to the generated, low-level attribute values

            AttributeValue generatedAttributeValue = attributeValue.toGeneratedAttributeValue();
            Map<String, AttributeValue> generatedItem = attributeValue.isMap() ? attributeValue.toGeneratedItem() : null;

            return null;
        }
    }



    private static class CustomType {}

    private static class CustomCollection<T> {}

}
