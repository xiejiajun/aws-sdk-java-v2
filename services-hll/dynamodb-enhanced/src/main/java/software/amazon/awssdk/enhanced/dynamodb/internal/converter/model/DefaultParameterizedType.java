package software.amazon.awssdk.enhanced.dynamodb.internal.converter.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class DefaultParameterizedType implements ParameterizedType {
    private final Class<?> rawType;
    private final Type[] arguments;

    private DefaultParameterizedType(Class<?> rawType, Type... arguments) {
        this.rawType = rawType;
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
