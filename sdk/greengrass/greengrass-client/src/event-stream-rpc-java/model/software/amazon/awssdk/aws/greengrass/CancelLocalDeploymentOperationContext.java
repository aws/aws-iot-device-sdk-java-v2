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
import software.amazon.awssdk.aws.greengrass.model.CancelLocalDeploymentRequest;
import software.amazon.awssdk.aws.greengrass.model.CancelLocalDeploymentResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Cancel a local deployment on the device.
 */
public class CancelLocalDeploymentOperationContext implements OperationModelContext<CancelLocalDeploymentRequest, CancelLocalDeploymentResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.CANCEL_LOCAL_DEPLOYMENT;
  }

  @Override
  public Class<CancelLocalDeploymentRequest> getRequestTypeClass() {
    return CancelLocalDeploymentRequest.class;
  }

  @Override
  public Class<CancelLocalDeploymentResponse> getResponseTypeClass() {
    return CancelLocalDeploymentResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return CancelLocalDeploymentRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return CancelLocalDeploymentResponse.APPLICATION_MODEL_TYPE;
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
