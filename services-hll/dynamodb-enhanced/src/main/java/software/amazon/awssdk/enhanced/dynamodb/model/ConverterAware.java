package software.amazon.awssdk.enhanced.dynamodb.model;

import java.util.Collection;
import java.util.List;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.ItemAttributeValueConverter;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
@ThreadSafe
public interface ConverterAware {
    List<ItemAttributeValueConverter> converters();

    interface Builder {
        Builder addConverters(Collection<? extends ItemAttributeValueConverter> converters);
        Builder addConverter(ItemAttributeValueConverter converter);
        Builder clearConverters();
    }
}
