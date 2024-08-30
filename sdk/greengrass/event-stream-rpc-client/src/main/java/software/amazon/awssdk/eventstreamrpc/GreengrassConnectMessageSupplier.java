/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

/* This file is part of greengrass-ipc project. */

package software.amazon.awssdk.eventstreamrpc;

import com.google.gson.Gson;
import software.amazon.awssdk.crt.eventstream.Header;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * The connect message supplier for Greengrass
 */
public class GreengrassConnectMessageSupplier {

    /**
     * Returns a new connect message supplier using the given token
     * @param authToken The auth token to use
     * @return A new connect message supplier
     */
    public static Supplier<CompletableFuture<MessageAmendInfo>> connectMessageSupplier(String authToken) {
        return () -> {
            final List<Header> headers = new LinkedList<>();
            GreengrassEventStreamConnectMessage connectMessage = new GreengrassEventStreamConnectMessage();
            connectMessage.setAuthToken(authToken);
            String payload = new Gson().toJson(connectMessage);
            return CompletableFuture.completedFuture(new MessageAmendInfo(headers, payload.getBytes(StandardCharsets.UTF_8)));
        };
    }
}
