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

package software.amazon.awssdk.enhanced.dynamodb.converter.bean;

import java.util.LinkedHashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.enhanced.dynamodb.converter.attribute.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.attribute.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeConvertingVisitor;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
@ThreadSafe
@Immutable
public class StaticBeanItemAttributeConverter<T> implements AttributeConverter<T> {
    private final BeanItemSchema<T> schema;

    private StaticBeanItemAttributeConverter(BeanItemSchema<T> schema) {
        this.schema = schema;
    }

    public static <T> StaticBeanItemAttributeConverter<T> create(BeanItemSchema<T> schema) {
        return new StaticBeanItemAttributeConverter<>(schema);
    }

    @Override
    public TypeToken<T> type() {
        return schema.beanType();
    }

    @Override
    public ItemAttributeValue toAttributeValue(T input, ConversionContext context) {
        Map<String, ItemAttributeValue> mappedValues = new LinkedHashMap<>();
        schema.attributeSchemas().forEach(attr -> mappedValues.put(attr.attributeName(), mapAttribute(input, context, attr)));
        return ItemAttributeValue.fromMap(mappedValues);
    }

    private <U> ItemAttributeValue mapAttribute(T bean,
                                                ConversionContext context,
                                                BeanAttributeSchema<T, U> attributeSchema) {
        U attribute = attributeSchema.getter().apply(bean);

        if (attribute == null) {
            return ItemAttributeValue.nullValue();
        }

        return attributeSchema.converter()
                              .toAttributeValue(attribute, context.toBuilder()
                                                                  .attributeName(attributeSchema.attributeName())
                                                                  .build());
    }

    @Override
    public T fromAttributeValue(ItemAttributeValue input, ConversionContext context) {
        return input.convert(new TypeConvertingVisitor<T>(schema.beanType().rawClass(), StaticBeanItemAttributeConverter.class) {
            @Override
            public T convertMap(Map<String, ItemAttributeValue> value) {
                T response = schema.constructor().get();

                Validate.isInstanceOf(targetType, response,
                                      "Item constructor created a %s, but a %s was requested.",
                                      response.getClass(), targetType);

                schema.attributeSchemas().forEach(attributeSchema -> {
                    ItemAttributeValue mappedValue = value.get(attributeSchema.attributeName());
                    convertAndSet(mappedValue, response, attributeSchema);
                });

                return response;
            }

            private <U> void convertAndSet(ItemAttributeValue mappedValue,
                                           T response,
                                           BeanAttributeSchema<T, U> attributeSchema) {
                AttributeConverter<U> converter = attributeSchema.converter();
                Object unmappedValue =
                        converter.fromAttributeValue(mappedValue,
                                                     context.toBuilder()
                                                            .attributeName(attributeSchema.attributeName())
                                                            .build());

                attributeSchema.setter().accept(response, attributeSchema.attributeType().rawClass().cast(unmappedValue));
            }
        });
    }
}
