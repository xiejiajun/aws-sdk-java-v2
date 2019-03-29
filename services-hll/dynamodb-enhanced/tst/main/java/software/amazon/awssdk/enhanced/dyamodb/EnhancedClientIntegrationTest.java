package software.amazon.awssdk.enhanced.dyamodb;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;
import software.amazon.awssdk.enhanced.dynamodb.AsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Table;
import software.amazon.awssdk.enhanced.dynamodb.model.ResponseItem;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;
import software.amazon.awssdk.testutils.Waiter;

public class EnhancedClientIntegrationTest {
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
    public void getCanReadTheResultOfPut() throws InterruptedException {
        try (DynamoDbEnhancedClient client = DynamoDbEnhancedClient.create()) {
            Table books = client.table("books");

            System.out.println("Putting item...");

            books.putItem(i -> i.putAttribute("isbn", "0-330-25864-8")
                                .putAttribute("title", "The Hitchhiker's Guide to the Galaxy")
                                .putAttribute("publicationDate", p -> p.putAttribute("UK", Instant.parse("1979-10-12T00:00:00Z"))
                                                                       .putAttribute("US", Instant.parse("1980-01-01T00:00:00Z")))
                                .putAttribute("authors", Collections.singletonList("Douglas Adams")));

            Thread.sleep(5_000);

            System.out.println("Getting item...");

            ResponseItem book = books.getItem(r -> r.putAttribute("isbn", "0-330-25864-8"));

            System.out.println("ISBN: " + book.attribute("isbn").asString() + "\n" +
                               "Title: " + book.attribute("title").asString() + "\n" +
                               "Publication Dates: " + book.attribute("publicationDate").asMap(String.class, Instant.class) + "\n" +
                               "Authors: " + book.attribute("authors").asList(String.class));
        }
    }

    @Test
    public void getCanReadTheResultOfPutAsync() throws InterruptedException {
        try (DynamoDbEnhancedAsyncClient client = DynamoDbEnhancedAsyncClient.create()) {
            AsyncTable books = client.table("books");

            System.out.println("Putting item...");

            books.putItem(i -> i.putAttribute("isbn", "0-330-25864-8")
                                .putAttribute("title", "The Hitchhiker's Guide to the Galaxy")
                                .putAttribute("publicationDate", p -> p.putAttribute("UK", Instant.parse("1979-10-12T00:00:00Z"))
                                                                       .putAttribute("US", Instant.parse("1980-01-01T00:00:00Z")))
                                .putAttribute("authors", Collections.singletonList("Douglas Adams")))
                 .join();

            Thread.sleep(5_000);

            System.out.println("Getting item...");

            ResponseItem book = books.getItem(r -> r.putAttribute("isbn", "0-330-25864-8"))
                                     .join();

            System.out.println("ISBN: " + book.attribute("isbn").asString() + "\n" +
                               "Title: " + book.attribute("title").asString() + "\n" +
                               "Publication Dates: " + book.attribute("publicationDate").asMap(String.class, Instant.class) + "\n" +
                               "Authors: " + book.attribute("authors").asList(String.class));
        }
    }

    @Test
    public void getCanReadTheResultOfPutLowLevel() throws InterruptedException {
        try (DynamoDbClient client = DynamoDbClient.create()) {
            System.out.println("Putting item...");

            Map<String, AttributeValue> publicationDate = new LinkedHashMap<>();
            publicationDate.put("UK", AttributeValue.builder().n(Long.toString(Instant.parse("1979-10-12T00:00:00Z").toEpochMilli())).build());
            publicationDate.put("US", AttributeValue.builder().n(Long.toString(Instant.parse("1980-01-01T00:00:00Z").toEpochMilli())).build());

            Map<String, AttributeValue> requestItem = new LinkedHashMap<>();
            requestItem.put("isbn", AttributeValue.builder().s("0-330-25864-8").build());
            requestItem.put("title", AttributeValue.builder().s("The Hitchhiker's Guide to the Galaxy").build());
            requestItem.put("publicationDate", AttributeValue.builder().m(publicationDate).build());
            requestItem.put("authors", AttributeValue.builder().ss("Douglas Adams").build());

            client.putItem(r -> r.tableName(TABLE).item(requestItem));

            Thread.sleep(5_000);

            System.out.println("Getting item...");

            Map<String, AttributeValue> key = new HashMap<>();
            key.put("isbn", AttributeValue.builder().s("0-330-25864-8").build());

            Map<String, AttributeValue> book = client.getItem(r -> r.tableName(TABLE).key(key)).item();

            Map<String, Instant> publicationDates = new LinkedHashMap<>();
            book.get("publicationDate").m().forEach((k, v) -> publicationDates.put(k, Instant.ofEpochMilli(Long.parseLong(v.n()))));

            System.out.println("ISBN: " + book.get("isbn").s() + "\n" +
                               "Title: " + book.get("title").s() + "\n" +
                               "Publication Dates: " + publicationDates + "\n" +
                               "Authors: " + book.get("authors").ss());
        }
    }
}
