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
import software.amazon.awssdk.aws.greengrass.model.VerifyClientDeviceIdentityRequest;
import software.amazon.awssdk.aws.greengrass.model.VerifyClientDeviceIdentityResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Verify client device credentials
 */
public class VerifyClientDeviceIdentityOperationContext implements OperationModelContext<VerifyClientDeviceIdentityRequest, VerifyClientDeviceIdentityResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.VERIFY_CLIENT_DEVICE_IDENTITY;
  }

  @Override
  public Class<VerifyClientDeviceIdentityRequest> getRequestTypeClass() {
    return VerifyClientDeviceIdentityRequest.class;
  }

  @Override
  public Class<VerifyClientDeviceIdentityResponse> getResponseTypeClass() {
    return VerifyClientDeviceIdentityResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return VerifyClientDeviceIdentityRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return VerifyClientDeviceIdentityResponse.APPLICATION_MODEL_TYPE;
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
