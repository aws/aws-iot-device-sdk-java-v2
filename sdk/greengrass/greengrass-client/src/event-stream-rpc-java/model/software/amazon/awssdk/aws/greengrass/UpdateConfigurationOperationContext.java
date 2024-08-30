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
import software.amazon.awssdk.aws.greengrass.model.UpdateConfigurationRequest;
import software.amazon.awssdk.aws.greengrass.model.UpdateConfigurationResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Update this component's configuration by replacing the value of given keyName with the newValue.
 * If an oldValue is specified then update will only take effect id the current value matches the given oldValue
 */
public class UpdateConfigurationOperationContext implements OperationModelContext<UpdateConfigurationRequest, UpdateConfigurationResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.UPDATE_CONFIGURATION;
  }

  @Override
  public Class<UpdateConfigurationRequest> getRequestTypeClass() {
    return UpdateConfigurationRequest.class;
  }

  @Override
  public Class<UpdateConfigurationResponse> getResponseTypeClass() {
    return UpdateConfigurationResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return UpdateConfigurationRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return UpdateConfigurationResponse.APPLICATION_MODEL_TYPE;
  }

  @Override
  public Optional<Class<EventStreamJsonMessage>> getStreamingRequestTypeClass() {
    return Optional.empty();
  }

  @Override
  public Optional<Class<EventStreamJsonMessage>> getStreamingResponseTypeClass() {
    return Optional.empty();
  }

  public Optional<String> getStreamingRequestApplicationModelType() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getStreamingResponseApplicationModelType() {
    return Optional.empty();
  }
}
