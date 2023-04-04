/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package rawconnect;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.Log;
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

        String input_endpoint = cmdUtils.getCommandRequired("endpoint", "");
        String input_clientId = cmdUtils.getCommandOrDefault("client_id", "test-" + UUID.randomUUID().toString());
        String input_caPath = cmdUtils.getCommandOrDefault("ca_file", "");
        String input_certPath = cmdUtils.getCommandRequired("cert", "");
        String input_keyPath = cmdUtils.getCommandRequired("key", "");
        String input_proxyHost = cmdUtils.getCommandOrDefault("proxy_host", "");
        int input_proxyPort = Integer.parseInt(cmdUtils.getCommandOrDefault("proxy_port", "8080"));
        String input_userName = cmdUtils.getCommandRequired("username", "");
        String input_password = cmdUtils.getCommandRequired("password", "");
        String input_protocolName = cmdUtils.getCommandOrDefault("protocol", "x-amzn-mqtt-ca");
        List<String> input_authParams = null;
        if (cmdUtils.hasCommand("auth_params")) {
            input_authParams = Arrays.asList(cmdUtils.getCommand("auth_params").split("\\s*,\\s*"));
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

        if (input_authParams != null && input_authParams.size() > 0) {
            if (input_userName.length() > 0) {
                StringBuilder usernameBuilder = new StringBuilder();

                usernameBuilder.append(input_userName);
                usernameBuilder.append("?");
                for (int i = 0; i < input_authParams.size(); ++i) {
                    usernameBuilder.append(input_authParams.get(i));
                    if (i + 1 < input_authParams.size()) {
                        usernameBuilder.append("&");
                    }
                }
                input_userName = usernameBuilder.toString();
            }
        }

        try(
            TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMtlsFromPath(input_certPath, input_keyPath)) {

            if (input_caPath != null) {
                tlsContextOptions.overrideDefaultTrustStoreFromPath(null, input_caPath);
            }

            int port = 8883;
            if (TlsContextOptions.isAlpnSupported()) {
                port = 443;
                tlsContextOptions.withAlpnList(input_protocolName);
            }

            try(TlsContext tlsContext = new TlsContext(tlsContextOptions);
                MqttClient client = new MqttClient(tlsContext);
                MqttConnectionConfig config = new MqttConnectionConfig()) {

                config.setMqttClient(client);
                config.setClientId(input_clientId);
                config.setConnectionCallbacks(callbacks);
                config.setCleanSession(true);
                config.setEndpoint(input_endpoint);
                config.setPort(port);

                if (input_userName != null && input_userName.length() > 0) {
                    config.setLogin(input_userName, input_password);
                }

                try (MqttClientConnection connection = new MqttClientConnection(config)) {

                    /**
                     * Connect and disconnect
                     */
                    CompletableFuture<Boolean> connected = connection.connect();
                    try {
                        boolean sessionPresent = connected.get();
                        System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");
                    } catch (Exception ex) {
                        throw new RuntimeException("Exception occurred during connect", ex);
                    }
                    System.out.println("Disconnecting...");
                    CompletableFuture<Void> disconnected = connection.disconnect();
                    disconnected.get();
                    System.out.println("Disconnected.");

                    // Close the connection now that we are completely done with it.
                    connection.close();
                }
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception encountered: " + ex.toString());
        }

        CrtResource.waitForNoResources();
        System.out.println("Complete!");
    }
}
