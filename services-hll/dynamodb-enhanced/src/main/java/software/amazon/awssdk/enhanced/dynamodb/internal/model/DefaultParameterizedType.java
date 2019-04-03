package software.amazon.awssdk.enhanced.dynamodb.internal.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.utils.Validate;

/**
 * An implementation of {@link ParameterizedType} that guarantees its raw type is always a {@link Class}.
 */
@SdkInternalApi
@ThreadSafe
public final class DefaultParameterizedType implements ParameterizedType {
    private final Class<?> rawType;
    private final Type[] arguments;

    private DefaultParameterizedType(Class<?> rawType, Type... arguments) {
        Validate.notEmpty(arguments, "Arguments must not be empty.");
        Validate.noNullElements(arguments, "Arguments cannot contain null values.");
        this.rawType = Validate.paramNotNull(rawType, "rawType");
        this.arguments = arguments;

    }

    public static ParameterizedType parameterizedType(Class<?> rawType, Type... arguments) {
        return new DefaultParameterizedType(rawType, arguments);
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return arguments.clone();
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
