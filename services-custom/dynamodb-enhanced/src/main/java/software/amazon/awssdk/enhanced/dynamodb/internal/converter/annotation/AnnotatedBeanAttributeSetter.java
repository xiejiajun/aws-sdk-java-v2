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
import software.amazon.awssdk.enhanced.dynamodb.converter.bean.BeanAttributeSetter;
import software.amazon.awssdk.enhanced.dynamodb.model.TypeToken;
import software.amazon.awssdk.utils.Validate;

public class AnnotatedBeanAttributeSetter<BeanT, SetterT> {
    private final TypeToken<SetterT> inputType;
    private final BeanAttributeSetter<BeanT, SetterT> setterFunction;

    private AnnotatedBeanAttributeSetter(TypeToken<SetterT> inputType, BeanAttributeSetter<BeanT, SetterT> setterFunction) {
        this.inputType = inputType;
        this.setterFunction = setterFunction;
    }

    @SuppressWarnings("unchecked")
    public static <BeanT, SetterT> AnnotatedBeanAttributeSetter<BeanT, SetterT> from(Class<BeanT> beanClass, Method setter) {
        Validate.isTrue(setter.getParameterCount() == 1,
                        "%s.%s doesn't have just 1 parameter, despite being named like a setter.",
                        beanClass, setter.getName());

        BeanAttributeSetter<BeanT, SetterT> setterFunction =
                LambdaToMethodBridgeBuilder.create(BeanAttributeSetter.class)
                                           .lambdaMethodName("accept")
                                           .postEraseLambdaSignature(Void.class, Object.class, Object.class)
                                           .preEraseLambdaSignature(Void.class, beanClass, setter.getParameters()[0].getType())
                                           .targetMethod(setter)
                                           .build();

        TypeToken<SetterT> returnType = (TypeToken<SetterT>) TypeToken.of(setter.getReturnType());
        return new AnnotatedBeanAttributeSetter<>(returnType, setterFunction);
    }

    public TypeToken<SetterT> inputType() {
        return inputType;
    }

    public BeanAttributeSetter<BeanT, SetterT> function() {
        return setterFunction;
    }
}
