package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.List;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;

public interface ConverterAwareItem {
    List<ItemAttributeValueConverter> converters();

    interface Builder {
        Builder addConverter(ItemAttributeValueConverter converter);

        Builder clearConverters();
    }
}
