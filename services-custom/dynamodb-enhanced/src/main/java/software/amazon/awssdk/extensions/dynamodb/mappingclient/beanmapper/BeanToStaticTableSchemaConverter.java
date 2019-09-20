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
 */

package software.amazon.awssdk.extensions.dynamodb.mappingclient.beanmapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;

import software.amazon.awssdk.extensions.dynamodb.mappingclient.staticmapper.Attribute;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.staticmapper.AttributeTypes;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.staticmapper.StaticTableSchema;

public class BeanToStaticTableSchemaConverter {
    public <T> StaticTableSchema<T> convert(BeanClassMap<T> beanClassMap) {
        beanClassMap.getAttributes().stream()
        return StaticTableSchema.builder()
                                .newItemSupplier(supplierFromConstructor(beanClassMap.getConstructor()))
                                .build();
    }

    private <T> Supplier<T> supplierFromConstructor(Constructor<T> constructor) {
        return () -> {
            try {
                return constructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Could not instantiate a new instance of bean class '" +
                                           constructor.getName() + "'", e);
            }
        };
    }

    private <T> Attribute<?> attributeFromBeanAttribute(BeanAttribute<T> beanAttribute) {
        return Attribute.<T, String>of(beanAttribute.getAttributeName(),
                     getterFunctionFromMethod(beanAttribute.getGetter(), String.class),
                     null,
                     AttributeTypes.stringType()
                     ).get();
    }

    private <T, R> Function<T, R> getterFunctionFromMethod(Method getterMethod, Class<R> returnType) {
        // TODO
        return null;
    }
}
