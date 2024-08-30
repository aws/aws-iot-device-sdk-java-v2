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
import software.amazon.awssdk.aws.greengrass.model.CreateDebugPasswordRequest;
import software.amazon.awssdk.aws.greengrass.model.CreateDebugPasswordResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Generate a password for the LocalDebugConsole component
 */
public class CreateDebugPasswordOperationContext implements OperationModelContext<CreateDebugPasswordRequest, CreateDebugPasswordResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.CREATE_DEBUG_PASSWORD;
  }

  @Override
  public Class<CreateDebugPasswordRequest> getRequestTypeClass() {
    return CreateDebugPasswordRequest.class;
  }

  @Override
  public Class<CreateDebugPasswordResponse> getResponseTypeClass() {
    return CreateDebugPasswordResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return CreateDebugPasswordRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return CreateDebugPasswordResponse.APPLICATION_MODEL_TYPE;
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
