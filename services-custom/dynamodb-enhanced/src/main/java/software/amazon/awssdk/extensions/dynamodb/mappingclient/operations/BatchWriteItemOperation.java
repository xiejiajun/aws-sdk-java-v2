/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package software.amazon.awssdk.extensions.dynamodb.mappingclient.operations;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.DatabaseOperation;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.MapperExtension;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.model.BatchWriteResult;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;

@SdkInternalApi
public class BatchWriteItemOperation implements DatabaseOperation<BatchWriteItemRequest,
                                 BatchWriteItemResponse, BatchWriteResult> {

    private final BatchWriteItemEnhancedRequest request;

    private BatchWriteItemOperation(BatchWriteItemEnhancedRequest request) {
        this.request = request;
    }

    public static BatchWriteItemOperation create(BatchWriteItemEnhancedRequest request) {
        return new BatchWriteItemOperation(request);
    }

    @Override
    public BatchWriteItemRequest generateRequest(MapperExtension mapperExtension) {
        Map<String, Collection<WriteRequest>> requestItems = new HashMap<>();
        request.writeBatches().forEach(writeBatch -> writeBatch.addWriteRequestsToMap(requestItems));

        return BatchWriteItemRequest.builder()
                                    .requestItems(Collections.unmodifiableMap(requestItems))
                                    .build();
    }

    @Override
    public BatchWriteResult transformResponse(BatchWriteItemResponse response, MapperExtension mapperExtension) {
        return BatchWriteResult.builder().unprocessedRequests(response.unprocessedItems()).build();
    }

    @Override
    public Function<BatchWriteItemRequest, BatchWriteItemResponse> serviceCall(DynamoDbClient dynamoDbClient) {
        return dynamoDbClient::batchWriteItem;
    }

    @Override
    public Function<BatchWriteItemRequest, CompletableFuture<BatchWriteItemResponse>> asyncServiceCall(
        DynamoDbAsyncClient dynamoDbAsyncClient) {

        return dynamoDbAsyncClient::batchWriteItem;
    }

}
