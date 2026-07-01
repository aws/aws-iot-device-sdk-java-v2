/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs;

import java.lang.AutoCloseable;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;
import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.iot.*;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.iot.*;
import software.amazon.awssdk.iot.iotjobs.model.*;

/**
 * The AWS IoT jobs service can be used to define a set of remote operations that are sent to and executed on one or more devices connected to AWS IoT.
 *
 * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#jobs-mqtt-api
 *
*/
public class IotJobsV2Client implements AutoCloseable {

    private MqttRequestResponseClient rrClient;
    private final Gson gson;

    private Gson createGson() {
        GsonBuilder gson = new GsonBuilder();
        gson.disableHtmlEscaping();
        gson.registerTypeAdapter(Timestamp.class, new Timestamp.Serializer());
        gson.registerTypeAdapter(Timestamp.class, new Timestamp.Deserializer());
        addTypeAdapters(gson);
        return gson.create();
    }

    private void addTypeAdapters(GsonBuilder gson) {
        gson.registerTypeAdapter(JobStatus.class, new EnumSerializer<JobStatus>());
        gson.registerTypeAdapter(RejectedErrorCode.class, new EnumSerializer<RejectedErrorCode>());
    }

    private IotJobsV2Client(MqttRequestResponseClient rrClient) {
        this.rrClient = rrClient;
        this.gson = createGson();
    }

    /**
     * Constructs a new IotJobsV2Client, using an MQTT5 client as transport
     *
     * @param protocolClient the MQTT5 client to use
     * @param options configuration options to use
     */
    static public IotJobsV2Client newFromMqtt5(Mqtt5Client protocolClient, MqttRequestResponseClientOptions options) {
        MqttRequestResponseClient rrClient = new MqttRequestResponseClient(protocolClient, options);
        return new IotJobsV2Client(rrClient);
    }

    /**
     * Constructs a new IotJobsV2Client, using an MQTT311 client as transport
     *
     * @param protocolClient the MQTT311 client to use
     * @param options configuration options to use
     */
    static public IotJobsV2Client newFromMqtt311(MqttClientConnection protocolClient, MqttRequestResponseClientOptions options) {
        MqttRequestResponseClient rrClient = new MqttRequestResponseClient(protocolClient, options);
        return new IotJobsV2Client(rrClient);
    }

    /**
     * Releases all resources used by the client.  It is not valid to invoke operations
     * on the client after it has been closed.
     */
    public void close() {
        this.rrClient.decRef();
        this.rrClient = null;
    }

    private NextJobExecutionChangedEvent createNextJobExecutionChangedEvent(IncomingPublishEvent publishEvent) {
        String payload = new String(publishEvent.getPayload(), StandardCharsets.UTF_8);
        return this.gson.fromJson(payload, NextJobExecutionChangedEvent.class);
    }

    private JobExecutionsChangedEvent createJobExecutionsChangedEvent(IncomingPublishEvent publishEvent) {
        String payload = new String(publishEvent.getPayload(), StandardCharsets.UTF_8);
        return this.gson.fromJson(payload, JobExecutionsChangedEvent.class);
    }

