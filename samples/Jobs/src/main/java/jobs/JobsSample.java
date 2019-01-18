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
 */

package jobs;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.TlsContext;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.mqtt.MqttClient;
import software.amazon.awssdk.crt.mqtt.MqttConnection;
import software.amazon.awssdk.crt.mqtt.MqttConnectionEvents;
import software.amazon.awssdk.iot.iotjobs.IotJobsClient;
import software.amazon.awssdk.iot.iotjobs.model.GetPendingJobExecutionsRequest;
import software.amazon.awssdk.iot.iotjobs.model.GetPendingJobExecutionsSubscriptionRequest;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionSummary;
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class JobsSample {
    static String clientId = "samples-client-id";
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String endpoint;
    static boolean showHelp = false;
    static int port = 8883;

    static void printUsage() {
        System.out.println(
                "Usage:\n"+
                "  --help        This message\n"+
                "  --clientId    Client ID to use when connecting (optional)\n"+
                "  -e|--endpoint AWS IoT service endpoint hostname\n"+
                "  -p|--port     Port to connect to on the endpoint\n"+
                "  -r|--rootca   Path to the root certificate\n"+
                "  -c|--cert     Path to the IoT thing certificate\n"+
                "  -k|--key      Path to the IoT thing public key"
        );
    }

    static void parseCommandLine(String[] args) {
        for (int idx = 0; idx < args.length; ++idx) {
            switch (args[idx]) {
                case "--help":
                    showHelp = true;
                    break;
                case "--clientId":
                    if (idx + 1 < args.length) {
                        clientId = args[++idx];
                    }
                    break;
                case "-e":
                case "--endpoint":
                    if (idx + 1 < args.length) {
                        endpoint = args[++idx];
                    }
                    break;
                case "-p":
                case "--port":
                    if (idx + 1 < args.length) {
                        port = Integer.parseInt(args[++idx]);
                    }
                    break;
                case "-r":
                case "--rootca":
                    if (idx + 1 < args.length) {
                        rootCaPath = args[++idx];
                    }
                    break;
                case "-c":
                case "--cert":
                    if (idx + 1 < args.length) {
                        certPath = args[++idx];
                    }
                    break;
                case "-k":
                case "--key":
                    if (idx + 1 < args.length) {
                        keyPath = args[++idx];
                    }
                    break;
                default:
                    System.out.println("Unrecognized argument: " + args[idx]);
            }
        }
    }

    static void onRejectedError(RejectedError error) {
        System.out.println("Request rejected: " + error.code.toString() + ": " + error.message);
    }

    public static void main(String[] args) {
        parseCommandLine(args);
        if (showHelp || endpoint == null || rootCaPath == null || certPath == null || keyPath == null) {
            printUsage();
            return;
        }

        try {
            EventLoopGroup eventLoopGroup = new EventLoopGroup(1);
            ClientBootstrap clientBootstrap = new ClientBootstrap(eventLoopGroup);
            TlsContextOptions tlsContextOptions = TlsContextOptions.createWithMTLS(certPath, keyPath);
            tlsContextOptions.overrideDefaultTrustStore(null, rootCaPath);
            TlsContext tlsContext = new TlsContext(tlsContextOptions);
            MqttClient client = new MqttClient(clientBootstrap, tlsContext);

            MqttConnection connection = new MqttConnection(client, new MqttConnectionEvents() {
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
            });
            IotJobsClient jobs = new IotJobsClient(connection);

            CompletableFuture<Void> gotJobs = new CompletableFuture<>();

            CompletableFuture<Boolean> connected = connection.connect(
                    clientId,
                    endpoint, port,
                    null, tlsContext, true, 0)
                    .exceptionally((ex) -> {
                        System.out.println("Exception occurred during connect: " + ex.toString());
                        return null;
                    });
            boolean sessionPresent = connected.get();
            System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");

            GetPendingJobExecutionsSubscriptionRequest getPendingJobExecutionsSubscriptionRequest = new GetPendingJobExecutionsSubscriptionRequest();
            getPendingJobExecutionsSubscriptionRequest.thingName = "crt-test";
            CompletableFuture<Integer> subscribed = jobs.SubscribeToGetPendingJobExecutionsAccepted(getPendingJobExecutionsSubscriptionRequest, (response) -> {
                System.out.println("Pending Jobs: " + (response.queuedJobs.size() == 0 ? "none" : ""));
                for (JobExecutionSummary job : response.queuedJobs) {
                    System.out.println("  " + job.jobId + " @ " + job.lastUpdatedAt.toString());
                }
                gotJobs.complete(null);
            })
                    .exceptionally((ex) -> {
                        System.out.println("Failed to subscribe to GetPendingJobExecutions: " + ex.toString());
                        return null;
                    });
            subscribed.get();
            System.out.println("Subscribed to GetPendingJobExecutionsAccepted");

            subscribed = jobs.SubscribeToGetPendingJobExecutionsRejected(getPendingJobExecutionsSubscriptionRequest, (error) -> {
                onRejectedError(error);
                gotJobs.complete(null);
            });
            subscribed.get();
            System.out.println("Subscribed to GetPendingJobExecutionsRejected");

            GetPendingJobExecutionsRequest getPendingJobExecutionsRequest = new GetPendingJobExecutionsRequest();
            getPendingJobExecutionsRequest.thingName = "crt-test";
            CompletableFuture<Integer> published = jobs.PublishGetPendingJobExecutions(getPendingJobExecutionsRequest)
                    .exceptionally((ex) -> {
                        System.out.println("Exception occurred during publish: " + ex.toString());
                        gotJobs.complete(null);
                        return null;
                    });
            published.get();

            gotJobs.get();
            CompletableFuture<Void> disconnected = connection.disconnect();
            disconnected.get();
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            System.out.println("Exception encountered: " + ex.toString());
        }

        System.out.println("Complete!");
    }
}
