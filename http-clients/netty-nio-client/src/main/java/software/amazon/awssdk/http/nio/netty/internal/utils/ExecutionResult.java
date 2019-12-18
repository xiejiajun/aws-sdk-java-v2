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

package software.amazon.awssdk.http.nio.netty.internal.utils;

import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public class ExecutionResult {
    private static final Logger log = Logger.loggerFor(ExecutionResult.class);

    private final CompletableFuture<Void> inputFuture = new CompletableFuture<>();
    private final CompletableFuture<Void> outputFuture = new CompletableFuture<>();

    private ExecutionResult(Builder builder) {
        inputFuture.whenComplete((r, t) -> {
            if (t != null) {
                outputFuture.completeExceptionally(decorateException(t));
            } else {
                outputFuture.complete(r);
            }
        });

        for (Consumer<Throwable> failureListener : builder.failureListeners) {
            outputFuture.whenComplete((r, t) -> {
                if (t != null) {
                    try {
                        failureListener.accept(t);
                    } catch (Throwable newT) {
                        log.error(() -> "Failure listener raised exception while handling exception. "
                                        + "The raised exception follows.", newT);
                    }
                }
            });
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ExecutionResult create() {
        return builder().build();
    }

    public CompletableFuture<Void> outputFuture() {
        return outputFuture;
    }

    public boolean trySucceedExecution() {
        return inputFuture.complete(null);
    }

    public boolean tryFailExecution(Throwable cause) {
        cause = decorateException(cause);
        return inputFuture.completeExceptionally(cause);
    }

    private static Throwable decorateException(Throwable originalCause) {
        if (isAcquireTimeoutException(originalCause)) {
            return new RuntimeException(getMessageForAcquireTimeoutException(), originalCause);
        } else if (isTooManyPendingAcquiresException(originalCause)) {
            return new RuntimeException(getMessageForTooManyAcquireOperationsError(), originalCause);
        } else if (originalCause instanceof ReadTimeoutException) {
            return new IOException("Read timed out", originalCause);
        } else if (originalCause instanceof WriteTimeoutException) {
            return new IOException("Write timed out", originalCause);
        } else if (originalCause instanceof ClosedChannelException) {
            return new IOException(getMessageForClosedChannel(), originalCause);
        }

        return originalCause;
    }

    private static boolean isAcquireTimeoutException(Throwable originalCause) {
        String message = originalCause.getMessage();
        return originalCause instanceof TimeoutException &&
               message != null &&
               message.contains("Acquire operation took longer");
    }

    private static boolean isTooManyPendingAcquiresException(Throwable originalCause) {
        String message = originalCause.getMessage();
        return originalCause instanceof IllegalStateException &&
               message != null &&
               originalCause.getMessage().contains("Too many outstanding acquire operations");
    }

    private static String getMessageForAcquireTimeoutException() {
        return "Acquire operation took longer than the configured maximum time. This indicates that a request cannot get a "
               + "connection from the pool within the specified maximum time. This can be due to high request rate.\n"

               + "Consider taking any of the following actions to mitigate the issue: increase max connections, "
               + "increase acquire timeout, or slowing the request rate.\n"

               + "Increasing the max connections can increase client throughput (unless the network interface is already "
               + "fully utilized), but can eventually start to hit operation system limitations on the number of file "
               + "descriptors used by the process. If you already are fully utilizing your network interface or cannot "
               + "further increase your connection count, increasing the acquire timeout gives extra time for requests to "
               + "acquire a connection before timing out. If the connections doesn't free up, the subsequent requests "
               + "will still timeout.\n"

               + "If the above mechanisms are not able to fix the issue, try smoothing out your requests so that large "
               + "traffic bursts cannot overload the client, being more efficient with the number of times you need to "
               + "call AWS, or by increasing the number of hosts sending requests.";
    }

    private static String getMessageForTooManyAcquireOperationsError() {
        return "Maximum pending connection acquisitions exceeded. The request rate is too high for the client to keep up.\n"

               + "Consider taking any of the following actions to mitigate the issue: increase max connections, "
               + "increase max pending acquire count, decrease pool lease timeout, or slowing the request rate.\n"

               + "Increasing the max connections can increase client throughput (unless the network interface is already "
               + "fully utilized), but can eventually start to hit operation system limitations on the number of file "
               + "descriptors used by the process. If you already are fully utilizing your network interface or cannot "
               + "further increase your connection count, increasing the pending acquire count allows extra requests to be "
               + "buffered by the client, but can cause additional request latency and higher memory usage. If your request"
               + " latency or memory usage is already too high, decreasing the lease timeout will allow requests to fail "
               + "more quickly, reducing the number of pending connection acquisitions, but likely won't decrease the total "
               + "number of failed requests.\n"

               + "If the above mechanisms are not able to fix the issue, try smoothing out your requests so that large "
               + "traffic bursts cannot overload the client, being more efficient with the number of times you need to call "
               + "AWS, or by increasing the number of hosts sending requests.";
    }

    private static String getMessageForClosedChannel() {
        return "The channel was closed. This may have been done by the client (e.g. because the request was aborted), " +
               "by the service (e.g. because the request took too long or the client tried to write on a read-only socket), " +
               "or by an intermediary party (e.g. because the channel was idle for too long).";
    }

    public static final class Builder {
        private final List<Consumer<Throwable>> failureListeners = new ArrayList<>();

        private Builder() {}

        public Builder addFailureListener(Consumer<Throwable> listener) {
            this.failureListeners.add(listener);
            return this;
        }

        public ExecutionResult build() {
            return new ExecutionResult(this);
        }
    }

}
