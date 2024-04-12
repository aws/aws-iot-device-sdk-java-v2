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
import software.amazon.awssdk.aws.greengrass.model.ConfigurationUpdateEvents;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToConfigurationUpdateRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToConfigurationUpdateResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Subscribes to be notified when GGC updates the configuration for a given componentName and keyName.
 */
public class SubscribeToConfigurationUpdateOperationContext implements OperationModelContext<SubscribeToConfigurationUpdateRequest, SubscribeToConfigurationUpdateResponse, EventStreamJsonMessage, ConfigurationUpdateEvents> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.SUBSCRIBE_TO_CONFIGURATION_UPDATE;
  }

  @Override
  public Class<SubscribeToConfigurationUpdateRequest> getRequestTypeClass() {
    return SubscribeToConfigurationUpdateRequest.class;
  }

  @Override
  public Class<SubscribeToConfigurationUpdateResponse> getResponseTypeClass() {
    return SubscribeToConfigurationUpdateResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return SubscribeToConfigurationUpdateRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return SubscribeToConfigurationUpdateResponse.APPLICATION_MODEL_TYPE;
  }

  @Override
  public Optional<Class<EventStreamJsonMessage>> getStreamingRequestTypeClass() {
    return Optional.empty();
  }

  @Override
  public Optional<Class<ConfigurationUpdateEvents>> getStreamingResponseTypeClass() {
    return Optional.of(ConfigurationUpdateEvents.class);
  }

  public Optional<String> getStreamingRequestApplicationModelType() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getStreamingResponseApplicationModelType() {
    return Optional.of(ConfigurationUpdateEvents.APPLICATION_MODEL_TYPE);
  }
}