    /**
     * Gets detailed information about a job execution.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-describejobexecution
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<DescribeJobExecutionResponse> describeJobExecution(DescribeJobExecutionRequest request) {
        V2ClientFuture<DescribeJobExecutionResponse> responseFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("DescribeJobExecutionRequest.thingName cannot be null");
            }

            if (request.jobId == null) {
                throw new CrtRuntimeException("DescribeJobExecutionRequest.jobId cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            // Correlation Token
            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            // Publish Topic
            String publishTopic = "$aws/things/{thingName}/jobs/{jobId}/get";
            publishTopic = publishTopic.replace("{thingName}", request.thingName);
            publishTopic = publishTopic.replace("{jobId}", request.jobId);
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/things/{thingName}/jobs/{jobId}/get/+";
            subscription0 = subscription0.replace("{thingName}", request.thingName);
            subscription0 = subscription0.replace("{jobId}", request.jobId);
            builder.withSubscription(subscription0);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, DescribeJobExecutionResponse.class, responseTopic2, V2ErrorResponse.class, IotJobsV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Gets the list of all jobs for a thing that are not in a terminal state.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-getpendingjobexecutions
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<GetPendingJobExecutionsResponse> getPendingJobExecutions(GetPendingJobExecutionsRequest request) {
        V2ClientFuture<GetPendingJobExecutionsResponse> responseFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("GetPendingJobExecutionsRequest.thingName cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            // Correlation Token
            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            // Publish Topic
            String publishTopic = "$aws/things/{thingName}/jobs/get";
            publishTopic = publishTopic.replace("{thingName}", request.thingName);
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/things/{thingName}/jobs/get/+";
            subscription0 = subscription0.replace("{thingName}", request.thingName);
            builder.withSubscription(subscription0);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, GetPendingJobExecutionsResponse.class, responseTopic2, V2ErrorResponse.class, IotJobsV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Gets and starts the next pending job execution for a thing (status IN_PROGRESS or QUEUED).
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-startnextpendingjobexecution
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<StartNextJobExecutionResponse> startNextPendingJobExecution(StartNextPendingJobExecutionRequest request) {
        V2ClientFuture<StartNextJobExecutionResponse> responseFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("StartNextPendingJobExecutionRequest.thingName cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            // Correlation Token
            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            // Publish Topic
            String publishTopic = "$aws/things/{thingName}/jobs/start-next";
            publishTopic = publishTopic.replace("{thingName}", request.thingName);
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/things/{thingName}/jobs/start-next/+";
            subscription0 = subscription0.replace("{thingName}", request.thingName);
            builder.withSubscription(subscription0);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, StartNextJobExecutionResponse.class, responseTopic2, V2ErrorResponse.class, IotJobsV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Updates the status of a job execution. You can optionally create a step timer by setting a value for the stepTimeoutInMinutes property. If you don't update the value of this property by running UpdateJobExecution again, the job execution times out when the step timer expires.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-updatejobexecution
     *
     * @param request modeled request to perform
     *
     * @return a future that will complete with the corresponding response
     */
    public CompletableFuture<UpdateJobExecutionResponse> updateJobExecution(UpdateJobExecutionRequest request) {
        V2ClientFuture<UpdateJobExecutionResponse> responseFuture = new V2ClientFuture<>();

        try {
            if (request.thingName == null) {
                throw new CrtRuntimeException("UpdateJobExecutionRequest.thingName cannot be null");
            }

            if (request.jobId == null) {
                throw new CrtRuntimeException("UpdateJobExecutionRequest.jobId cannot be null");
            }

            RequestResponseOperation.RequestResponseOperationBuilder builder = RequestResponseOperation.builder();

            // Correlation Token
            String correlationToken = UUID.randomUUID().toString();
            request.clientToken = correlationToken;
            builder.withCorrelationToken(correlationToken);

            // Publish Topic
            String publishTopic = "$aws/things/{thingName}/jobs/{jobId}/update";
            publishTopic = publishTopic.replace("{thingName}", request.thingName);
            publishTopic = publishTopic.replace("{jobId}", request.jobId);
            builder.withPublishTopic(publishTopic);

            // Payload
            String payloadJson = gson.toJson(request);
            builder.withPayload(payloadJson.getBytes(StandardCharsets.UTF_8));

            // Subscriptions
            String subscription0 = "$aws/things/{thingName}/jobs/{jobId}/update/+";
            subscription0 = subscription0.replace("{thingName}", request.thingName);
            subscription0 = subscription0.replace("{jobId}", request.jobId);
            builder.withSubscription(subscription0);

            // Response paths
            ResponsePath.ResponsePathBuilder pathBuilder1 = ResponsePath.builder();
            String responseTopic1 = publishTopic + "/accepted";
            pathBuilder1.withResponseTopic(publishTopic + "/accepted");
            pathBuilder1.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder1.build());

            ResponsePath.ResponsePathBuilder pathBuilder2 = ResponsePath.builder();
            String responseTopic2 = publishTopic + "/rejected";
            pathBuilder2.withResponseTopic(publishTopic + "/rejected");
            pathBuilder2.withCorrelationTokenJsonPath("clientToken");
            builder.withResponsePath(pathBuilder2.build());

            // Submit
            submitOperation(responseFuture, builder.build(), responseTopic1, UpdateJobExecutionResponse.class, responseTopic2, V2ErrorResponse.class, IotJobsV2Client::createV2ErrorResponseException);
        } catch (Exception e) {
            responseFuture.completeExceptionally(createV2ErrorResponseException(e.getMessage(), null));
        }

