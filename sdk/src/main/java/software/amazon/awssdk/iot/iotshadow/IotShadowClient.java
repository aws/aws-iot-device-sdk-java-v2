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

package software.amazon.awssdk.iot.iotshadow;

import java.util.HashMap;
import software.amazon.awssdk.iot.iotshadow.model.DeleteShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.DeleteShadowResponse;
import software.amazon.awssdk.iot.iotshadow.model.DeleteShadowSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.ErrorResponse;
import software.amazon.awssdk.iot.iotshadow.model.GetShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.GetShadowResponse;
import software.amazon.awssdk.iot.iotshadow.model.GetShadowSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.ShadowDeltaUpdatedEvent;
import software.amazon.awssdk.iot.iotshadow.model.ShadowDeltaUpdatedSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.ShadowMetadata;
import software.amazon.awssdk.iot.iotshadow.model.ShadowState;
import software.amazon.awssdk.iot.iotshadow.model.ShadowStateWithDelta;
import software.amazon.awssdk.iot.iotshadow.model.ShadowUpdatedEvent;
import software.amazon.awssdk.iot.iotshadow.model.ShadowUpdatedSnapshot;
import software.amazon.awssdk.iot.iotshadow.model.ShadowUpdatedSubscriptionRequest;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowRequest;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowResponse;
import software.amazon.awssdk.iot.iotshadow.model.UpdateShadowSubscriptionRequest;

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

public class IotShadowClient {
    private MqttClientConnection connection = null;
    private final Gson gson = getGson();

    public IotShadowClient(MqttClientConnection connection) {
        this.connection = connection;
    }

    private Gson getGson() {
        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(Timestamp.class, new Timestamp.Serializer());
        gson.registerTypeAdapter(Timestamp.class, new Timestamp.Deserializer());
        addTypeAdapters(gson);
        return gson.create();
    }

    private void addTypeAdapters(GsonBuilder gson) {
    }

