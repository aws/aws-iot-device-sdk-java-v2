/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.iot.*;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.packets.ConnectPacket;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;
import software.amazon.awssdk.iot.iotcommands.IotCommandsV2Client;
import software.amazon.awssdk.iot.iotcommands.model.*;
import software.amazon.awssdk.iot.V2ClientStreamOptions;

import software.amazon.awssdk.iot.iotcommands.model.CommandExecutionStatus;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.*;
import software.amazon.awssdk.services.sts.StsClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;


public class CommandsSandbox {

    /**
     * Auxiliary class to store a received command execution.
     * It's used for the updating command execution status.
     */
    static class CommandExecutionContext {
        DeviceType deviceType;
        String deviceId;
    }

    /**
     * Auxiliary class to store an opened streaming operation data.
     * It's used to report on the list-streams command.
     */
    static class StreamingOperationContext {
        StreamingOperation operation;
        DeviceType deviceType;
        String deviceId;
        String payloadType;
    }

    static class ApplicationContext implements AutoCloseable {
        public final Gson gson = createGson();
        public final CompletableFuture<Void> connectedFuture = new CompletableFuture<>();
        public final CompletableFuture<Void> stoppedFuture = new CompletableFuture<>();

        int nextStreamId;
        private Map<Integer, StreamingOperationContext> commandExecutionsStreams;

        private Map<String, CommandExecutionContext> activeCommandExecutions;

        public String thingName;
        public String thingArn;
        public String mqttClientId;
        public String mqttClientArn;

        public StsClient stsClient;
        public IotClient controlPlaneClient;
        public Mqtt5Client protocolClient;
        public IotCommandsV2Client commandsClient;

        public void close() {
            if (this.commandExecutionsStreams != null) {
                this.commandExecutionsStreams.values().forEach(context -> context.operation.close());
            }

            if (this.commandsClient != null) {
                this.commandsClient.close();
            }

            if (this.protocolClient != null) {
                this.protocolClient.close();
            }

            if (this.controlPlaneClient != null) {
                this.controlPlaneClient.close();
            }

            if (this.stsClient != null) {
                this.stsClient.close();
            }
        }

        public void setMqttClientArn(String region) {
            GetCallerIdentityResponse response = stsClient.getCallerIdentity(GetCallerIdentityRequest.builder().build());
            String accountId = response.account();
            mqttClientArn = String.format("arn:aws:iot:%s:%s:client/%s", region, accountId, mqttClientId);
        }

        private static Gson createGson() {
            GsonBuilder builder = new GsonBuilder();
            builder.disableHtmlEscaping();
            return builder.create();
        }
    }

