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
import software.amazon.awssdk.enhanced.dynamodb.converter.bean.BeanAttributeGetter;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.utils.Validate;

public class AnnotatedBeanAttributeGetter<BeanT, GetterT> {
    private final TypeToken<GetterT> returnType;
    private final BeanAttributeGetter<BeanT, GetterT> getterFunction;

    private AnnotatedBeanAttributeGetter(TypeToken<GetterT> returnType, BeanAttributeGetter<BeanT, GetterT> getterFunction) {
        this.returnType = returnType;
        this.getterFunction = getterFunction;
    }

    @SuppressWarnings("unchecked")
    public static <BeanT, GetterT> AnnotatedBeanAttributeGetter<BeanT, GetterT> from(Class<BeanT> beanClass, Method getter) {
        Validate.isTrue(getter.getParameterCount() == 0,
                        "%s.%s has parameters, despite being named like a getter.",
                        beanClass, getter.getName());

        BeanAttributeGetter<BeanT, GetterT> getterFunction =
                LambdaToMethodBridgeBuilder.create(BeanAttributeGetter.class)
                                           .lambdaMethodName("apply")
                                           .postEraseLambdaSignature(Object.class, Object.class)
                                           .preEraseLambdaSignature(getter.getReturnType(), beanClass)
                                           .targetMethod(getter)
                                           .build();

        TypeToken<GetterT> returnType = (TypeToken<GetterT>) TypeToken.of(getter.getReturnType());
        return new AnnotatedBeanAttributeGetter<>(returnType, getterFunction);
    }

    public TypeToken<GetterT> returnType() {
        return returnType;
    }

    public BeanAttributeGetter<BeanT, GetterT> function() {
        return getterFunction;
    }
}