    public CompletableFuture<Integer> SubscribeToUpdateShadowRejected(
        UpdateShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/update/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload().array(), "UTF-8");
                ErrorResponse response = gson.fromJson(payload, ErrorResponse.class);
                handler.accept(response);
            } catch (UnsupportedEncodingException ex) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(ex);
                }
            };
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    public CompletableFuture<Integer> SubscribeToUpdateShadowRejected(
        UpdateShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToUpdateShadowRejected(request, qos, handler, null);
    }

    public CompletableFuture<Integer> PublishUpdateShadow(
        UpdateShadowRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/shadow/update";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateShadowRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        String payloadJson = gson.toJson(request);
        ByteBuffer payload = ByteBuffer.allocateDirect(payloadJson.length());
        payload.put(payloadJson.getBytes());
        MqttMessage message = new MqttMessage(topic, payload);
        return connection.publish(message, qos, false);
    }

    public CompletableFuture<Integer> PublishGetShadow(
        GetShadowRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/shadow/get";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetShadowRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        String payloadJson = gson.toJson(request);
        ByteBuffer payload = ByteBuffer.allocateDirect(payloadJson.length());
        payload.put(payloadJson.getBytes());
        MqttMessage message = new MqttMessage(topic, payload);
        return connection.publish(message, qos, false);
    }

    public CompletableFuture<Integer> SubscribeToShadowDeltaUpdatedEvents(
        ShadowDeltaUpdatedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ShadowDeltaUpdatedEvent> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/update/delta";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("ShadowDeltaUpdatedSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload().array(), "UTF-8");
                ShadowDeltaUpdatedEvent response = gson.fromJson(payload, ShadowDeltaUpdatedEvent.class);
                handler.accept(response);
            } catch (UnsupportedEncodingException ex) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(ex);
                }
            };
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    public CompletableFuture<Integer> SubscribeToShadowDeltaUpdatedEvents(
        ShadowDeltaUpdatedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ShadowDeltaUpdatedEvent> handler) {
        return SubscribeToShadowDeltaUpdatedEvents(request, qos, handler, null);
    }

    public CompletableFuture<Integer> SubscribeToUpdateShadowAccepted(
        UpdateShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<UpdateShadowResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/update/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("UpdateShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload().array(), "UTF-8");
                UpdateShadowResponse response = gson.fromJson(payload, UpdateShadowResponse.class);
                handler.accept(response);
            } catch (UnsupportedEncodingException ex) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(ex);
                }
            };
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    public CompletableFuture<Integer> SubscribeToUpdateShadowAccepted(
        UpdateShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<UpdateShadowResponse> handler) {
        return SubscribeToUpdateShadowAccepted(request, qos, handler, null);
    }

    public CompletableFuture<Integer> PublishDeleteShadow(
        DeleteShadowRequest request,
        QualityOfService qos) {
        String topic = "$aws/things/{thingName}/shadow/delete";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DeleteShadowRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        String payloadJson = gson.toJson(request);
        ByteBuffer payload = ByteBuffer.allocateDirect(payloadJson.length());
        payload.put(payloadJson.getBytes());
        MqttMessage message = new MqttMessage(topic, payload);
        return connection.publish(message, qos, false);
    }

    public CompletableFuture<Integer> SubscribeToDeleteShadowAccepted(
        DeleteShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<DeleteShadowResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/delete/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DeleteShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload().array(), "UTF-8");
                DeleteShadowResponse response = gson.fromJson(payload, DeleteShadowResponse.class);
                handler.accept(response);
            } catch (UnsupportedEncodingException ex) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(ex);
                }
            };
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    public CompletableFuture<Integer> SubscribeToDeleteShadowAccepted(
        DeleteShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<DeleteShadowResponse> handler) {
        return SubscribeToDeleteShadowAccepted(request, qos, handler, null);
    }

    public CompletableFuture<Integer> SubscribeToGetShadowAccepted(
        GetShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<GetShadowResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/get/accepted";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload().array(), "UTF-8");
                GetShadowResponse response = gson.fromJson(payload, GetShadowResponse.class);
                handler.accept(response);
            } catch (UnsupportedEncodingException ex) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(ex);
                }
            };
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    public CompletableFuture<Integer> SubscribeToGetShadowAccepted(
        GetShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<GetShadowResponse> handler) {
        return SubscribeToGetShadowAccepted(request, qos, handler, null);
    }

    public CompletableFuture<Integer> SubscribeToShadowUpdatedEvents(
        ShadowUpdatedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ShadowUpdatedEvent> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/update/documents";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("ShadowUpdatedSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload().array(), "UTF-8");
                ShadowUpdatedEvent response = gson.fromJson(payload, ShadowUpdatedEvent.class);
                handler.accept(response);
            } catch (UnsupportedEncodingException ex) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(ex);
                }
            };
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    public CompletableFuture<Integer> SubscribeToShadowUpdatedEvents(
        ShadowUpdatedSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ShadowUpdatedEvent> handler) {
        return SubscribeToShadowUpdatedEvents(request, qos, handler, null);
    }

    public CompletableFuture<Integer> SubscribeToDeleteShadowRejected(
        DeleteShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/delete/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("DeleteShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload().array(), "UTF-8");
                ErrorResponse response = gson.fromJson(payload, ErrorResponse.class);
                handler.accept(response);
            } catch (UnsupportedEncodingException ex) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(ex);
                }
            };
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    public CompletableFuture<Integer> SubscribeToDeleteShadowRejected(
        DeleteShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToDeleteShadowRejected(request, qos, handler, null);
    }

    public CompletableFuture<Integer> SubscribeToGetShadowRejected(
        GetShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler,
        Consumer<Exception> exceptionHandler) {
        String topic = "$aws/things/{thingName}/shadow/get/rejected";
        if (request.thingName == null) {
            CompletableFuture<Integer> result = new CompletableFuture<Integer>();
            result.completeExceptionally(new MqttException("GetShadowSubscriptionRequest must have a non-null thingName"));
            return result;
        }
        topic = topic.replace("{thingName}", request.thingName);
        Consumer<MqttMessage> messageHandler = (message) -> {
            try {
                String payload = new String(message.getPayload().array(), "UTF-8");
                ErrorResponse response = gson.fromJson(payload, ErrorResponse.class);
                handler.accept(response);
            } catch (UnsupportedEncodingException ex) {
                if (exceptionHandler != null) {
                    exceptionHandler.accept(ex);
                }
            };
        };
        return connection.subscribe(topic, qos, messageHandler);
    }

    public CompletableFuture<Integer> SubscribeToGetShadowRejected(
        GetShadowSubscriptionRequest request,
        QualityOfService qos,
        Consumer<ErrorResponse> handler) {
        return SubscribeToGetShadowRejected(request, qos, handler, null);
    }

}