    private static ApplicationContext buildSampleContext(String[] args) throws Exception {
        ApplicationContext context = new ApplicationContext();

        Options cliOptions = new Options();

        cliOptions.addOption(Option.builder("c").longOpt("cert").desc("file path to an X509 certificate to use when establishing mTLS context").hasArg().required().build());
        cliOptions.addOption(Option.builder("k").longOpt("key").desc("file path to an X509 private key to use when establishing mTLS context").hasArg().required().build());
        cliOptions.addOption(Option.builder("t").longOpt("thing").desc("name of the AWS IoT thing resource to interact with").hasArg().required().build());
        cliOptions.addOption(Option.builder("i").longOpt("client-id").desc("ID of the MQTT client to interact with").hasArg().build());
        cliOptions.addOption(Option.builder("e").longOpt("endpoint").desc("AWS IoT endpoint to connect to").hasArg().required().build());
        cliOptions.addOption(Option.builder("r").longOpt("region").desc("AWS Region the AWS IoT endpoint is using").hasArg().build());
        cliOptions.addOption(Option.builder("h").longOpt("help").desc("Prints command line help").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(cliOptions, args);

        if (commandLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("CommandsSandbox", cliOptions);
            return null;
        }

        String endpoint = commandLine.getOptionValue("endpoint");
        String region = null;
        if (commandLine.hasOption("region")) {
            region = commandLine.getOptionValue("region");
        }

        if (region == null) {
            System.out.println("No region supplied on the command line, attempting to extract from endpoint");

            Pattern standardRegionPattern = Pattern.compile(".*\\.iot.*\\.([^.]+)\\.amazonaws\\.com");
            Matcher standardMatch = standardRegionPattern.matcher(endpoint);
            standardMatch.find();
            region = standardMatch.group(1);
        }

        if (region == null) {
            System.out.println("ERROR: could not determine region from endpoint");
            return null;
        }

        System.out.println(String.format("Using region '%s'", region));

        // needed to pull in STS to the class path so that profile-based STS lookups work correctly
        context.stsClient = StsClient.builder()
                .region(Region.of(region))
                .build();

        context.controlPlaneClient = IotClient.builder()
                .region(Region.of(region))
                .build();

        context.thingName = commandLine.getOptionValue("thing");
        context.mqttClientId = commandLine.getOptionValue("client-id", String.format("test-%s", UUID.randomUUID()));
        context.setMqttClientArn(region);

        try {
            context.controlPlaneClient.describeThing(DescribeThingRequest.builder().thingName(context.thingName).build());
        } catch (ResourceNotFoundException ex) {
            System.out.println(String.format("Thing '%s' does not exist.  Creating it...", context.thingName));
            context.controlPlaneClient.createThing(CreateThingRequest.builder().thingName(context.thingName).build());
        }

        DescribeThingResponse describeResponse = context.controlPlaneClient.describeThing(DescribeThingRequest.builder().thingName(context.thingName).build());
        context.thingArn = describeResponse.thingArn();

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
            connectProperties.withClientId(context.mqttClientId);
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

        context.nextStreamId = 1;
        context.commandExecutionsStreams = new HashMap<Integer, StreamingOperationContext>();

        context.activeCommandExecutions = new ConcurrentHashMap<String, CommandExecutionContext>();

        context.commandsClient = IotCommandsV2Client.newFromMqtt5(context.protocolClient, rrClientOptions);

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

    private static void printCommandHelp() {
        System.out.println("Usage\n");
        System.out.println("  IoT control plane commands:");
        System.out.println("    create-command <command-id> <content-type> <command-document>");
        System.out.println("                     create a new command with the specified command ID and document;");
        System.out.println("                     <command-id> is a unique AWS IoT Command identifier");
        System.out.println("                     <content-type> a content type of the payload");
        System.out.println("                         JSON and CBOR are handled specifically, see README for more information");
        System.out.println("    delete-command <command-id>");
        System.out.println("                     deletes a command with the specified command ID\n");
        System.out.println("  MQTT Command service commands:");
        System.out.println("    open-thing-stream <payload-format>");
        System.out.println("                     subscribe to a stream of command executions with a specified payload format");
        System.out.println("                     targeting the IoT Thing set on the application startup");
        System.out.println("                     <payload-format> is a string, with the following special values:");
        System.out.println("                         json - subscribe to commands with JSON payload");
        System.out.println("                         cbor - subscribe to commands with CBOR payload");
        System.out.println("                         for any other value, subscribe to a generic topic");
        System.out.println("    open-client-stream <payload-format>");
        System.out.println("                     subscribe to a stream of command executions with a specified payload format");
        System.out.println("                     targeting the MQTT client ID set on the application startup");
        System.out.println("                     <payload-format> is a string, with the following special values:");
        System.out.println("                         json - subscribe to commands with JSON payload");
        System.out.println("                         cbor - subscribe to commands with CBOR payload");
        System.out.println("                         for any other value, subscribe to a generic topic");
        System.out.println("    update-command-execution <executionId> <status> [<reason-code>] [<reason-description>]");
        System.out.println("                     updates a command execution with a new status");
        System.out.println("                     <status> can be one of the following:");
        System.out.println("                         IN_PROGRESS, SUCCEEDED, REJECTED, FAILED, TIMED_OUT");
        System.out.println("                     <reason-code> and <reason-description> may be optionally provided for");
        System.out.println("                         the REJECTED, FAILED, or TIMED_OUT statuses\n");
        System.out.println("  Miscellaneous commands:");
        System.out.println("    list-streams      list all open streaming operations");
        System.out.println("    close-stream <streamID>");
        System.out.println("                      close a specified stream;");
        System.out.println("                      <stream-id> is internal ID that can be found with 'list-streams' command");
        System.out.println("    quit              exit the application\n");
    }

    private static void handleCreateCommand(ApplicationContext context, String arguments) {
        String[] argumentSplit = arguments.trim().split(" ", 3);
        if (argumentSplit.length < 2) {
            printCommandHelp();
            return;
        }

        try {
            String commandId = argumentSplit[0];
            SdkBytes commandDocumentBytes = SdkBytes.fromUtf8String(argumentSplit[2]);
            CommandPayload commandPayload = CommandPayload.builder()
                    .contentType(argumentSplit[1])
                    .content(commandDocumentBytes)
                    .build();

            CreateCommandResponse response = context.controlPlaneClient.createCommand(CreateCommandRequest.builder()
                    .commandId(commandId)
                    .payload(commandPayload)
                    .build());
            System.out.printf("CreateCommandResponse: \n  %s\n%n", response.toString());
        } catch (Exception ex) {
            handleOperationException("create-command", ex, context);
        }
    }

    private static void handleDeleteCommand(ApplicationContext context, String arguments) {
        String commandId = arguments.trim();

        try {
            DeleteCommandResponse response = context.controlPlaneClient.deleteCommand(DeleteCommandRequest.builder().commandId(commandId).build());
            System.out.println(String.format("DeleteCommandResponse: \n  %s\n", response.toString()));
        } catch (Exception ex) {
            handleOperationException("delete-command", ex, context);
        }
    }

    private static void handleUpdateCommandExecution(ApplicationContext context, String arguments) {
        String[] argumentSplit = arguments.trim().split(" ", 2);
        if (argumentSplit.length < 2) {
            printCommandHelp();
            return;
        }

        String executionId = argumentSplit[0];
        String statusStr = argumentSplit[1];

        try {
            CommandExecutionContext commandExecutionContext = context.activeCommandExecutions.get(executionId);
            UpdateCommandExecutionRequest request = new UpdateCommandExecutionRequest();
            request.executionId = executionId;
            request.deviceType = commandExecutionContext.deviceType;
            request.deviceId = commandExecutionContext.deviceId;
            request.status = CommandExecutionStatus.valueOf(statusStr);

            UpdateCommandExecutionResponse response = context.commandsClient.updateCommandExecution(request).get();
            System.out.printf("UpdateCommandExecutionResponse: \n  %s\n\n", context.gson.toJson(response));
        } catch (Exception ex) {
            handleOperationException("update-command-execution", ex, context);
        }
    }

    private static void handleOpenStream(ApplicationContext context, DeviceType deviceType, String payloadFormat) {
        try {
            String deviceId;
            if (deviceType == DeviceType.THING) {
                deviceId = context.thingName;
            } else {
                deviceId = context.mqttClientId;
            }

            V2ClientStreamOptions<CommandExecutionEvent> options =
                    V2ClientStreamOptions.<CommandExecutionEvent>builder().withStreamEventHandler(event -> {
                        System.out.printf("Received command execution event ID '%s' with payload type '%s'\n", event.executionId, event.contentType);
                        CommandExecutionContext commandExecutionContext = new CommandExecutionContext();
                        commandExecutionContext.deviceId = deviceId;
                        commandExecutionContext.deviceType = deviceType;
                        context.activeCommandExecutions.put(event.executionId, commandExecutionContext);
                    }).build();

            CommandExecutionsSubscriptionRequest request = new CommandExecutionsSubscriptionRequest();

            StreamingOperationContext streamingOperationContext = new StreamingOperationContext();
            streamingOperationContext.deviceType = deviceType;
            streamingOperationContext.deviceId = deviceId;
            streamingOperationContext.payloadType = payloadFormat;

            request.deviceType = deviceType;
            request.deviceId = deviceId;

            switch (payloadFormat) {
                case "json":
                    streamingOperationContext.operation = context.commandsClient.createCommandExecutionsJsonPayloadStream(request, options);
                    break;
                case "cbor":
                    streamingOperationContext.operation = context.commandsClient.createCommandExecutionsCborPayloadStream(request, options);
                    break;
                default:
                    streamingOperationContext.operation = context.commandsClient.createCommandExecutionsGenericPayloadStream(request, options);
                    break;
            }

            streamingOperationContext.operation.open();
            System.out.printf("Opened streaming operation with ID %d\n", context.nextStreamId);

            context.commandExecutionsStreams.put(context.nextStreamId, streamingOperationContext);
            context.nextStreamId++;

        } catch (Exception ex) {
            handleOperationException("open-command-stream", ex, context);
        }
    }

    static void handleListStreams(ApplicationContext context) {
        System.out.println("Streams:");
        context.commandExecutionsStreams.entrySet().forEach(entry -> {
            System.out.printf("  %d: device type '%s', device ID '%s', payload type '%s'\n",
                    entry.getKey(), entry.getValue().deviceType, entry.getValue().deviceId, entry.getValue().payloadType);
        });
    }

    static void handleCloseStream(ApplicationContext context, String arguments) {
        try {
            int streamId = Integer.parseInt(arguments.trim());
            StreamingOperationContext streamingOperationContext = context.commandExecutionsStreams.get(streamId);
            if (streamingOperationContext != null) {
                streamingOperationContext.operation.close();
                context.commandExecutionsStreams.remove(streamId);
            }
        } catch (Exception ex) {
            handleOperationException("close-stream", ex, context);
        }
    }

    private static boolean handleCommand(String commandLine, ApplicationContext context) {
        String[] commandLineSplit = commandLine.trim().split(" ", 2);
        if (commandLineSplit.length == 0) {
            return false;
        }

        String command = commandLineSplit[0];
        switch (command) {
            case "create-command":
                if (commandLineSplit.length == 2) {
                    handleCreateCommand(context, commandLineSplit[1]);
                }
                return false;
            case "delete-command":
                if (commandLineSplit.length == 2) {
                    handleDeleteCommand(context, commandLineSplit[1]);
                }
                return false;
            case "open-thing-stream":
                if (commandLineSplit.length == 2) {
                    handleOpenStream(context, DeviceType.THING, commandLineSplit[1]);
                }
                return false;

            case "open-client-stream":
                if (commandLineSplit.length == 2) {
                    handleOpenStream(context, DeviceType.CLIENT, commandLineSplit[1]);
                }
                return false;

            case "update-command-execution":
                if (commandLineSplit.length == 2) {
                    handleUpdateCommandExecution(context, commandLineSplit[1]);
                }
                return false;

            case "list-streams":
                handleListStreams(context);
                return false;

            case "close-stream":
                if (commandLineSplit.length == 2) {
                    handleCloseStream(context, commandLineSplit[1]);
                }
                return false;

            case "quit":
                return true;

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
                System.out.print("Enter command: > ");
                if (scanner.hasNextLine()) {
                    String userInput = scanner.nextLine();
                    done = handleCommand(userInput, context);
                }
                else {
                    done = true;
                }
            }
            scanner.close();

            context.protocolClient.stop(null);
            context.stoppedFuture.get(60, TimeUnit.SECONDS);

            System.out.println("Exiting application...");
        } catch (Exception ex) {
            System.out.println("Exception encountered: " + ex.toString());
            System.exit(1);
        }

        CrtResource.waitForNoResources();
    }
}
