/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package identity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.iot.*;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.packets.ConnectPacket;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;
import software.amazon.awssdk.iot.iotidentity.IotIdentityV2Client;
import software.amazon.awssdk.iot.iotidentity.model.*;


import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;


public class BasicProvisioning {

    static class ApplicationContext implements AutoCloseable {
        public final CompletableFuture<Void> connectedFuture = new CompletableFuture<>();
        public final CompletableFuture<Void> stoppedFuture = new CompletableFuture<>();

        public Mqtt5Client protocolClient;
        public IotIdentityV2Client identityClient;

        public String templateName;
        public String templateParameters;

        public void close() {
            if (this.identityClient != null) {
                this.identityClient.close();
            }

            if (this.protocolClient != null) {
                this.protocolClient.close();
            }
        }
    }

    private static ApplicationContext buildSampleContext(String [] args) throws Exception {
        ApplicationContext context = new ApplicationContext();

        Options cliOptions = new Options();

        cliOptions.addOption(Option.builder("c").longOpt("cert").desc("file path to an X509 certificate to use when establishing mTLS context").hasArg().required().build());
        cliOptions.addOption(Option.builder("k").longOpt("key").desc("file path to an X509 private key to use when establishing mTLS context").hasArg().required().build());
        cliOptions.addOption(Option.builder("t").longOpt("template").desc("name of the provisioning template resource to interact with").hasArg().required().build());
        cliOptions.addOption(Option.builder("p").longOpt("params").desc("Json document with values for all the provisioning template's parameters").hasArg().build());
        cliOptions.addOption(Option.builder("e").longOpt("endpoint").desc("AWS IoT endpoint to connect to").hasArg().required().build());
        cliOptions.addOption(Option.builder("h").longOpt("help").desc("Prints command line help").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(cliOptions, args);

        if (commandLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("BasicProvisioning", cliOptions);
            return null;
        }

        context.templateName = commandLine.getOptionValue("template");
        if (commandLine.hasOption("params")) {
            context.templateParameters = commandLine.getOptionValue("params");
        }

        Mqtt5ClientOptions.LifecycleEvents lifecycleEvents = new Mqtt5ClientOptions.LifecycleEvents() {
            @Override
            public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn onAttemptingConnectReturn) {
                System.out.println("Attempting connection...");
            }

            @Override
            public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn onConnectionSuccessReturn) {
                System.out.println("Connection success");
                context.connectedFuture.complete(null);
            }

            @Override
            public void onConnectionFailure(Mqtt5Client client, OnConnectionFailureReturn onConnectionFailureReturn) {
                String errorString = CRT.awsErrorString(onConnectionFailureReturn.getErrorCode());
                System.out.println("Connection failed with error: " + errorString);
                context.connectedFuture.completeExceptionally(new Exception("Could not connect: " + errorString));
            }

            @Override
            public void onDisconnection(Mqtt5Client client, OnDisconnectionReturn onDisconnectionReturn) {
            }

            @Override
            public void onStopped(Mqtt5Client client, OnStoppedReturn onStoppedReturn) {
                context.stoppedFuture.complete(null);
            }
        };

        try (AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(
                commandLine.getOptionValue("endpoint"), commandLine.getOptionValue("cert"), commandLine.getOptionValue("key"))) {
            builder.withLifeCycleEvents(lifecycleEvents);

            ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
            connectProperties.withClientId(String.format("test-%s", UUID.randomUUID()));
            builder.withConnectProperties(connectProperties);

            context.protocolClient = builder.build();
        }

        context.protocolClient.start();
        context.connectedFuture.get();

        MqttRequestResponseClientOptions rrClientOptions = MqttRequestResponseClientOptions.builder()
                .withMaxRequestResponseSubscriptions(5)
                .withMaxStreamingSubscriptions(2)
                .withOperationTimeoutSeconds(30)
                .build();

        context.identityClient = IotIdentityV2Client.newFromMqtt5(context.protocolClient, rrClientOptions);

        return context;
    }

    private static void handleException(Exception ex, Gson gson) {
        if (ex instanceof ExecutionException) {
            System.out.printf("ExecutionException!\n");
            Throwable source = ex.getCause();
            if (source != null) {
                System.out.printf("  source exception: %s\n", source.getMessage());
                if (source instanceof V2ErrorResponseException) {
                    V2ErrorResponseException v2exception = (V2ErrorResponseException) source;
                    if (v2exception.getModeledError() != null) {
                        System.out.printf("  Modeled error: %s\n", gson.toJson(v2exception.getModeledError()));
                    }
                }
            }
        } else {
            System.out.printf("Exception: %s\n", ex.getMessage());
        }
    }

    public static void main(String[] args) {
        GsonBuilder builder = new GsonBuilder();
        builder.disableHtmlEscaping();
        // otherwise integers get turned to doubles which breaks thing name construction
        builder.setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE);
        Gson gson = builder.create();

        try (ApplicationContext context = buildSampleContext(args)) {
            if (context == null) {
                return;
            }

            CreateKeysAndCertificateRequest createRequest = new CreateKeysAndCertificateRequest();
            CreateKeysAndCertificateResponse createResponse = context.identityClient.createKeysAndCertificate(createRequest).get();
            System.out.println("CreateKeysAndCertificateResponse: \n  " + gson.toJson(createResponse));

            RegisterThingRequest registerRequest = new RegisterThingRequest();
            registerRequest.templateName = context.templateName;
            registerRequest.certificateOwnershipToken = createResponse.certificateOwnershipToken;
            registerRequest.parameters = gson.fromJson(context.templateParameters, HashMap.class);

            RegisterThingResponse registerResponse = context.identityClient.registerThing(registerRequest).get();
            System.out.println("RegisterThingResponse: \n  " + gson.toJson(registerResponse));

            context.protocolClient.stop(null);
            context.stoppedFuture.get(60, TimeUnit.SECONDS);
        } catch (Exception ex) {
            handleException(ex, gson);
            System.exit(1);
        }

        CrtResource.waitForNoResources();
    }
}
