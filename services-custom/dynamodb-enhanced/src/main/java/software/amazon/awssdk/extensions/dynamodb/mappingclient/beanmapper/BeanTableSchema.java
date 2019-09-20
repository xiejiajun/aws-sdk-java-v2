/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package software.amazon.awssdk.extensions.dynamodb.mappingclient.beanmapper;

import software.amazon.awssdk.extensions.dynamodb.mappingclient.TableMetadata;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.TableSchema;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.staticmapper.StaticTableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collection;
import java.util.Map;

public class BeanTableSchema<T> implements TableSchema<T> {
    private final StaticTableSchema<T> staticTableSchema;
    private final Class<T> beanClass;

    public static <T> BeanTableSchema<T> of(Class<T> beanClass) {
        throw new UnsupportedOperationException();
    }

    BeanTableSchema(Class<T> beanClass, StaticTableSchema<T> staticTableSchema) {
        this.beanClass = beanClass;
        this.staticTableSchema = staticTableSchema;
    }

    public StaticTableSchema<T> getStaticTableSchema() {
        return staticTableSchema;
    }

    public Class<T> getBeanClass() {
        return beanClass;
    }

    @Override
    public T mapToItem(Map<String, AttributeValue> attributeMap) {
        return staticTableSchema.mapToItem(attributeMap);
    }

    @Override
    public Map<String, AttributeValue> itemToMap(T item, boolean ignoreNulls) {
        return staticTableSchema.itemToMap(item, ignoreNulls);
    }

    @Override
    public Map<String, AttributeValue> itemToMap(T item, Collection<String> attributes) {
        return staticTableSchema.itemToMap(item, attributes);
    }

    @Override
    public AttributeValue getAttributeValue(T item, String key) {
        return staticTableSchema.getAttributeValue(item, key);
    }

    @Override
    public TableMetadata getTableMetadata() {
        return staticTableSchema.getTableMetadata();
    }
}
