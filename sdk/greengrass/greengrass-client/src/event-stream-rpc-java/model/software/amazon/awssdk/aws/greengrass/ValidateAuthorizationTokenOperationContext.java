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
import software.amazon.awssdk.aws.greengrass.model.ValidateAuthorizationTokenRequest;
import software.amazon.awssdk.aws.greengrass.model.ValidateAuthorizationTokenResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Validate authorization token
 * NOTE This API can be used only by stream manager, customer component calling this API will receive UnauthorizedError
 */
public class ValidateAuthorizationTokenOperationContext implements OperationModelContext<ValidateAuthorizationTokenRequest, ValidateAuthorizationTokenResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.VALIDATE_AUTHORIZATION_TOKEN;
  }

  @Override
  public Class<ValidateAuthorizationTokenRequest> getRequestTypeClass() {
    return ValidateAuthorizationTokenRequest.class;
  }

  @Override
  public Class<ValidateAuthorizationTokenResponse> getResponseTypeClass() {
    return ValidateAuthorizationTokenResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return ValidateAuthorizationTokenRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return ValidateAuthorizationTokenResponse.APPLICATION_MODEL_TYPE;
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
