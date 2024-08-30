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
import software.amazon.awssdk.aws.greengrass.model.ComponentUpdatePolicyEvents;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToComponentUpdatesRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToComponentUpdatesResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Subscribe to receive notification if GGC is about to update any components
 */
public class SubscribeToComponentUpdatesOperationContext implements OperationModelContext<SubscribeToComponentUpdatesRequest, SubscribeToComponentUpdatesResponse, EventStreamJsonMessage, ComponentUpdatePolicyEvents> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.SUBSCRIBE_TO_COMPONENT_UPDATES;
  }

  @Override
  public Class<SubscribeToComponentUpdatesRequest> getRequestTypeClass() {
    return SubscribeToComponentUpdatesRequest.class;
  }

  @Override
  public Class<SubscribeToComponentUpdatesResponse> getResponseTypeClass() {
    return SubscribeToComponentUpdatesResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return SubscribeToComponentUpdatesRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return SubscribeToComponentUpdatesResponse.APPLICATION_MODEL_TYPE;
  }

  @Override
  public Optional<Class<EventStreamJsonMessage>> getStreamingRequestTypeClass() {
    return Optional.empty();
  }

  @Override
  public Optional<Class<ComponentUpdatePolicyEvents>> getStreamingResponseTypeClass() {
    return Optional.of(ComponentUpdatePolicyEvents.class);
  }

  public Optional<String> getStreamingRequestApplicationModelType() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getStreamingResponseApplicationModelType() {
    return Optional.of(ComponentUpdatePolicyEvents.APPLICATION_MODEL_TYPE);
  }
}
