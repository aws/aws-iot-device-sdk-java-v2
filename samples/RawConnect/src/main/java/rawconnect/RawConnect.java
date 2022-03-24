/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package rawconnect;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.mqtt.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import utils.commandlineutils.CommandLineUtils;

public class RawConnect {
    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static CommandLineUtils cmdUtils;

    public static void main(String[] args) {

        cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName("RawConnect");
        cmdUtils.addCommonMQTTCommands();
        cmdUtils.registerCommand("key", "<path>", "Path to your key in PEM format.");
        cmdUtils.registerCommand("cert", "<path>", "Path to your client certificate in PEM format.");
        cmdUtils.addCommonProxyCommands();
        cmdUtils.registerCommand("client_id", "<int>", "Client id to use (optional, default='test-*').");
        cmdUtils.registerCommand("username", "<str>", "Username to use as part of the connection/authentication process.");
        cmdUtils.registerCommand("password", "<str>", "Password to use as part of the connection/authentication process.");
        cmdUtils.registerCommand("protocol", "<str>", "ALPN protocol to use (optional, default='x-amzn-mqtt-ca').");
        cmdUtils.registerCommand("auth_params", "<comma delimited list>",
                "Comma delimited list of auth parameters. For websockets these will be set as headers. " +
                "For raw mqtt these will be appended to user_name. (optional)");
        cmdUtils.sendArguments(args);

        String endpoint = cmdUtils.getCommandRequired("endpoint", "");
        String clientId = cmdUtils.getCommandOrDefault("client_id", "test-" + UUID.randomUUID().toString());
        String caPath = cmdUtils.getCommandOrDefault("ca_file", "");
        String certPath = cmdUtils.getCommandRequired("cert", "");
        String keyPath = cmdUtils.getCommandRequired("key", "");
        String proxyHost = cmdUtils.getCommandOrDefault("proxy_host", "");
        int proxyPort = Integer.parseInt(cmdUtils.getCommandOrDefault("proxy_port", "8080"));
        String userName = cmdUtils.getCommandRequired("username", "");
        String password = cmdUtils.getCommandRequired("password", "");
        String protocolName = cmdUtils.getCommandOrDefault("protocol", "x-amzn-mqtt-ca");
        List<String> authParams = null;
        if (cmdUtils.hasCommand("auth_params")) {
            authParams = Arrays.asList(cmdUtils.getCommand("auth_params").split("\\s*,\\s*"));
        }

        MqttClientConnectionEvents callbacks = new MqttClientConnectionEvents() {
            @Override
            public void onConnectionInterrupted(int errorCode) {
                if (errorCode != 0) {
                    System.out.println("Connection interrupted: " + errorCode + ": " + CRT.awsErrorString(errorCode));
                }
            }

            @Override
            public void onConnectionResumed(boolean sessionPresent) {
                System.out.println("Connection resumed: " + (sessionPresent ? "existing session" : "clean session"));
            }
        };

        if (authParams != null && authParams.size() > 0) {
            if (userName.length() > 0) {
                StringBuilder usernameBuilder = new StringBuilder();

                usernameBuilder.append(userName);
                usernameBuilder.append("?");
                for (int i = 0; i < authParams.size(); ++i) {
                    usernameBuilder.append(authParams.get(i));
                    if (i + 1 < authParams.size()) {
                        usernameBuilder.append("&");
                    }
                }
                userName = usernameBuilder.toString();
            }
        }

        try(
            TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtlsFromPath(certPath, keyPath)) {

            if (caPath != null) {
                tlsContextOptions.overrideDefaultTrustStoreFromPath(null, caPath);
            }

            int port = 8883;
            if (TlsContextOptions.isAlpnSupported()) {
                port = 443;
                tlsContextOptions.withAlpnList(protocolName);
            }

            try(TlsContext tlsContext = new TlsContext(tlsContextOptions);
                MqttClient client = new MqttClient(tlsContext);
                MqttConnectionConfig config = new MqttConnectionConfig()) {

                config.setMqttClient(client);
                config.setClientId(clientId);
                config.setConnectionCallbacks(callbacks);
                config.setCleanSession(true);
                config.setEndpoint(endpoint);
                config.setPort(port);

                if (userName != null && userName.length() > 0) {
                    config.setLogin(userName, password);
                }

                try (MqttClientConnection connection = new MqttClientConnection(config)) {

                    // Connect and disconnect using the connection we created
                    // (see sampleConnectAndDisconnect for implementation)
                    cmdUtils.sampleConnectAndDisconnect(connection);
                }
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception encountered: " + ex.toString());
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}
