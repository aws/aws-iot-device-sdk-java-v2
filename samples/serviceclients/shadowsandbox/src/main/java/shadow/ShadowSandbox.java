/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package shadow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.iot.*;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.packets.ConnectPacket;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;
import software.amazon.awssdk.iot.iotshadow.IotShadowV2Client;
import software.amazon.awssdk.iot.iotshadow.model.*;
import software.amazon.awssdk.iot.ShadowStateFactory;
import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.V2ClientStreamOptions;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;


public class ShadowSandbox {

    static class ApplicationContext implements AutoCloseable {
        public final Gson gson = createGson();
        public final CompletableFuture<Void> connectedFuture = new CompletableFuture<>();
        public final CompletableFuture<Void> stoppedFuture = new CompletableFuture<>();

        private StreamingOperation shadowUpdatedStream;
        private StreamingOperation shadowDeltaUpdatedStream;

        public String thingName;

        public Mqtt5Client protocolClient;
        public IotShadowV2Client client;

        public void close() {
            if (this.shadowUpdatedStream != null) {
                this.shadowUpdatedStream.close();
            }

            if (this.shadowDeltaUpdatedStream != null) {
                this.shadowDeltaUpdatedStream.close();
            }

            if (this.client != null) {
                this.client.close();
            }

            if (this.protocolClient != null) {
                this.protocolClient.close();
            }
        }

        private static Gson createGson() {
            GsonBuilder builder = new GsonBuilder();
            builder.disableHtmlEscaping();
            builder.registerTypeAdapter(Timestamp.class, new Timestamp.Serializer());
            builder.registerTypeAdapter(Timestamp.class, new Timestamp.Deserializer());
            builder.registerTypeAdapterFactory(new ShadowStateFactory());
            return builder.create();
        }
    }

