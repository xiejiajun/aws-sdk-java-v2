package software.amazon.awssdk.enhanced.dynamodb.converter;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.DefaultConversionContext;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@ThreadSafe
public interface ConversionContext extends ToCopyableBuilder<ConversionContext.Builder, ConversionContext> {
    static Builder builder() {
        return DefaultConversionContext.builder();
    }

    Optional<String> attributeName();
    ItemAttributeValueConverter converter();

    interface Builder extends CopyableBuilder<ConversionContext.Builder, ConversionContext> {
        Builder attributeName(String attributeName);
        Builder converter(ItemAttributeValueConverter converter);
    }
}
