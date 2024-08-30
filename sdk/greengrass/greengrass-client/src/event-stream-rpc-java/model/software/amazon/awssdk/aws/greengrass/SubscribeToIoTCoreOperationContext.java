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
import software.amazon.awssdk.aws.greengrass.model.IoTCoreMessage;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToIoTCoreRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToIoTCoreResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Subscribe to a topic in AWS IoT message broker.
 */
public class SubscribeToIoTCoreOperationContext implements OperationModelContext<SubscribeToIoTCoreRequest, SubscribeToIoTCoreResponse, EventStreamJsonMessage, IoTCoreMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.SUBSCRIBE_TO_IOT_CORE;
  }

  @Override
  public Class<SubscribeToIoTCoreRequest> getRequestTypeClass() {
    return SubscribeToIoTCoreRequest.class;
  }

  @Override
  public Class<SubscribeToIoTCoreResponse> getResponseTypeClass() {
    return SubscribeToIoTCoreResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return SubscribeToIoTCoreRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return SubscribeToIoTCoreResponse.APPLICATION_MODEL_TYPE;
  }

  @Override
  public Optional<Class<EventStreamJsonMessage>> getStreamingRequestTypeClass() {
    return Optional.empty();
  }

  @Override
  public Optional<Class<IoTCoreMessage>> getStreamingResponseTypeClass() {
    return Optional.of(IoTCoreMessage.class);
  }

  public Optional<String> getStreamingRequestApplicationModelType() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getStreamingResponseApplicationModelType() {
    return Optional.of(IoTCoreMessage.APPLICATION_MODEL_TYPE);
  }
}
