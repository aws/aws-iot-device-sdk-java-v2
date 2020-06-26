/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot;


import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.auth.signing.AwsSigner;
import software.amazon.awssdk.crt.auth.signing.AwsSigningConfig;
import software.amazon.awssdk.crt.http.HttpRequest;
import software.amazon.awssdk.crt.mqtt.WebsocketHandshakeTransformArgs;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AwsSigv4HandshakeTransformer extends CrtResource implements Consumer<WebsocketHandshakeTransformArgs> {

    AwsSigningConfig signingConfig;

    public AwsSigv4HandshakeTransformer(AwsSigningConfig signingConfig) {
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

    public void accept(WebsocketHandshakeTransformArgs handshakeArgs) {
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
