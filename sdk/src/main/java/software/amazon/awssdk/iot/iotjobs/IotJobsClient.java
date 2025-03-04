/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotjobs;

import software.amazon.awssdk.iot.iotjobs.model.DescribeJobExecutionRequest;
import software.amazon.awssdk.iot.iotjobs.model.DescribeJobExecutionResponse;
import software.amazon.awssdk.iot.iotjobs.model.DescribeJobExecutionSubscriptionRequest;
import software.amazon.awssdk.iot.iotjobs.model.GetPendingJobExecutionsRequest;
import software.amazon.awssdk.iot.iotjobs.model.GetPendingJobExecutionsResponse;
import software.amazon.awssdk.iot.iotjobs.model.GetPendingJobExecutionsSubscriptionRequest;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionData;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionState;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionSummary;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionsChangedEvent;
import software.amazon.awssdk.iot.iotjobs.model.JobExecutionsChangedSubscriptionRequest;
import software.amazon.awssdk.iot.iotjobs.model.JobStatus;
import software.amazon.awssdk.iot.iotjobs.model.NextJobExecutionChangedEvent;
import software.amazon.awssdk.iot.iotjobs.model.NextJobExecutionChangedSubscriptionRequest;
import software.amazon.awssdk.iot.iotjobs.model.RejectedError;
import software.amazon.awssdk.iot.iotjobs.model.RejectedErrorCode;
import software.amazon.awssdk.iot.iotjobs.model.StartNextJobExecutionResponse;
import software.amazon.awssdk.iot.iotjobs.model.StartNextPendingJobExecutionRequest;
import software.amazon.awssdk.iot.iotjobs.model.StartNextPendingJobExecutionSubscriptionRequest;
import software.amazon.awssdk.iot.iotjobs.model.UpdateJobExecutionRequest;
import software.amazon.awssdk.iot.iotjobs.model.UpdateJobExecutionResponse;
import software.amazon.awssdk.iot.iotjobs.model.UpdateJobExecutionSubscriptionRequest;
import software.amazon.awssdk.iot.iotjobs.model.V2ErrorResponse;

import java.nio.charset.StandardCharsets;

import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.QualityOfService;
import software.amazon.awssdk.crt.mqtt.MqttException;
import software.amazon.awssdk.crt.mqtt.MqttMessage;

import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.EnumSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * The AWS IoT jobs service can be used to define a set of remote operations that are sent to and executed on one or more devices connected to AWS IoT.
 *
 * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#jobs-mqtt-api
 *
*/
public class IotJobsClient {
    private MqttClientConnection connection = null;
    private final Gson gson = getGson();

    /**
     * Constructs a new IotJobsClient
     * @param connection The connection to use
     */
    public IotJobsClient(MqttClientConnection connection) {
        this.connection = connection;
    }

