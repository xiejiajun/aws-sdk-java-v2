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

import java.lang.reflect.Method;

public class BeanAttribute<T> {
    private final String attributeName;
    private final Class<T> attributeType;
    private final Method getter;
    private final Method setter;

    private BeanAttribute(String attributeName, Class<T> attributeType, Method getter, Method setter) {
        this.attributeName = attributeName;
        this.attributeType = attributeType;
        this.getter = getter;
        this.setter = setter;
    }

    public static <T> BeanAttribute<T> of(String attributeName, Class<T> attributeType, Method getter, Method setter) {
        return new BeanAttribute<>(attributeName, attributeType, getter, setter);
    }

    public String getAttributeName() {
        return attributeName;
    }

    public Class<T> getAttributeType() {
        return attributeType;
    }

    public Method getGetter() {
        return getter;
    }

    public Method getSetter() {
        return setter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanAttribute<?> that = (BeanAttribute<?>) o;

        if (attributeName != null ? ! attributeName.equals(that.attributeName) : that.attributeName != null)
            return false;
        if (attributeType != null ? ! attributeType.equals(that.attributeType) : that.attributeType != null)
            return false;
        if (getter != null ? ! getter.equals(that.getter) : that.getter != null) return false;
        return setter != null ? setter.equals(that.setter) : that.setter == null;
    }

    @Override
    public int hashCode() {
        int result = attributeName != null ? attributeName.hashCode() : 0;
        result = 31 * result + (attributeType != null ? attributeType.hashCode() : 0);
        result = 31 * result + (getter != null ? getter.hashCode() : 0);
        result = 31 * result + (setter != null ? setter.hashCode() : 0);
        return result;
    }
}
