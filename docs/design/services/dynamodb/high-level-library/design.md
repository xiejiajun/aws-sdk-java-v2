# Nouns

The following nouns are introduced by the enhanced DynamoDB client:
 
**Item** - An abstraction of the generated `Map<String,
AttributeValue>`. It represents a map of data that can be stored or
retrieved from DynamoDB.

Example Usage: 
```Java
client.putItem(Item.builder()
                   .putAttribute("userId", UUID.randomUUID())
                   .build());
```

**ItemAttributeValue** - An abstraction of the generated
`AttributeValue`. It represents a single piece of data that can be
stored or retrieved from DynamoDB. An `Item` contains multiple
`ItemAttributeValue`s and their keys.

Example Usage: 
```Java
Item item = client.getItem(r -> r.putAttribute("userId", UUID.randomUUID()));
ItemAttributeValue userIdAttribute = item.attribute("userId");
```

**ItemAttributeValueType** - The type of an `ItemAttributeValue`. This
type is either a persistable type supported by DynamoDB (eg. String,
Number, Item) or a "Java" type. A "Java" type cannot be stored in
DynamoDB without first being converted to a persistable type.

```Java
Item item = client.getItem(r -> r.putAttribute("userId", UUID.randomUUID()));
ItemAttributeValue userIdAttribute = item.attribute("userId");
ItemAttributeValueType userIdAttributeType = userIdAttribute.type();
assert userIdAttributeType == ItemAttributeValueType.STRING;
```

**ItemAttributeValueConverter** - A converter between
`ItemAttributeValue`s and a Java-specific type. These are used to
convert between "Java" type `ItemAttributeValue`s and ones are actually
persisted in DynamoDB.

# Put Request Example

The customer writes:

```Java
EnhancedDynamoDbClient client;
client.putItem(Item.builder()
                   .putAttribute("userId", UUID.randomUUID())
                   .build());
```

The following steps are performed by the SDK:

**Step 1**: Convert the `UUID` into an `ItemAttributeValue` of type
"Java". 

**Step 2**: Use the "conversion chain" to convert the Java-type
`ItemAttributeValue` into a type can can be persisted in DynamoDB. For
UUID, this would be an `ItemAttributeValue` of type String.

**Step 3**: Convert the `Item` into a `Map<String, AttributeValue>`
using a direct mapping to the generated client.
 
**Step 4**: Persist the `Map<String, AttributeValue>` in DynamoDB using
the generated `putItem` method.

# Get Request Example

The customer writes:

```Java
EnhancedDynamoDbClient client;
Item item = client.getItem(Item.builder()
                               .putAttribute("userId", "3521192b-d439-4906-95d7-b7bf838c28d0")
                               .build());
UUID userId = item.attribute("userId").as(UUID.class);
```

The following steps are performed by the SDK:

**Step 1**: Convert the "3521192b-d439-4906-95d7-b7bf838c28d0" into an
`ItemAttributeValue` of type "Java".

**Step 2**: Use the "conversion chain" to convert the Java-type
`ItemAttributeValue` into a type that can be queried against DynamoDB.
For String, this would be an `ItemAttributeValue` of type String.

**Step 3**: Convert the `Item` into a `Map<String, AttributeValue>`
using a direct mapping to the generated client.
 
**Step 4**: Retrieve the requested `Map<String, AttributeValue>` in
DynamoDB using the input `Map<String, AttributeValue>` and the generated
`getItem` method.

**Step 5**: Convert the resulting `Map<String, AttributeValue>` into an
`Item`, where the the `ItemAttributeValue` of "userId" is of type
String.

**Step 6**: Return the `Item` as a result of the enhanced `getItem`
method.

**Step 7**: When the customer invokes `as(UUID.class)`, use the
"conversion chain" to convert the Java-type `ItemAttributeValue` into a
`UUID`.

**Step 8**: Return the `UUID` as a result of the `as` method.

# Type Conversion

Type conversion is performed by a "conversion chain", which is simply a
prioritized list of `ItemAttributeValueConverter`s.

Each `ItemAttributeValueConverter` has:

1. A `defaultConversionCondition`. This defines when the converter
   should be invoked as part of the "conversion chain".
2. A `ItemAttributeValue toAttributeValue(T)` method. This is invoked to
   convert a Java type T into an `ItemAttributeValue` of a persistable
   type.
3. A `T fromAttributeValue(ItemAttributeValue)` method. This is invoked
   to convert an `ItemAttributeValue` of a persistable type into a Java
   type T.

Each `ItemAttributeValueConverter` has a `Conver