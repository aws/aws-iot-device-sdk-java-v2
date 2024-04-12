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
import software.amazon.awssdk.aws.greengrass.model.CertificateUpdateEvent;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToCertificateUpdatesRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToCertificateUpdatesResponse;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Create a subscription for new certificates
 */
public class SubscribeToCertificateUpdatesOperationContext implements OperationModelContext<SubscribeToCertificateUpdatesRequest, SubscribeToCertificateUpdatesResponse, EventStreamJsonMessage, CertificateUpdateEvent> {
  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  @Override
  public String getOperationName() {
    return GreengrassCoreIPCServiceModel.SUBSCRIBE_TO_CERTIFICATE_UPDATES;
  }

  @Override
  public Class<SubscribeToCertificateUpdatesRequest> getRequestTypeClass() {
    return SubscribeToCertificateUpdatesRequest.class;
  }

  @Override
  public Class<SubscribeToCertificateUpdatesResponse> getResponseTypeClass() {
    return SubscribeToCertificateUpdatesResponse.class;
  }

  @Override
  public String getRequestApplicationModelType() {
    return SubscribeToCertificateUpdatesRequest.APPLICATION_MODEL_TYPE;
  }

  @Override
  public String getResponseApplicationModelType() {
    return SubscribeToCertificateUpdatesResponse.APPLICATION_MODEL_TYPE;
  }

  @Override
  public Optional<Class<EventStreamJsonMessage>> getStreamingRequestTypeClass() {
    return Optional.empty();
  }

  @Override
  public Optional<Class<CertificateUpdateEvent>> getStreamingResponseTypeClass() {
    return Optional.of(CertificateUpdateEvent.class);
  }

  public Optional<String> getStreamingRequestApplicationModelType() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getStreamingResponseApplicationModelType() {
    return Optional.of(CertificateUpdateEvent.APPLICATION_MODEL_TYPE);
  }
}
