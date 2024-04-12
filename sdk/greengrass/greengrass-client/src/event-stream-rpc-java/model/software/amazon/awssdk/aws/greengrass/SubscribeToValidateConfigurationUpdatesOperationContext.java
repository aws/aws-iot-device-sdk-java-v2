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
import software.amazon.awssdk.aws.greengrass.model.SubscribeToValidateConfigurationUpdatesRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToValidateConfigurationUpdatesResponse;
import software.amazon.awssdk.aws.greengrass.model.ValidateConfigurationUpdateEvents;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Subscribes to be notified when GGC is about to update configuration for this component
 * GGC will wait for a timeout period before it proceeds with the update.
 * If the new configuration is not valid this component can use the SendConfigurationValidityReport
 * operation to indicate that
 */
public class SubscribeToValidateConfigurationUpdatesOperationContext implements OperationModelContext<SubscribeToValidateConfigurationUpdatesRequest, SubscribeToValidateConfigurationUpdatesResponse, EventStreamJsonMessage, ValidateConfigurationUpdateEvents> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.SUBSCRIBE_TO_VALIDATE_CONFIGURATION_UPDATES;
  }

  @Override
  public Class<SubscribeToValidateConfigurationUpdatesRequest> getRequestTypeClass() {
    return SubscribeToValidateConfigurationUpdatesRequest.class;
  }

  @Override
  public Class<SubscribeToValidateConfigurationUpdatesResponse> getResponseTypeClass() {
    return SubscribeToValidateConfigurationUpdatesResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return SubscribeToValidateConfigurationUpdatesRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return SubscribeToValidateConfigurationUpdatesResponse.APPLICATION_MODEL_TYPE;
  }

  @Override
  public Optional<Class<EventStreamJsonMessage>> getStreamingRequestTypeClass() {
    return Optional.empty();
  }

  @Override
  public Optional<Class<ValidateConfigurationUpdateEvents>> getStreamingResponseTypeClass() {
    return Optional.of(ValidateConfigurationUpdateEvents.class);
  }

  public Optional<String> getStreamingRequestApplicationModelType() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getStreamingResponseApplicationModelType() {
    return Optional.of(ValidateConfigurationUpdateEvents.APPLICATION_MODEL_TYPE);
  }
}
