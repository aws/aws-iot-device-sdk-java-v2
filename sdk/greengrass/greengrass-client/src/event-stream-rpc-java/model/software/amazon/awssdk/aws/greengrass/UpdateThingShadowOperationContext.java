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
import software.amazon.awssdk.aws.greengrass.model.UpdateThingShadowRequest;
import software.amazon.awssdk.aws.greengrass.model.UpdateThingShadowResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Updates a device shadow document stored in the local shadow service
 * The update is an upsert operation, with optimistic locking support
 */
public class UpdateThingShadowOperationContext implements OperationModelContext<UpdateThingShadowRequest, UpdateThingShadowResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.UPDATE_THING_SHADOW;
  }

  @Override
  public Class<UpdateThingShadowRequest> getRequestTypeClass() {
    return UpdateThingShadowRequest.class;
  }

  @Override
  public Class<UpdateThingShadowResponse> getResponseTypeClass() {
    return UpdateThingShadowResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return UpdateThingShadowRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return UpdateThingShadowResponse.APPLICATION_MODEL_TYPE;
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
