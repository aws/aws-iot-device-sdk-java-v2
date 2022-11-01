/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot;


import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.auth.signing.AwsSigner;
import software.amazon.awssdk.crt.auth.signing.AwsSigningConfig;
import software.amazon.awssdk.crt.http.HttpRequest;
import software.amazon.awssdk.crt.mqtt5.Mqtt5WebsocketHandshakeTransformArgs;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * A websocket handshake transformer that adds a sigv4 signature for the handshake to the request.
 * Required in order to connect to Aws IoT via websockets using sigv4 authentication.
 */
public class AwsMqtt5Sigv4HandshakeTransformer extends CrtResource implements Consumer<Mqtt5WebsocketHandshakeTransformArgs> {

    AwsSigningConfig signingConfig;

    /**
     *
     * @param signingConfig sigv4 configuration for the signing process
     */
    public AwsMqtt5Sigv4HandshakeTransformer(AwsSigningConfig signingConfig) {
        addReferenceTo(signingConfig);
        this.signingConfig = signingConfig;
    }

    /**
     * Required override method that must begin the release process of the acquired native handle
     */
    @Override
    protected void releaseNativeHandle() {}

    /**
     * Override that determines whether a resource releases its dependencies at the same time the native handle is released or if it waits.
     * Resources with asynchronous shutdown processes should override this with false, and establish a callback from native code that
     * invokes releaseReferences() when the asynchronous shutdown process has completed.  See HttpClientConnectionManager for an example.
     */
    @Override
    protected boolean canReleaseReferencesImmediately() { return true; }

    /**
     * Modifies the handshake request to include its sigv4 signature
     * @param handshakeArgs handshake transformation completion object
     */
    public void accept(Mqtt5WebsocketHandshakeTransformArgs handshakeArgs) {
        try (AwsSigningConfig config = signingConfig.clone()) {
            config.setTime(System.currentTimeMillis());

            CompletableFuture<HttpRequest> signingFuture = AwsSigner.signRequest(handshakeArgs.getHttpRequest(), config);
            signingFuture.whenComplete((HttpRequest request, Throwable error) -> {
                if (error != null) {
                    handshakeArgs.completeExceptionally(error);
                } else {
                    handshakeArgs.complete(request);
                }});
        }
    }
}
