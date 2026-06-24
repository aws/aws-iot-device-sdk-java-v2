/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass;

import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.util.Optional;
import software.amazon.awssdk.aws.greengrass.model.IoTCoreConnectionStatusEvent;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToIoTCoreConnectionStatusRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToIoTCoreConnectionStatusResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Subscribe to the connection status of the IoT Core MQTT connection.
 */
public class SubscribeToIoTCoreConnectionStatusOperationContext implements OperationModelContext<SubscribeToIoTCoreConnectionStatusRequest, SubscribeToIoTCoreConnectionStatusResponse, EventStreamJsonMessage, IoTCoreConnectionStatusEvent> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.SUBSCRIBE_TO_IOT_CORE_CONNECTION_STATUS;
  }

  @Override
  public Class<SubscribeToIoTCoreConnectionStatusRequest> getRequestTypeClass() {
    return SubscribeToIoTCoreConnectionStatusRequest.class;
  }

  @Override
  public Class<SubscribeToIoTCoreConnectionStatusResponse> getResponseTypeClass() {
    return SubscribeToIoTCoreConnectionStatusResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return SubscribeToIoTCoreConnectionStatusRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return SubscribeToIoTCoreConnectionStatusResponse.APPLICATION_MODEL_TYPE;
  }

  @Override
  public Optional<Class<EventStreamJsonMessage>> getStreamingRequestTypeClass() {
    return Optional.empty();
  }

  @Override
  public Optional<Class<IoTCoreConnectionStatusEvent>> getStreamingResponseTypeClass() {
    return Optional.of(IoTCoreConnectionStatusEvent.class);
  }

  public Optional<String> getStreamingRequestApplicationModelType() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getStreamingResponseApplicationModelType() {
    return Optional.of(IoTCoreConnectionStatusEvent.APPLICATION_MODEL_TYPE);
  }
}
