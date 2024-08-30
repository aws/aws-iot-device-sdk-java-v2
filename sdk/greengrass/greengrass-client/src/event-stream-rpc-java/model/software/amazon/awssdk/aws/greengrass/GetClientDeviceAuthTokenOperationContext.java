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
import software.amazon.awssdk.aws.greengrass.model.GetClientDeviceAuthTokenRequest;
import software.amazon.awssdk.aws.greengrass.model.GetClientDeviceAuthTokenResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Get session token for a client device
 */
public class GetClientDeviceAuthTokenOperationContext implements OperationModelContext<GetClientDeviceAuthTokenRequest, GetClientDeviceAuthTokenResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.GET_CLIENT_DEVICE_AUTH_TOKEN;
  }

  @Override
  public Class<GetClientDeviceAuthTokenRequest> getRequestTypeClass() {
    return GetClientDeviceAuthTokenRequest.class;
  }

  @Override
  public Class<GetClientDeviceAuthTokenResponse> getResponseTypeClass() {
    return GetClientDeviceAuthTokenResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return GetClientDeviceAuthTokenRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return GetClientDeviceAuthTokenResponse.APPLICATION_MODEL_TYPE;
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