    private static ApplicationContext buildSampleContext(String [] args) throws Exception {
        ApplicationContext context = new ApplicationContext();

        Options cliOptions = new Options();

        cliOptions.addOption(Option.builder("c").longOpt("cert").desc("file path to an X509 certificate to use when establishing mTLS context").hasArg().required().build());
        cliOptions.addOption(Option.builder("k").longOpt("key").desc("file path to an X509 private key to use when establishing mTLS context").hasArg().required().build());
        cliOptions.addOption(Option.builder("t").longOpt("thing").desc("name of the AWS IoT thing resource to interact with").hasArg().required().build());
        cliOptions.addOption(Option.builder("e").longOpt("endpoint").desc("AWS IoT endpoint to connect to").hasArg().required().build());
        cliOptions.addOption(Option.builder("h").longOpt("help").desc("Prints command line help").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(cliOptions, args);

        if (commandLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ShadowSandbox", cliOptions);
            return null;
        }

        context.thingName = commandLine.getOptionValue("thing");
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

        context.client = IotShadowV2Client.newFromMqtt5(context.protocolClient, rrClientOptions);

        // ShadowUpdated streaming operation
        ShadowUpdatedSubscriptionRequest shadowUpdatedRequest = new ShadowUpdatedSubscriptionRequest();
        shadowUpdatedRequest.thingName = context.thingName;

        V2ClientStreamOptions<ShadowUpdatedEvent> shadowUpdatedOptions = V2ClientStreamOptions.<ShadowUpdatedEvent>builder()
            .withStreamEventHandler((event) -> {
                System.out.println("ShadowUpdated event: \n  " + context.gson.toJson(event));
            })
            .build();

        context.shadowUpdatedStream = context.client.createShadowUpdatedStream(shadowUpdatedRequest, shadowUpdatedOptions);
        context.shadowUpdatedStream.open();

        // ShadowDeltaUpdated streaming operation
        ShadowDeltaUpdatedSubscriptionRequest shadowDeltaUpdatedRequest = new ShadowDeltaUpdatedSubscriptionRequest();
        shadowDeltaUpdatedRequest.thingName = context.thingName;

        V2ClientStreamOptions<ShadowDeltaUpdatedEvent> shadowDeltaUpdatedOptions = V2ClientStreamOptions.<ShadowDeltaUpdatedEvent>builder()
            .withStreamEventHandler((event) -> {
                System.out.println("ShadowDeltaUpdated event: \n  " + context.gson.toJson(event));
            })
            .build();

        context.shadowDeltaUpdatedStream = context.client.createShadowDeltaUpdatedStream(shadowDeltaUpdatedRequest, shadowDeltaUpdatedOptions);
        context.shadowDeltaUpdatedStream.open();

        return context;
    }

    private static void handleOperationException(String operationName, Exception ex, ApplicationContext context) {
        if (ex instanceof ExecutionException) {
            System.out.printf("%s ExecutionException!\n", operationName);
            Throwable source = ex.getCause();
            if (source != null) {
                System.out.printf("  %s source exception: %s\n", operationName, source.getMessage());
                if (source instanceof V2ErrorResponseException) {
                    V2ErrorResponseException v2exception = (V2ErrorResponseException) source;
                    if (v2exception.getModeledError() != null) {
                        System.out.printf("  %s Modeled error: %s\n", operationName, context.gson.toJson(v2exception.getModeledError()));
                    }
                }
            }
        } else {
            System.out.printf("%s Exception: %s\n", operationName, ex.getMessage());
        }
    }

    private static void handleGet(ApplicationContext context) {
        GetShadowRequest request = new GetShadowRequest();
        request.thingName = context.thingName;

        try {
            GetShadowResponse response = context.client.getShadow(request).get();
            System.out.println("GetShadowResponse: \n  " + context.gson.toJson(response));
        } catch (Exception ex) {
            handleOperationException("Get", ex, context);
        }
    }

    private static void handleDelete(ApplicationContext context) {
        DeleteShadowRequest request = new DeleteShadowRequest();
        request.thingName = context.thingName;

        try {
            DeleteShadowResponse response = context.client.deleteShadow(request).get();
            System.out.println("DeleteShadowResponse: \n  " + context.gson.toJson(response));
        } catch (Exception ex) {
            handleOperationException("Delete", ex, context);
        }
    }

    private static void handleUpdate(ApplicationContext context, ShadowState newState) {
        UpdateShadowRequest request = new UpdateShadowRequest();
        request.thingName = context.thingName;
        request.state = newState;

        try {
            UpdateShadowResponse response = context.client.updateShadow(request).get();
            System.out.println("UpdateShadowResponse: \n  " + context.gson.toJson(response));
        } catch (Exception ex) {
            handleOperationException("Update", ex, context);
        }
    }

    private static void handleUpdateDesired(ApplicationContext context, String value) {
        ShadowState state = new ShadowState();
        state.desiredIsNullable = true;
        if (value.equals("null")) {
            state.desired = null;
        } else {
            state.desired = context.gson.fromJson(value, HashMap.class);
        }

        handleUpdate(context, state);
    }

    private static void handleUpdateReported(ApplicationContext context, String value) {
        ShadowState state = new ShadowState();
        state.reportedIsNullable = true;
        if (value.equals("null")) {
            state.reported = null;
        } else {
            state.reported = context.gson.fromJson(value, HashMap.class);
        }

        handleUpdate(context, state);
    }

    private static void printCommandHelp() {
        System.out.println("Usage");
        System.out.println("  get -- gets the thing's current shadow document");
        System.out.println("  delete -- deletes the thing;s shadow document");
        System.out.println("  update-desired <Desired state JSON> -- updates the desired component of the thing's shadow document");
        System.out.println("  update-reported <Reported state JSON> -- updates the reported component of the thing's shadow document");
        System.out.println("  quit -- exit the application");
    }

    private static boolean handleCommand(String commandLine, ApplicationContext context) {
        String[] commandLineSplit = commandLine.trim().split(" ", 2);
        if (commandLineSplit.length == 0) {
            return false;
        }

        String command = commandLineSplit[0];
        switch (command) {
            case "quit":
                return true;

            case "get":
                handleGet(context);
                return false;

            case "delete":
                handleDelete(context);
                return false;

            case "update-desired":
                if (commandLineSplit.length == 2) {
                    handleUpdateDesired(context, commandLineSplit[1]);
                }
                return false;

            case "update-reported":
                if (commandLineSplit.length == 2) {
                    handleUpdateReported(context, commandLineSplit[1]);
                }
                return false;

            default:
                break;
        }

        printCommandHelp();
        return false;
    }

    public static void main(String[] args) {
        try (ApplicationContext context = buildSampleContext(args)) {
            if (context == null) {
                return;
            }

            boolean done = false;
            Scanner scanner = new Scanner(System.in);
            while (!done) {
                String userInput = scanner.nextLine();
                done = handleCommand(userInput, context);
            }
            scanner.close();

            context.protocolClient.stop(null);
            context.stoppedFuture.get(60, TimeUnit.SECONDS);
        } catch (Exception ex) {
            System.out.println("Exception encountered: " + ex.toString());
            System.exit(1);
        }

        CrtResource.waitForNoResources();
    }
}
