/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * This source was modified heavily from the Guava implementation of the same name:
 * https://github.com/google/guava/blob/master/guava/src/com/google/common/reflect/TypeToken.java
 *
 * Original source is Copyright (C) 2006 The Guava Authors
 * Licensed under the Apache License, Version 2.0 (the "License"):
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package software.amazon.awssdk.enhanced.dynamodb.model;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.internal.model.DefaultParameterizedType;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;

/**
 * A {@link Type} with generics.
 *
 * Original Guava authors: Bob Lee, Sven Mawson, Ben Yu
 */
@SdkPublicApi
@ThreadSafe
public class TypeToken<T> {
    private final Type runtimeType;
    private final Class<T> representedClass;
    private final List<TypeToken<?>> representedClassParameters;

    protected TypeToken() {
        this(null);
    }

    private TypeToken(Type type) {
        if (type == null) {
            type = captureGenericTypeArguments();
        }

        this.runtimeType = validateIsSupportedType(type);
        this.representedClass = loadRepresentedClass();
        this.representedClassParameters = loadRepresentedClassParameters();
    }

    public static TypeToken<?> from(Type type) {
        return new TypeToken<>(Validate.paramNotNull(type, "type"));
    }

    public static <T> TypeToken<T> from(Class<T> type) {
        return new TypeToken<>(Validate.paramNotNull(type, "type"));
    }

    public static <T> TypeToken<List<T>> listOf(Class<T> valueType) {
        return new TypeToken<>(DefaultParameterizedType.parameterizedType(List.class, valueType));
    }

    public static <T, U> TypeToken<Map<T, U>> mapOf(Class<T> keyType, Class<U> valueType) {
        return new TypeToken<>(DefaultParameterizedType.parameterizedType(Map.class, keyType, valueType));
    }

    private static Type validateIsSupportedType(Type type) {
        Validate.validState(!(type instanceof GenericArrayType),
                            "Array type %s is not supported. Use java.util.List instead of arrays.", type);
        Validate.validState(!(type instanceof TypeVariable), "Type variable type %s is not supported.", type);
        Validate.validState(!(type instanceof WildcardType), "Wildcard type %s is not supported.", type);
        return type;
    }

    private Type captureGenericTypeArguments() {
        Type superclass = getClass().getGenericSuperclass();

        ParameterizedType parameterizedSuperclass =
                Validate.isInstanceOf(ParameterizedType.class, superclass, "%s isn't parameterized", superclass);

        return parameterizedSuperclass.getActualTypeArguments()[0];
    }

    private Class<T> loadRepresentedClass() {
        if (runtimeType instanceof Class) {
            return (Class<T>) runtimeType;
        } else if (runtimeType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) runtimeType;
            return (Class<T>) type.getRawType();
        } else {
            throw new IllegalStateException("Unsupported type: " + runtimeType);
        }
    }

    private List<TypeToken<?>> loadRepresentedClassParameters() {
        if (!(runtimeType instanceof ParameterizedType)) {
            return Collections.emptyList();
        }

        ParameterizedType type = (ParameterizedType) runtimeType;

        return Collections.unmodifiableList(
                Arrays.stream(type.getActualTypeArguments())
                     .map(TypeToken::from)
                     .collect(Collectors.toList()));
    }

    public Class<T> representedClass() {
        return representedClass;
    }

    public List<TypeToken<?>> representedClassParameters() {
        return representedClassParameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeToken<?> typeToken = (TypeToken<?>) o;
        return representedClass.equals(typeToken.representedClass) &&
               representedClassParameters.equals(typeToken.representedClassParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(representedClass, representedClassParameters);
    }

    @Override
    public String toString() {
        return ToString.builder("TypeToken")
                       .add("representedClass", representedClass)
                       .add("representedClassParameters", representedClassParameters)
                       .build();
    }
}
