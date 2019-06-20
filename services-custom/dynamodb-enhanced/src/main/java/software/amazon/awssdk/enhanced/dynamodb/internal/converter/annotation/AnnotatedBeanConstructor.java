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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class AnnotatedBeanConstructor<BeanT> implements Supplier<BeanT> {
    private final Supplier<BeanT> delegate;

    private AnnotatedBeanConstructor(Class<BeanT> beanClass, Constructor<BeanT> constructor) {
        this.delegate = () -> {
            try {
                return constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | RuntimeException e) {
                throw new IllegalArgumentException("Failed to instantiate " + beanClass, e);
            }
        };
    }

    public static <BeanT> AnnotatedBeanConstructor<BeanT> from(Class<BeanT> beanClass) {
        try {
            return new AnnotatedBeanConstructor<>(beanClass, beanClass.getDeclaredConstructor());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(beanClass + " must contain zero-arg constructor", e);
        }
    }

    @Override
    public BeanT get() {
        return delegate.get();
    }
}
