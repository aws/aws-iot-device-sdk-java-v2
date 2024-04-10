/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package ServiceTestLifecycleEvents;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.mqtt5.packets.DisconnectPacket;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.crt.mqtt5.Mqtt5ClientOptions;

import java.util.concurrent.CompletableFuture;

final public class ServiceTestLifecycleEvents implements Mqtt5ClientOptions.LifecycleEvents {
    CompletableFuture<Void> connectedFuture = new CompletableFuture<>();
    CompletableFuture<Void> stoppedFuture = new CompletableFuture<>();

    @Override
    public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn onAttemptingConnectReturn) {
        // System.out.println("Mqtt5 Client: Attempting connection...");
    }

    @Override
    public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn onConnectionSuccessReturn) {
        // System.out.println("Mqtt5 Client: Connection success, client ID: "
                + onConnectionSuccessReturn.getNegotiatedSettings().getAssignedClientID());
        connectedFuture.complete(null);
    }

    @Override
    public void onConnectionFailure(Mqtt5Client client, OnConnectionFailureReturn onConnectionFailureReturn) {
        String errorString = CRT.awsErrorString(onConnectionFailureReturn.getErrorCode());
        // System.out.println("Mqtt5 Client: Connection failed with error: " + errorString);
        connectedFuture.completeExceptionally(new Exception("Could not connect: " + errorString));
    }

    @Override
    public void onDisconnection(Mqtt5Client client, OnDisconnectionReturn onDisconnectionReturn) {
        // System.out.println("Mqtt5 Client: Disconnected");
        DisconnectPacket disconnectPacket = onDisconnectionReturn.getDisconnectPacket();
        if (disconnectPacket != null) {
            // System.out.println("\tDisconnection packet code: " + disconnectPacket.getReasonCode());
            // System.out.println("\tDisconnection packet reason: " + disconnectPacket.getReasonString());
        }
    }

    @Override
    public void onStopped(Mqtt5Client client, OnStoppedReturn onStoppedReturn) {
        // System.out.println("Mqtt5 Client: Stopped");
        stoppedFuture.complete(null);
    }
}

