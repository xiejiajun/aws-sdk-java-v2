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

public class AnnotatedBeanAttribute<BeanT, GetterT, SetterT> {
    private final String attributeName;
    private final AnnotatedBeanAttributeGetter<BeanT, GetterT> getter;
    private final AnnotatedBeanAttributeSetter<BeanT, SetterT> setter;

    public AnnotatedBeanAttribute(Class<BeanT> beanClass, String attributeName, Method getter, Method setter) {
        this.attributeName = attributeName;
        this.getter = AnnotatedBeanAttributeGetter.from(beanClass, getter);
        this.setter = AnnotatedBeanAttributeSetter.from(beanClass, setter);
    }

    public static <BeanT, GetterT, SetterT> AnnotatedBeanAttribute<BeanT, GetterT, SetterT> from(Class<BeanT> beanClass,
                                                                                                 String attributeName,
                                                                                                 Method getter,
                                                                                                 Method setter) {
        return new AnnotatedBeanAttribute<>(beanClass, attributeName, getter, setter);
    }

    public String attributeName() {
        return attributeName;
    }

    public AnnotatedBeanAttributeGetter<BeanT, GetterT> getter() {
        return getter;
    }

    public AnnotatedBeanAttributeSetter<BeanT, SetterT> setter() {
        return setter;
    }

    public enum Type {
        GETTER,
        SETTER
    }
}
