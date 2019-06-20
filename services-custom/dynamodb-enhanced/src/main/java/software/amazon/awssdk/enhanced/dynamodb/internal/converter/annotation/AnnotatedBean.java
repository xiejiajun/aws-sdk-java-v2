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

package software.amazon.awssdk.enhanced.dynamodb.internal.converter.annotation;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import software.amazon.awssdk.enhanced.dynamodb.converter.attribute.bundled.DefaultAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.converter.bean.AsymmetricBeanAttribute;
import software.amazon.awssdk.enhanced.dynamodb.converter.bean.BeanSchema;
import software.amazon.awssdk.enhanced.dynamodb.converter.bean.annotation.Item;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.utils.Validate;

public class AnnotatedBean<T> {
    private final Class<T> beanType;
    private final AnnotatedBeanConstructor<T> constructor;
    private final AnnotatedBeanAttributes<T> attributes;

    private AnnotatedBean(Class<T> beanType) {
        Validate.isTrue(beanType.getAnnotation(Item.class) != null, "Provided type is not an annotated bean: %s", beanType);
        this.beanType = beanType;
        this.constructor = AnnotatedBeanConstructor.from(beanType);
        this.attributes = AnnotatedBeanAttributes.from(beanType);
    }

    public static <T> AnnotatedBean<T> from(Class<T> beanType) {
        return new AnnotatedBean<>(beanType);
    }

    public BeanSchema<T> toBeanSchema() {
        return BeanSchema.builder(beanType)
                         .constructor(constructor)
                         .addAsymmetricAttributes(convertAttributes())
                         .build();
    }

    private Collection<AsymmetricBeanAttribute<T, ?, ?>> convertAttributes() {
        return attributes.attributes().stream()
                         .map(this::toAttributeSchema)
                         .collect(toList());
    }

    private <GetterT, SetterT> AsymmetricBeanAttribute<T, GetterT, SetterT> toAttributeSchema(
            AnnotatedBeanAttribute<T, GetterT, SetterT> attribute) {
        DefaultAttributeConverter defaultConverter = DefaultAttributeConverter.create();

        return AsymmetricBeanAttribute.builder(TypeToken.of(beanType),
                                               attribute.getter().returnType(),
                                               attribute.setter().inputType())
                                      .attributeName(attribute.attributeName())
                                      .getter(attribute.getter().function())
                                      .setter(attribute.setter().function())
                                      .getterConverter(defaultConverter)
                                      .getterConverter(defaultConverter)
                                      .build();
    }
}
