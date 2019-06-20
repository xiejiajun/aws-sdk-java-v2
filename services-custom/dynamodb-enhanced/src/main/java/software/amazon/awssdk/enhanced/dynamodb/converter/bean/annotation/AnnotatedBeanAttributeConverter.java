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

package software.amazon.awssdk.enhanced.dynamodb.converter.bean.annotation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import software.amazon.awssdk.enhanced.dynamodb.converter.attribute.ConversionContext;
import software.amazon.awssdk.enhanced.dynamodb.converter.attribute.SubtypeAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bean.BeanAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.annotation.AnnotatedBean;
import software.amazon.awssdk.enhanced.dynamodb.model.ItemAttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;

public class AnnotatedBeanAttributeConverter implements SubtypeAttributeConverter<Object> {
    private ConcurrentMap<Class<?>, BeanAttributeConverter<?>> STATIC_CONVERTER_CACHE = new ConcurrentHashMap<>();

    private AnnotatedBeanAttributeConverter() {}

    public static AnnotatedBeanAttributeConverter create() {
        return new AnnotatedBeanAttributeConverter();
    }

    @Override
    public TypeToken<Object> type() {
        return TypeToken.of(Object.class);
    }

    @Override
    public ItemAttributeValue toAttributeValue(Object input, ConversionContext context) {
        BeanAttributeConverter<?> converter = STATIC_CONVERTER_CACHE.get(input.getClass());
        if (converter != null) {
            return toAttributeValue(converter, input, context);
        }

        return toAttributeValue(cacheAndGetBeanConverter(input.getClass()), context);
    }

    private <T> ItemAttributeValue toAttributeValue(BeanAttributeConverter<T> converter,
                                                    Object input,
                                                    ConversionContext context) {
        T castInput = converter.type().rawClass().cast(input);
        return converter.toAttributeValue(castInput, context);
    }

    @Override
    public <U> U fromAttributeValue(ItemAttributeValue input, TypeToken<U> desiredType, ConversionContext context) {
        BeanAttributeConverter<U> converter = (BeanAttributeConverter<U>) STATIC_CONVERTER_CACHE.get(desiredType.rawClass());
        if (converter != null) {
            return converter.fromAttributeValue(input, context);
        }

        return cacheAndGetBeanConverter(desiredType.rawClass()).fromAttributeValue(input, context);
    }

    private <T> BeanAttributeConverter<T> cacheAndGetBeanConverter(Class<T> beanClass) {
        BeanAttributeConverter<T> converter = BeanAttributeConverter.create(AnnotatedBean.from(beanClass).toBeanSchema());
        STATIC_CONVERTER_CACHE.put(beanClass, converter);
        return converter;
    }
}
