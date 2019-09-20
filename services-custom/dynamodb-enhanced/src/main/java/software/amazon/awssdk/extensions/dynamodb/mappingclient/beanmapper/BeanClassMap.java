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
import java.util.List;

public class BeanClassMap<T> {
    private final Class<T> beanClass;
    private final List<BeanAttribute<?>> attributes;
    private final Constructor<T> constructor;

    private BeanClassMap(Class<T> beanClass, Constructor<T> constructor, List<BeanAttribute<?>> attributes) {
        this.beanClass = beanClass;
        this.constructor = constructor;
        this.attributes = attributes;
    }

    public static <T> BeanClassMap<T> of(Class<T> beanClass,
                                         Constructor<T> constructor,
                                         List<BeanAttribute<?>> attributes) {
        return new BeanClassMap<>(beanClass, constructor, attributes);
    }

    public Class<T> getBeanClass() {
        return beanClass;
    }

    public List<BeanAttribute<?>> getAttributes() {
        return attributes;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanClassMap<?> that = (BeanClassMap<?>) o;

        if (beanClass != null ? ! beanClass.equals(that.beanClass) : that.beanClass != null) return false;
        if (attributes != null ? ! attributes.equals(that.attributes) : that.attributes != null) return false;
        return constructor != null ? constructor.equals(that.constructor) : that.constructor == null;
    }

    @Override
    public int hashCode() {
        int result = beanClass != null ? beanClass.hashCode() : 0;
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (constructor != null ? constructor.hashCode() : 0);
        return result;
    }
}
