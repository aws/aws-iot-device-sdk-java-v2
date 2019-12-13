/* Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

* This file is generated
*/

package software.amazon.awssdk.iot;


import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.auth.signing.AwsSigner;
import software.amazon.awssdk.crt.auth.signing.AwsSigningConfig;
import software.amazon.awssdk.crt.http.HttpRequest;
import software.amazon.awssdk.crt.mqtt.WebsocketHandshakeTransformArgs;

import java.time.Instant;
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
            config.setTime(Instant.now());

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
