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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import software.amazon.awssdk.extensions.dynamodb.mappingclient.staticmapper.Attribute;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.staticmapper.StaticTableSchema;

public class BeanToStaticTableSchemaConverterTest {
    public static class SimpleBean {
        public String getProperty() {
            return null;
        }

        public void setProperty(String value) {
        }
    }

    private static final Method PROPERTY_GETTER;
    private static final Method PROPERTY_SETTER;
    private static final Constructor<SimpleBean> CONSTRUCTOR;

    static {
        try {
            CONSTRUCTOR = SimpleBean.class.getConstructor();
            PROPERTY_GETTER = SimpleBean.class.getMethod("getProperty");
            PROPERTY_SETTER = SimpleBean.class.getMethod("setProperty", String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public final BeanAttribute propertyAttribute = BeanAttribute.of("property", PROPERTY_GETTER, PROPERTY_SETTER);
    public final BeanClassMap<SimpleBean> beanClassMap = BeanClassMap.of(SimpleBean.class,
                                                                         CONSTRUCTOR,
                                                                         Collections.singletonList(propertyAttribute));

    public final BeanToStaticTableSchemaConverter beanToStaticTableSchemaConverter =
        new BeanToStaticTableSchemaConverter();

    @Test
    public void convert_constructor() {
        StaticTableSchema<SimpleBean> staticTableSchema = beanToStaticTableSchemaConverter.convert(beanClassMap);

        SimpleBean result = staticTableSchema.getNewItemSupplier().get();

        assertThat(result, instanceOf(SimpleBean.class));
    }

    @Test
    public void convert_getter() {
        StaticTableSchema<SimpleBean> staticTableSchema = beanToStaticTableSchemaConverter.convert(beanClassMap);

        List<Attribute<SimpleBean>> attributes = staticTableSchema.getAttributeMappers();
        assertThat(attributes.size(), is(1));
        Attribute<SimpleBean> attribute = attributes.get(0);

        SimpleBean mockSimpleBean = Mockito.mock(SimpleBean.class);
        when(mockSimpleBean.getProperty()).thenReturn("test-string");
        assertThat(attribute.getAttributeName(), is("property"));
        assertThat(attribute.getGetAttributeMethod().apply(mockSimpleBean), is("test-string"));
        verify(mockSimpleBean).getProperty();
    }
}