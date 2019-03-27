package software.amazon.awssdk.enhanced.dyamodb;

import org.junit.BeforeClass;
import org.junit.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

public class DynamoDbEnhancedClientIntegrationTest {
    private static final String TEST_TABLE = "enhanced-client-test-" + System.currentTimeMillis();

    @BeforeClass
    public static void setup() {

    }

    @AfterCl

    @Test
    public void getCanReadTheResultOfPut() {
        try (DynamoDbEnhancedClient client = DynamoDbEnhancedClient.builder()) {

        }
    }
}