    private Gson getGson() {
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

    /**
     * Gets detailed information about a job execution.
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-describejobexecution
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
    public CompletableFuture<Integer> PublishDescribeJobExecution(
        DescribeJobExecutionRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/jobs/{jobId}/get";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DescribeJobExecutionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.jobId == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DescribeJobExecutionRequest must have a non-null jobId"));
            return result;
        }
        topic = topic.replace("{jobId}", request.jobId);
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    /**
     * Gets the list of all jobs for a thing that are not in a terminal state.
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-getpendingjobexecutions
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
    public CompletableFuture<Integer> PublishGetPendingJobExecutions(
        GetPendingJobExecutionsRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/jobs/get";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetPendingJobExecutionsRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    /**
     * Gets and starts the next pending job execution for a thing (status IN_PROGRESS or QUEUED).
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-startnextpendingjobexecution
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
    public CompletableFuture<Integer> PublishStartNextPendingJobExecution(
        StartNextPendingJobExecutionRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/jobs/start-next";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("StartNextPendingJobExecutionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    /**
     * Updates the status of a job execution. You can optionally create a step timer by setting a value for the stepTimeoutInMinutes property. If you don't update the value of this property by running UpdateJobExecution again, the job execution times out when the step timer expires.
     *
     * If the device is offline, the PUBLISH packet will be sent once the connection resumes.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-updatejobexecution
     *
     * @param request Message to be serialized and sent
     * @param qos Quality of Service for delivering this message
     * @return a future containing the MQTT packet id used to perform the publish operation
     *
     * * For QoS 0, completes as soon as the packet is sent.
     * * For QoS 1, completes when PUBACK is received.
     * * QoS 2 is not supported by AWS IoT.
     */
    public CompletableFuture<Integer> PublishUpdateJobExecution(
        UpdateJobExecutionRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/jobs/{jobId}/update";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateJobExecutionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.jobId == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateJobExecutionRequest must have a non-null jobId"));
            return result;
        }
        topic = topic.replace("{jobId}", request.jobId);
        String payloadJson = gson.toJson(request);
        MqttMessage message = new MqttMessage(topic, payloadJson.getBytes(StandardCharsets.UTF_8));
        return connection.publish(message, qos, false);
    }

    /**
     * Subscribes to the accepted topic for the DescribeJobExecution operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-describejobexecution
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToDescribeJobExecutionAccepted(
        DescribeJobExecutionSubscriptionRequest request,
        QualityOfService qos,
        Consumer<DescribeJobExecutionResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/jobs/{jobId}/get/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DescribeJobExecutionSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.jobId == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DescribeJobExecutionSubscriptionRequest must have a non-null jobId"));
            return result;
        }
        topic = topic.replace("{jobId}", request.jobId);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                DescribeJobExecutionResponse response = gson.fromJson(payload, DescribeJobExecutionResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the accepted topic for the DescribeJobExecution operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-describejobexecution
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToDescribeJobExecutionAccepted(
        DescribeJobExecutionSubscriptionRequest request,
        QualityOfService qos,
        Consumer<DescribeJobExecutionResponse> handler) {
        return SubscribeToDescribeJobExecutionAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic for the DescribeJobExecution operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-describejobexecution
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToDescribeJobExecutionRejected(
        DescribeJobExecutionSubscriptionRequest request,
        QualityOfService qos,
        Consumer<RejectedError> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/jobs/{jobId}/get/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DescribeJobExecutionSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.jobId == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DescribeJobExecutionSubscriptionRequest must have a non-null jobId"));
            return result;
        }
        topic = topic.replace("{jobId}", request.jobId);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                RejectedError response = gson.fromJson(payload, RejectedError.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the rejected topic for the DescribeJobExecution operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-describejobexecution
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToDescribeJobExecutionRejected(
        DescribeJobExecutionSubscriptionRequest request,
        QualityOfService qos,
        Consumer<RejectedError> handler) {
        return SubscribeToDescribeJobExecutionRejected(request, qos, handler, null);
    }

    /**
     * Subscribes to the accepted topic for the GetPendingJobsExecutions operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-getpendingjobexecutions
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToGetPendingJobExecutionsAccepted(
        GetPendingJobExecutionsSubscriptionRequest request,
        QualityOfService qos,
        Consumer<GetPendingJobExecutionsResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/jobs/get/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetPendingJobExecutionsSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                GetPendingJobExecutionsResponse response = gson.fromJson(payload, GetPendingJobExecutionsResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the accepted topic for the GetPendingJobsExecutions operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-getpendingjobexecutions
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToGetPendingJobExecutionsAccepted(
        GetPendingJobExecutionsSubscriptionRequest request,
        QualityOfService qos,
        Consumer<GetPendingJobExecutionsResponse> handler) {
        return SubscribeToGetPendingJobExecutionsAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic for the GetPendingJobsExecutions operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-getpendingjobexecutions
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToGetPendingJobExecutionsRejected(
        GetPendingJobExecutionsSubscriptionRequest request,
        QualityOfService qos,
        Consumer<RejectedError> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/jobs/get/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetPendingJobExecutionsSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                RejectedError response = gson.fromJson(payload, RejectedError.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the rejected topic for the GetPendingJobsExecutions operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-getpendingjobexecutions
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToGetPendingJobExecutionsRejected(
        GetPendingJobExecutionsSubscriptionRequest request,
        QualityOfService qos,
        Consumer<RejectedError> handler) {
        return SubscribeToGetPendingJobExecutionsRejected(request, qos, handler, null);
    }

    /**
     * Subscribes to JobExecutionsChanged notifications for a given IoT thing.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-jobexecutionschanged
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToJobExecutionsChangedEvents(
        JobExecutionsChangedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<JobExecutionsChangedEvent> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/jobs/notify";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("JobExecutionsChangedSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                JobExecutionsChangedEvent response = gson.fromJson(payload, JobExecutionsChangedEvent.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to JobExecutionsChanged notifications for a given IoT thing.
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-jobexecutionschanged
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToJobExecutionsChangedEvents(
        JobExecutionsChangedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<JobExecutionsChangedEvent> handler) {
        return SubscribeToJobExecutionsChangedEvents(request, qos, handler, null);
    }

    /**
     * 
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-nextjobexecutionchanged
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToNextJobExecutionChangedEvents(
        NextJobExecutionChangedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<NextJobExecutionChangedEvent> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/jobs/notify-next";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("NextJobExecutionChangedSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                NextJobExecutionChangedEvent response = gson.fromJson(payload, NextJobExecutionChangedEvent.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * 
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-nextjobexecutionchanged
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToNextJobExecutionChangedEvents(
        NextJobExecutionChangedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<NextJobExecutionChangedEvent> handler) {
        return SubscribeToNextJobExecutionChangedEvents(request, qos, handler, null);
    }

    /**
     * Subscribes to the accepted topic for the StartNextPendingJobExecution operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-startnextpendingjobexecution
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToStartNextPendingJobExecutionAccepted(
        StartNextPendingJobExecutionSubscriptionRequest request,
        QualityOfService qos,
        Consumer<StartNextJobExecutionResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/jobs/start-next/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("StartNextPendingJobExecutionSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                StartNextJobExecutionResponse response = gson.fromJson(payload, StartNextJobExecutionResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the accepted topic for the StartNextPendingJobExecution operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-startnextpendingjobexecution
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToStartNextPendingJobExecutionAccepted(
        StartNextPendingJobExecutionSubscriptionRequest request,
        QualityOfService qos,
        Consumer<StartNextJobExecutionResponse> handler) {
        return SubscribeToStartNextPendingJobExecutionAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic for the StartNextPendingJobExecution operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-startnextpendingjobexecution
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToStartNextPendingJobExecutionRejected(
        StartNextPendingJobExecutionSubscriptionRequest request,
        QualityOfService qos,
        Consumer<RejectedError> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/jobs/start-next/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("StartNextPendingJobExecutionSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                RejectedError response = gson.fromJson(payload, RejectedError.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the rejected topic for the StartNextPendingJobExecution operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-startnextpendingjobexecution
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToStartNextPendingJobExecutionRejected(
        StartNextPendingJobExecutionSubscriptionRequest request,
        QualityOfService qos,
        Consumer<RejectedError> handler) {
        return SubscribeToStartNextPendingJobExecutionRejected(request, qos, handler, null);
    }

    /**
     * Subscribes to the accepted topic for the UpdateJobExecution operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-updatejobexecution
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToUpdateJobExecutionAccepted(
        UpdateJobExecutionSubscriptionRequest request,
        QualityOfService qos,
        Consumer<UpdateJobExecutionResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/jobs/{jobId}/update/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateJobExecutionSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.jobId == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateJobExecutionSubscriptionRequest must have a non-null jobId"));
            return result;
        }
        topic = topic.replace("{jobId}", request.jobId);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                UpdateJobExecutionResponse response = gson.fromJson(payload, UpdateJobExecutionResponse.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the accepted topic for the UpdateJobExecution operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-updatejobexecution
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToUpdateJobExecutionAccepted(
        UpdateJobExecutionSubscriptionRequest request,
        QualityOfService qos,
        Consumer<UpdateJobExecutionResponse> handler) {
        return SubscribeToUpdateJobExecutionAccepted(request, qos, handler, null);
    }

    /**
     * Subscribes to the rejected topic for the UpdateJobExecution operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-updatejobexecution
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     * @param exceptionHandler callback function to invoke if an exception occurred deserializing a message
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToUpdateJobExecutionRejected(
        UpdateJobExecutionSubscriptionRequest request,
        QualityOfService qos,
        Consumer<RejectedError> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/jobs/{jobId}/update/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateJobExecutionSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        if (request.jobId == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateJobExecutionSubscriptionRequest must have a non-null jobId"));
            return result;
        }
        topic = topic.replace("{jobId}", request.jobId);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                RejectedError response = gson.fromJson(payload, RejectedError.class);
                handler.accept(response);
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(e);
                }
            }
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    /**
     * Subscribes to the rejected topic for the UpdateJobExecution operation
     *
     * Once subscribed, `handler` is invoked each time a message matching
     * the `topic` is received. It is possible for such messages to arrive before
     * the SUBACK is received.
     *
     * AWS documentation: https://docs.aws.amazon.com/iot/latest/developerguide/jobs-api.html#mqtt-updatejobexecution
     *
     * @param request Subscription request configuration
     * @param qos Maximum requested QoS that server may use when sending messages to the client.
     *            The server may grant a lower QoS in the SUBACK
     * @param handler callback function to invoke with messages received on the subscription topic
     *
     * @return a future containing the MQTT packet id used to perform the subscribe operation
     */
    public CompletableFuture<Integer> SubscribeToUpdateJobExecutionRejected(
        UpdateJobExecutionSubscriptionRequest request,
        QualityOfService qos,
        Consumer<RejectedError> handler) {
        return SubscribeToUpdateJobExecutionRejected(request, qos, handler, null);
    }

}
