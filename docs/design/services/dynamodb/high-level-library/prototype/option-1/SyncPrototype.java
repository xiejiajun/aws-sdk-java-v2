public class SyncPrototype {
    public static void main(String[] args) {
        DynamoDbClient lowLevelClient = DynamoDb.clientBuilder()
                                                .region(Region.US_WEST_2)
                                                .build();

        DynamoDbDocumentClient client = DynamoDb.documentClientBuilder()
                                                .dynamoDbClient(lowLevelClient)
                                                .build();

        DynamoDbTable<User> userTable = client.table(User.class);
        userTable.ensureTableExists();

        simpleCrud(userTable);
        conditionalWrite(userTable);
    }

    private static void simpleCrud(DynamoDbTable<User> userTable) {
        String joeId = UUID.randomUuid().toString();

        // Create
        User user = new User();
        user.setUserId(joeId);
        user.setUserName("joe");

        userTable.put(user);

        // Read
        User joe = userTable.get(joeId);

        // Update
        joe.setUserName("new-joe");
        userTable.update(user);

        // Delete
        userTable.delete(joeId);
    }

    private static void conditionalCreate(DynamoDbTable<User> userTable) {
        User user = new User();
        user.setUserId(UUID.randomUuid().toString());
        user.setUserName("joe");

        userTable.save(user, SaveRequest.builder()
                                        .condition("attribute_not_exists(userName) OR userName = :user_name")
                                        .addConditionValue(":user_name", user.getUserName())
                                        .build());
    }

    private static void getWithRangeKey(DynamoDbTable<User> userTable) {
        UserAction userAction = new UserAction();
        userAction.setUserId("aaaa-aaaaaa-aaaa");
        userAction.setActionTime(exactTime);

        User user = userTable.get(userAction);
    }

    private static void queryWithRangeKey(DynamoDbTable<User> userTable) {
        UserAction userAction = new UserAction();
        userAction.setUserId("aaaa-aaaaaa-aaaa");
        userAction.setActionTime(exactTime);

        User user = userTable.get(userAction);
    }

    private static void getWithMetadata(DynamoDbTable<User> userTable) {
        User user = new User();
        user.setUserId(UUID.randomUuid().toString());
        user.setUserName("joe");

        DynamoDbItem<User> user = userTable.get(user, GetRequest.builder()
                                                                .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
                                                                .build());

        ConsumedCapacity consumedCapacity = user.serviceResponse().consumedCapacity();
        System.out.println(user.item().getUserName());
    }

    private static void conditionalUpdate(DynamoDbTable<User> userTable) {
        User user = new User();
        user.setUserId(UUID.randomUuid().toString());
        user.setUserName("joe");

        userTable.save(user, SaveRequest.builder()
                                        .condition("attribute_not_exists(userName) OR userName = :user_name")
                                        .addConditionValue(":user_name", user.getUserName())
                                        .build());
    }

    @DynamoDbTable.Name("users")
    private static class User {
        @DynamoDbField.HashKey
        private String userId;

        private String userName;

        private String getUserId() {
            return userId;
        }

        private void setUserId(String userId) {
            this.userId = userId;
        }

        private String getUserName() {
            return userName;
        }

        private void setUserName(String userName) {
            this.userName = userName;
        }
    }

    @DynamoDbTable.Name("user-actions")
    private static class UserAction {
        @DynamoDbField.HashKey
        private String userId;

        @DynamoDbField.RangeKey
        private Instant actionTime;

        private ActionType actionType;

        private enum ActionType {
            LOGIN,
            LOGOUT
        }

        private String getUserId() {
            return userId;
        }

        private void setUserId(String userId) {
            this.userId = userId;
        }

        private Instant getActionTime() {
            return actionTime;
        }

        private void setActionTime(Instant actionTime) {
            this.actionTime = actionTime;
        }

        private ActionType getActionType() {
            return actionType;
        }

        private void setActionType(ActionType actionType) {
            this.actionType = actionType;
        }
    }
}