        return responseFuture;
    }

    /**
     * Creates a stream of JobExecutionsChanged notifications for a given IoT thing.
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-jobexecutionschanged
     *
     * @param request modeled streaming operation subscription configuration
     * @param options set of callbacks that the operation should invoke in response to related events
     *
     * @return a streaming operation which will invoke a callback every time a message is received on the
     *    associated MQTT topic
     */
    public StreamingOperation createJobExecutionsChangedStream(JobExecutionsChangedSubscriptionRequest request, V2ClientStreamOptions<JobExecutionsChangedEvent> options) {
        String topic = "$aws/things/{thingName}/jobs/notify";

        if (request.thingName == null) {
            throw new CrtRuntimeException("JobExecutionsChangedSubscriptionRequest.thingName cannot be null");
        }
        topic = topic.replace("{thingName}", request.thingName);

        StreamingOperationOptions innerOptions = StreamingOperationOptions.builder()
            .withTopic(topic)
            .withSubscriptionStatusEventCallback(options.subscriptionEventHandler())
            .withIncomingPublishEventCallback((event) -> {
                try {
                    JobExecutionsChangedEvent response = createJobExecutionsChangedEvent(event);
                    options.streamEventHandler().accept(response);
                } catch (Exception e) {
                    V2DeserializationFailureEvent failureEvent = V2DeserializationFailureEvent.builder()
                        .withCause(e)
                        .withPayload(event.getPayload())
                        .withTopic(event.getTopic())
                        .build();
                    options.deserializationFailureHandler().accept(failureEvent);
                }
            })
            .build();

        return this.rrClient.createStream(innerOptions);
    }

    /**
     * 
     *
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-nextjobexecutionchanged
     *
     * @param request modeled streaming operation subscription configuration
     * @param options set of callbacks that the operation should invoke in response to related events
     *
     * @return a streaming operation which will invoke a callback every time a message is received on the
     *    associated MQTT topic
     */
    public StreamingOperation createNextJobExecutionChangedStream(NextJobExecutionChangedSubscriptionRequest request, V2ClientStreamOptions<NextJobExecutionChangedEvent> options) {
        String topic = "$aws/things/{thingName}/jobs/notify-next";

        if (request.thingName == null) {
            throw new CrtRuntimeException("NextJobExecutionChangedSubscriptionRequest.thingName cannot be null");
        }
        topic = topic.replace("{thingName}", request.thingName);

        StreamingOperationOptions innerOptions = StreamingOperationOptions.builder()
            .withTopic(topic)
            .withSubscriptionStatusEventCallback(options.subscriptionEventHandler())
            .withIncomingPublishEventCallback((event) -> {
                try {
                    NextJobExecutionChangedEvent response = createNextJobExecutionChangedEvent(event);
                    options.streamEventHandler().accept(response);
                } catch (Exception e) {
                    V2DeserializationFailureEvent failureEvent = V2DeserializationFailureEvent.builder()
                        .withCause(e)
                        .withPayload(event.getPayload())
                        .withTopic(event.getTopic())
                        .build();
                    options.deserializationFailureHandler().accept(failureEvent);
                }
            })
            .build();

        return this.rrClient.createStream(innerOptions);
    }

    static private Throwable createV2ErrorResponseException(String message, V2ErrorResponse errorResponse) {
        return new V2ErrorResponseException(message, errorResponse);
    }

    private <T, E> void submitOperation(V2ClientFuture<T> finalFuture, RequestResponseOperation operation, String responseTopic, Class<T> responseClass, String errorTopic, Class<E> errorClass, BiFunction<String, E, Throwable> exceptionFactory) {
        try {
            CompletableFuture<MqttRequestResponse> responseFuture = this.rrClient.submitRequest(operation);
            CompletableFuture<MqttRequestResponse> compositeFuture = responseFuture.whenComplete((res, ex) -> {
                if (ex != null) {
                    finalFuture.completeExceptionally(exceptionFactory.apply(ex.getMessage(), null));
                } else if (res.getTopic().equals(responseTopic)) {
                    try {
                        String payload = new String(res.getPayload(), StandardCharsets.UTF_8);
                        T response = this.gson.fromJson(payload, responseClass);
                        finalFuture.complete(response);
                    } catch (Exception e) {
                        finalFuture.completeExceptionally(exceptionFactory.apply(e.getMessage(), null));
                    }
                } else if (res.getTopic().equals(errorTopic)) {
                    try {
                        String payload = new String(res.getPayload(), StandardCharsets.UTF_8);
                        E error = this.gson.fromJson(payload, errorClass);
                        finalFuture.completeExceptionally(exceptionFactory.apply("Request-response operation failure", error));
                    } catch (Exception e) {
                        finalFuture.completeExceptionally(exceptionFactory.apply(e.getMessage(), null));
                    }
                } else {
                    finalFuture.completeExceptionally(exceptionFactory.apply("Request-response operation completed on unknown topic: " + res.getTopic(), null));
                }
            });
            finalFuture.setTriggeringFuture(compositeFuture);
        } catch (Exception ex) {
            finalFuture.completeExceptionally(exceptionFactory.apply(ex.getMessage(), null));
        }
    }

}
