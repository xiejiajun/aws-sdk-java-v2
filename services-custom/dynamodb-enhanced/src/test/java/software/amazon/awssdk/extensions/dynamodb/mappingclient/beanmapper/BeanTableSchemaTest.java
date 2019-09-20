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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.AttributeValues;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.TableMetadata;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.functionaltests.models.FakeItem;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.staticmapper.StaticTableMetadata;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.staticmapper.StaticTableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BeanTableSchemaTest {
    private static final AttributeValue ATTRIBUTE_VALUE = AttributeValues.stringValue("test-attribute");
    private static final FakeItem FAKE_ITEM = FakeItem.createUniqueFakeItem();
    private static final Map<String, AttributeValue> ITEM_MAP;

    static {
        ITEM_MAP = new HashMap<>();
        ITEM_MAP.put("one", ATTRIBUTE_VALUE);
    }

    @Mock
    private StaticTableSchema<FakeItem> mockStaticTableSchema;

    @Mock
    private StaticTableMetadata mockStaticTableMetadata;

    private BeanTableSchema<FakeItem> beanTableSchema;

    @Before
    public void initializeBeanTableSchema() {
        beanTableSchema = new BeanTableSchema<>(FakeItem.class, mockStaticTableSchema);
    }

    @Test
    public void getStaticTableSchema() {
        assertThat(beanTableSchema.getStaticTableSchema(), is(mockStaticTableSchema));
    }

    @Test
    public void getBeanClass() {
        assertThat(beanTableSchema.getBeanClass(), equalTo(FakeItem.class));
    }

    @Test
    public void mapToItem() {
        when(mockStaticTableSchema.mapToItem(anyMap())).thenReturn(FAKE_ITEM);

        FakeItem actualResult = beanTableSchema.mapToItem(ITEM_MAP);

        assertThat(actualResult, is(FAKE_ITEM));
        verify(mockStaticTableSchema).mapToItem(ITEM_MAP);
    }

    @Test
    public void itemToMap() {
        when(mockStaticTableSchema.itemToMap(any(), anyBoolean())).thenReturn(ITEM_MAP);

        Map<String, AttributeValue> result = beanTableSchema.itemToMap(FAKE_ITEM, true);

        assertThat(result, is(ITEM_MAP));
        verify(mockStaticTableSchema).itemToMap(FAKE_ITEM, true);
    }

    @Test
    public void itemToMap_attributes() {
        Collection<String> attributes = Arrays.asList("one", "two");
        when(mockStaticTableSchema.itemToMap(any(), anyCollection())).thenReturn(ITEM_MAP);

        Map<String, AttributeValue> result = beanTableSchema.itemToMap(FAKE_ITEM, attributes);

        assertThat(result, is(ITEM_MAP));
        verify(mockStaticTableSchema).itemToMap(FAKE_ITEM, attributes);
    }

    @Test
    public void getAttributeValue() {
        when(mockStaticTableSchema.getAttributeValue(any(), anyString())).thenReturn(ATTRIBUTE_VALUE);

        AttributeValue result = beanTableSchema.getAttributeValue(FAKE_ITEM, "key");

        assertThat(result, is(ATTRIBUTE_VALUE));
        verify(mockStaticTableSchema).getAttributeValue(FAKE_ITEM, "key");
    }

    @Test
    public void getTableMetadata() {
        when(mockStaticTableSchema.getTableMetadata()).thenReturn(mockStaticTableMetadata);

        TableMetadata result = beanTableSchema.getTableMetadata();

        assertThat(result, is(mockStaticTableMetadata));
    }
}