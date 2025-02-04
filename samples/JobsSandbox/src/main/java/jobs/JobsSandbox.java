/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package jobs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.iot.*;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.packets.ConnectPacket;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;
import software.amazon.awssdk.iot.iotjobs.IotJobsV2Client;
import software.amazon.awssdk.iot.iotjobs.model.*;
import software.amazon.awssdk.iot.V2ClientStreamOptions;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.CreateThingRequest;
import software.amazon.awssdk.services.iot.model.CreateThingResponse;
import software.amazon.awssdk.services.iot.model.DescribeThingRequest;
import software.amazon.awssdk.services.iot.model.DescribeThingResponse;
import software.amazon.awssdk.services.sts.StsClient;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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


public class JobsSandbox {

    static class ApplicationContext implements AutoCloseable {
        public final Gson gson = createGson();
        public final CompletableFuture<Void> connectedFuture = new CompletableFuture<>();
        public final CompletableFuture<Void> stoppedFuture = new CompletableFuture<>();

        private StreamingOperation jobExecutionsChangedStream;
        private StreamingOperation nextJobExecutionChangedStream;

        public String thingName;

        public IotClient controlPlaneClient;
        public Mqtt5Client protocolClient;
        public IotJobsV2Client jobsClient;

        public void close() {
            if (this.jobExecutionsChangedStream != null) {
                this.jobExecutionsChangedStream.close();
            }

            if (this.nextJobExecutionChangedStream != null) {
                this.nextJobExecutionChangedStream.close();
            }

            if (this.jobsClient != null) {
                this.jobsClient.close();
            }

            if (this.protocolClient != null) {
                this.protocolClient.close();
            }
        }

        private static Gson createGson() {
            GsonBuilder builder = new GsonBuilder();
            builder.disableHtmlEscaping();
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
        cliOptions.addOption(Option.builder("r").longOpt("region").desc("AWS Region the AWS IoT endpoint is using").hasArg().required().build());
        cliOptions.addOption(Option.builder("h").longOpt("help").desc("Prints command line help").build());

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(cliOptions, args);

        if (commandLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("JobsSandbox", cliOptions);
            return null;
        }

        String endpoint = commandLine.getOptionValue("endpoint");
        String region = null;
        if (commandLine.hasOption("region")) {
            region = commandLine.getOptionValue("region");
        }

        if (region == null) {
            System.out.println("No region supplied on the command line, attempting to extract from standard IoT Core endpoint pattern");

            //  Try the standard account-specific endpoint
            Pattern standardRegionPattern = Pattern.compile(".+-ats\\.iot.*\\.(.+)\\.amazonaws\\.com");
            Matcher standardMatch = standardRegionPattern.matcher(endpoint);
            region = standardMatch.group(1);
        }

        if (region == null) {
            System.out.println("No region supplied on the command line, attempting to extract from jobs IoT Core endpoint pattern");

            // Try the jobs specific endpoint
            // account-specific-prefix.jobs.iot.aws-region.amazonaws.com
            Pattern jobsRegionPattern = Pattern.compile(".*\\.jobs\\.iot.*\\.(.+)\\.amazonaws\\.com");
            Matcher standardMatch = jobsRegionPattern.matcher(endpoint);
            region = standardMatch.group(1);
        }

        if (region == null) {
            System.out.println("ERROR: could not determine region from endpoint");
            return null;
        }

        System.out.println(String.format("Using region '%s'", region));

        // needed to pull in STS to the class path so that profile-based STS lookups work correctly
        StsClient stsClient = StsClient.builder()
                .region(Region.of(region))
                .build();

        context.controlPlaneClient = IotClient.builder()
                .region(Region.of(region))
                .build();

        context.thingName = commandLine.getOptionValue("thing");

        try {
            DescribeThingResponse describeResponse = context.controlPlaneClient.describeThing(DescribeThingRequest.builder().thingName(context.thingName).build());
        } catch (Exception ex) {
            System.out.println(String.format("Thing '%s' does not exist.  Creating it...", context.thingName));
            CreateThingResponse createResponse = context.controlPlaneClient.createThing(CreateThingRequest.builder().thingName(context.thingName).build());
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

        context.jobsClient = IotJobsV2Client.newFromMqtt5(context.protocolClient, rrClientOptions);

        // JobExecutionsChanged streaming operation
        JobExecutionsChangedSubscriptionRequest jobExecutionsChangedRequest = new JobExecutionsChangedSubscriptionRequest();
        jobExecutionsChangedRequest.thingName = context.thingName;

        V2ClientStreamOptions<JobExecutionsChangedEvent> jobExecutionsChangedOptions = V2ClientStreamOptions.<JobExecutionsChangedEvent>builder()
            .withStreamEventHandler((event) -> {
                System.out.println("JobExecutionsChanged event: \n  " + context.gson.toJson(event));
            })
            .build();

        context.jobExecutionsChangedStream = context.jobsClient.createJobExecutionsChangedStream(jobExecutionsChangedRequest, jobExecutionsChangedOptions);
        context.jobExecutionsChangedStream.open();

        // NextJobExecutionChanged streaming operation
        NextJobExecutionChangedSubscriptionRequest nextJobExecutionChangedRequest = new NextJobExecutionChangedSubscriptionRequest();
        nextJobExecutionChangedRequest.thingName = context.thingName;

        V2ClientStreamOptions<NextJobExecutionChangedEvent> nextJobExecutionChangedOptions = V2ClientStreamOptions.<NextJobExecutionChangedEvent>builder()
            .withStreamEventHandler((event) -> {
                System.out.println("NextJobExecutionChanged event: \n  " + context.gson.toJson(event));
            })
            .build();

        context.nextJobExecutionChangedStream = context.jobsClient.createNextJobExecutionChangedStream(nextJobExecutionChangedRequest, nextJobExecutionChangedOptions);
        context.nextJobExecutionChangedStream.open();

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
        System.out.println("Usage");
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
