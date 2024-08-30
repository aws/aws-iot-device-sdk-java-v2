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
import software.amazon.awssdk.aws.greengrass.model.SubscribeToTopicRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToTopicResponse;
import software.amazon.awssdk.aws.greengrass.model.SubscriptionResponseMessage;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Creates a subscription for a custom topic
 */
public class SubscribeToTopicOperationContext implements OperationModelContext<SubscribeToTopicRequest, SubscribeToTopicResponse, EventStreamJsonMessage, SubscriptionResponseMessage> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.SUBSCRIBE_TO_TOPIC;
  }

  @Override
  public Class<SubscribeToTopicRequest> getRequestTypeClass() {
    return SubscribeToTopicRequest.class;
  }

  @Override
  public Class<SubscribeToTopicResponse> getResponseTypeClass() {
    return SubscribeToTopicResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return SubscribeToTopicRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return SubscribeToTopicResponse.APPLICATION_MODEL_TYPE;
  }

  @Override
  public Optional<Class<EventStreamJsonMessage>> getStreamingRequestTypeClass() {
    return Optional.empty();
  }

  @Override
  public Optional<Class<SubscriptionResponseMessage>> getStreamingResponseTypeClass() {
    return Optional.of(SubscriptionResponseMessage.class);
  }

  public Optional<String> getStreamingRequestApplicationModelType() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getStreamingResponseApplicationModelType() {
    return Optional.of(SubscriptionResponseMessage.APPLICATION_MODEL_TYPE);
  }
}
