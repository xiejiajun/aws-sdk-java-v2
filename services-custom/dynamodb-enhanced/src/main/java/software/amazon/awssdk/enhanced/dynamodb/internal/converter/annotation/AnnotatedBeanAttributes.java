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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotatedBeanAttributes<BeanT> {
    private final List<AnnotatedBeanAttribute<BeanT, ?, ?>> attributes;

    public AnnotatedBeanAttributes(Class<BeanT> beanType) {
        Method[] methods = beanType.getMethods();
        Map<String, PartialAnnotatedBeanAttribute> partialAttributes = new HashMap<>();

        for (Method method : methods) {
            AttributeUtils.getAttributeName(method).ifPresent(attributeName -> {
                PartialAnnotatedBeanAttribute partialAttribute =
                        partialAttributes.computeIfAbsent(attributeName, x -> new PartialAnnotatedBeanAttribute());

                switch (AttributeUtils.getAttributeMethodType(method)) {
                    case GETTER:
                        partialAttribute.getter = method;
                        break;
                    case SETTER:
                        partialAttribute.setter = method;
                        break;
                    default:
                        throw new IllegalStateException();
                }
            });
        }

        List<AnnotatedBeanAttribute<BeanT, ?, ?>> attributes = new ArrayList<>();

        partialAttributes.forEach((name, attribute) -> {
            // Skip attributes that are missing a getter or setter
            if (attribute.getter == null || attribute.setter == null) {
                return;
            }

            attributes.add(AnnotatedBeanAttribute.from(beanType, name, attribute.getter, attribute.setter));
        });

        this.attributes = Collections.unmodifiableList(attributes);
    }

    public static <BeanT> AnnotatedBeanAttributes<BeanT> from(Class<BeanT> beanType) {
        return new AnnotatedBeanAttributes<>(beanType);
    }

    public List<AnnotatedBeanAttribute<BeanT, ?, ?>> attributes() {
        return attributes;
    }

    private static final class PartialAnnotatedBeanAttribute {
        private Method getter;
        private Method setter;
    }
}
