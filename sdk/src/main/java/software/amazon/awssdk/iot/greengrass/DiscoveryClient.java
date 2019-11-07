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

package software.amazon.awssdk.iot.greengrass;

import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.iot.greengrass.model.DiscoverResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import software.amazon.awssdk.crt.http.CrtHttpStreamHandler;
import software.amazon.awssdk.crt.http.HttpClientConnectionManager;
import software.amazon.awssdk.crt.http.HttpHeader;
import software.amazon.awssdk.crt.http.HttpRequest;
import software.amazon.awssdk.crt.http.HttpStream;

public class DiscoveryClient {
    private final Gson gson = new GsonBuilder().create();
    private HttpClientConnectionManager connectionManager;
    private String endpoint;

    public DiscoveryClient(ClientBootstrap bootstrap, SocketOptions socketOptions, TlsContext tlsContext, String region)
            throws URISyntaxException {
        this.endpoint = String.format("greengrass-ats.iot.%s.amazonaws.com", region);
        int port = TlsContextOptions.isAlpnSupported() ? 443 : 8443;
        this.connectionManager = new HttpClientConnectionManager(bootstrap, socketOptions, tlsContext,
                new URI(String.format("https://%s:%d", this.endpoint, port)));
    }

    public CompletableFuture<DiscoverResponse> discover(String thingName) {
        return this.connectionManager.acquireConnection()
            .thenCompose((connection) -> {
                CompletableFuture<DiscoverResponse> result = new CompletableFuture<>();
                HttpRequest request = new HttpRequest("GET", String.format("/greengrass/discover/thing/%s", thingName));
                connection.makeRequest(request, new CrtHttpStreamHandler() {
                    String response = "";
                    @Override
                    public void onResponseHeaders(HttpStream stream, int responseStatusCode, int blockType, HttpHeader[] nextHeaders) {}

                    @Override
                    public int onResponseBody(HttpStream stream, byte[] bodyBytesIn) {
                        response += new String(bodyBytesIn, StandardCharsets.UTF_8);
                        return bodyBytesIn.length;
                    }
                
                    @Override
                    public void onResponseComplete(HttpStream stream, int errorCode) {
                        try {
                            DiscoverResponse discoverResponse = gson.fromJson(response, DiscoverResponse.class);
                            result.complete(discoverResponse);
                        } catch (Exception ex) {
                            result.completeExceptionally(ex);
                        }
                    }
                });
                return result;
            });
    }
}
