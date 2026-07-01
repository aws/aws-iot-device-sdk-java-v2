/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot;

import software.amazon.awssdk.crt.iot.MqttRequestResponse;

import java.util.concurrent.CompletableFuture;

/**
 * CompletableFuture variant used internally to chain from a generic callback to a type-specific callback.
 *
 * We need to keep the generic future alive from a garbage collection perspective so that its .whenComplete(...)
 * control flow path will complete this future.
 *
 * I cannot tell from documentation if this is truly necessary.  Does a completion stage have a reference to
 * its predecessor?
 *
 * @param <T>
 */
public class V2ClientFuture<T> extends CompletableFuture<T> {
    private CompletableFuture<MqttRequestResponse> triggeringFuture;

    public V2ClientFuture() {
        super();
    }

    /**
     * Add a ref to the generic future that will complete this future when it completes
     *
     * @param triggeringFuture generic future to keep alive from garbage collection
     */
    public void setTriggeringFuture(CompletableFuture<MqttRequestResponse> triggeringFuture) {
        this.triggeringFuture = triggeringFuture;
    }
}
