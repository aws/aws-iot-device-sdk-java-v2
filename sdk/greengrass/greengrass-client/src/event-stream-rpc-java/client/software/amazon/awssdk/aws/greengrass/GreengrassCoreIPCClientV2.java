/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass;

import java.io.IOException;
import java.lang.AutoCloseable;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.RuntimeException;
import java.lang.String;
import java.lang.Throwable;
import java.lang.Void;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.aws.greengrass.model.AuthorizeClientDeviceActionRequest;
import software.amazon.awssdk.aws.greengrass.model.AuthorizeClientDeviceActionResponse;
import software.amazon.awssdk.aws.greengrass.model.CancelLocalDeploymentRequest;
import software.amazon.awssdk.aws.greengrass.model.CancelLocalDeploymentResponse;
import software.amazon.awssdk.aws.greengrass.model.CertificateUpdateEvent;
import software.amazon.awssdk.aws.greengrass.model.ComponentUpdatePolicyEvents;
import software.amazon.awssdk.aws.greengrass.model.ConfigurationUpdateEvents;
import software.amazon.awssdk.aws.greengrass.model.CreateDebugPasswordRequest;
import software.amazon.awssdk.aws.greengrass.model.CreateDebugPasswordResponse;
import software.amazon.awssdk.aws.greengrass.model.CreateLocalDeploymentRequest;
import software.amazon.awssdk.aws.greengrass.model.CreateLocalDeploymentResponse;
import software.amazon.awssdk.aws.greengrass.model.DeferComponentUpdateRequest;
import software.amazon.awssdk.aws.greengrass.model.DeferComponentUpdateResponse;
import software.amazon.awssdk.aws.greengrass.model.DeleteThingShadowRequest;
import software.amazon.awssdk.aws.greengrass.model.DeleteThingShadowResponse;
import software.amazon.awssdk.aws.greengrass.model.GetClientDeviceAuthTokenRequest;
import software.amazon.awssdk.aws.greengrass.model.GetClientDeviceAuthTokenResponse;
import software.amazon.awssdk.aws.greengrass.model.GetComponentDetailsRequest;
import software.amazon.awssdk.aws.greengrass.model.GetComponentDetailsResponse;
import software.amazon.awssdk.aws.greengrass.model.GetConfigurationRequest;
import software.amazon.awssdk.aws.greengrass.model.GetConfigurationResponse;
import software.amazon.awssdk.aws.greengrass.model.GetLocalDeploymentStatusRequest;
import software.amazon.awssdk.aws.greengrass.model.GetLocalDeploymentStatusResponse;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueRequest;
import software.amazon.awssdk.aws.greengrass.model.GetSecretValueResponse;
import software.amazon.awssdk.aws.greengrass.model.GetThingShadowRequest;
import software.amazon.awssdk.aws.greengrass.model.GetThingShadowResponse;
import software.amazon.awssdk.aws.greengrass.model.IoTCoreMessage;
import software.amazon.awssdk.aws.greengrass.model.ListComponentsRequest;
import software.amazon.awssdk.aws.greengrass.model.ListComponentsResponse;
import software.amazon.awssdk.aws.greengrass.model.ListLocalDeploymentsRequest;
import software.amazon.awssdk.aws.greengrass.model.ListLocalDeploymentsResponse;
import software.amazon.awssdk.aws.greengrass.model.ListNamedShadowsForThingRequest;
import software.amazon.awssdk.aws.greengrass.model.ListNamedShadowsForThingResponse;
import software.amazon.awssdk.aws.greengrass.model.PauseComponentRequest;
import software.amazon.awssdk.aws.greengrass.model.PauseComponentResponse;
import software.amazon.awssdk.aws.greengrass.model.PublishToIoTCoreRequest;
import software.amazon.awssdk.aws.greengrass.model.PublishToIoTCoreResponse;
import software.amazon.awssdk.aws.greengrass.model.PublishToTopicRequest;
import software.amazon.awssdk.aws.greengrass.model.PublishToTopicResponse;
import software.amazon.awssdk.aws.greengrass.model.PutComponentMetricRequest;
import software.amazon.awssdk.aws.greengrass.model.PutComponentMetricResponse;
import software.amazon.awssdk.aws.greengrass.model.RestartComponentRequest;
import software.amazon.awssdk.aws.greengrass.model.RestartComponentResponse;
import software.amazon.awssdk.aws.greengrass.model.ResumeComponentRequest;
import software.amazon.awssdk.aws.greengrass.model.ResumeComponentResponse;
import software.amazon.awssdk.aws.greengrass.model.SendConfigurationValidityReportRequest;
import software.amazon.awssdk.aws.greengrass.model.SendConfigurationValidityReportResponse;
import software.amazon.awssdk.aws.greengrass.model.StopComponentRequest;
import software.amazon.awssdk.aws.greengrass.model.StopComponentResponse;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToCertificateUpdatesRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToCertificateUpdatesResponse;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToComponentUpdatesRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToComponentUpdatesResponse;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToConfigurationUpdateRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToConfigurationUpdateResponse;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToIoTCoreRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToIoTCoreResponse;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToTopicRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToTopicResponse;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToValidateConfigurationUpdatesRequest;
import software.amazon.awssdk.aws.greengrass.model.SubscribeToValidateConfigurationUpdatesResponse;
import software.amazon.awssdk.aws.greengrass.model.SubscriptionResponseMessage;
import software.amazon.awssdk.aws.greengrass.model.UpdateConfigurationRequest;
import software.amazon.awssdk.aws.greengrass.model.UpdateConfigurationResponse;
import software.amazon.awssdk.aws.greengrass.model.UpdateStateRequest;
import software.amazon.awssdk.aws.greengrass.model.UpdateStateResponse;
import software.amazon.awssdk.aws.greengrass.model.UpdateThingShadowRequest;
import software.amazon.awssdk.aws.greengrass.model.UpdateThingShadowResponse;
import software.amazon.awssdk.aws.greengrass.model.ValidateAuthorizationTokenRequest;
import software.amazon.awssdk.aws.greengrass.model.ValidateAuthorizationTokenResponse;
import software.amazon.awssdk.aws.greengrass.model.ValidateConfigurationUpdateEvents;
import software.amazon.awssdk.aws.greengrass.model.VerifyClientDeviceIdentityRequest;
import software.amazon.awssdk.aws.greengrass.model.VerifyClientDeviceIdentityResponse;
import software.amazon.awssdk.crt.io.ClientBootstrap;
import software.amazon.awssdk.crt.io.EventLoopGroup;
import software.amazon.awssdk.crt.io.SocketOptions;
import software.amazon.awssdk.crt.io.SocketOptions.SocketDomain;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCConnection;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCConnectionConfig;
import software.amazon.awssdk.eventstreamrpc.GreengrassConnectMessageSupplier;
import software.amazon.awssdk.eventstreamrpc.StreamResponseHandler;

/**
 * V2 Client for Greengrass.
 */
public class GreengrassCoreIPCClientV2 implements AutoCloseable {
  protected GreengrassCoreIPC client;

  protected Executor executor;

  protected EventStreamRPCConnection connection;

  GreengrassCoreIPCClientV2(GreengrassCoreIPC client, EventStreamRPCConnection connection,
      Executor executor) {
    this.client = client;
    this.connection = connection;
    this.executor = executor;
  }

  @Override
  public void close() throws Exception {
    if (client instanceof AutoCloseable) {
      ((AutoCloseable) client).close();
    }
    if (connection != null) {
      connection.close();
    }
  }

  public GreengrassCoreIPC getClient() {
    return client;
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Perform the authorizeClientDeviceAction operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public AuthorizeClientDeviceActionResponse authorizeClientDeviceAction(
      final AuthorizeClientDeviceActionRequest request) throws InterruptedException {
    return getResponse(this.authorizeClientDeviceActionAsync(request));
  }

  /**
   * Perform the authorizeClientDeviceAction operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<AuthorizeClientDeviceActionResponse> authorizeClientDeviceActionAsync(
      final AuthorizeClientDeviceActionRequest request) {
    return client.authorizeClientDeviceAction(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the cancelLocalDeployment operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public CancelLocalDeploymentResponse cancelLocalDeployment(
      final CancelLocalDeploymentRequest request) throws InterruptedException {
    return getResponse(this.cancelLocalDeploymentAsync(request));
  }

  /**
   * Perform the cancelLocalDeployment operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<CancelLocalDeploymentResponse> cancelLocalDeploymentAsync(
      final CancelLocalDeploymentRequest request) {
    return client.cancelLocalDeployment(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the createDebugPassword operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public CreateDebugPasswordResponse createDebugPassword(final CreateDebugPasswordRequest request)
      throws InterruptedException {
    return getResponse(this.createDebugPasswordAsync(request));
  }

  /**
   * Perform the createDebugPassword operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<CreateDebugPasswordResponse> createDebugPasswordAsync(
      final CreateDebugPasswordRequest request) {
    return client.createDebugPassword(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the createLocalDeployment operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public CreateLocalDeploymentResponse createLocalDeployment(
      final CreateLocalDeploymentRequest request) throws InterruptedException {
    return getResponse(this.createLocalDeploymentAsync(request));
  }

  /**
   * Perform the createLocalDeployment operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<CreateLocalDeploymentResponse> createLocalDeploymentAsync(
      final CreateLocalDeploymentRequest request) {
    return client.createLocalDeployment(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the deferComponentUpdate operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public DeferComponentUpdateResponse deferComponentUpdate(
      final DeferComponentUpdateRequest request) throws InterruptedException {
    return getResponse(this.deferComponentUpdateAsync(request));
  }

  /**
   * Perform the deferComponentUpdate operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<DeferComponentUpdateResponse> deferComponentUpdateAsync(
      final DeferComponentUpdateRequest request) {
    return client.deferComponentUpdate(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the deleteThingShadow operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public DeleteThingShadowResponse deleteThingShadow(final DeleteThingShadowRequest request) throws
      InterruptedException {
    return getResponse(this.deleteThingShadowAsync(request));
  }

  /**
   * Perform the deleteThingShadow operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<DeleteThingShadowResponse> deleteThingShadowAsync(
      final DeleteThingShadowRequest request) {
    return client.deleteThingShadow(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the getClientDeviceAuthToken operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public GetClientDeviceAuthTokenResponse getClientDeviceAuthToken(
      final GetClientDeviceAuthTokenRequest request) throws InterruptedException {
    return getResponse(this.getClientDeviceAuthTokenAsync(request));
  }

  /**
   * Perform the getClientDeviceAuthToken operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<GetClientDeviceAuthTokenResponse> getClientDeviceAuthTokenAsync(
      final GetClientDeviceAuthTokenRequest request) {
    return client.getClientDeviceAuthToken(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the getComponentDetails operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public GetComponentDetailsResponse getComponentDetails(final GetComponentDetailsRequest request)
      throws InterruptedException {
    return getResponse(this.getComponentDetailsAsync(request));
  }

  /**
   * Perform the getComponentDetails operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<GetComponentDetailsResponse> getComponentDetailsAsync(
      final GetComponentDetailsRequest request) {
    return client.getComponentDetails(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the getConfiguration operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public GetConfigurationResponse getConfiguration(final GetConfigurationRequest request) throws
      InterruptedException {
    return getResponse(this.getConfigurationAsync(request));
  }

  /**
   * Perform the getConfiguration operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<GetConfigurationResponse> getConfigurationAsync(
      final GetConfigurationRequest request) {
    return client.getConfiguration(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the getLocalDeploymentStatus operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public GetLocalDeploymentStatusResponse getLocalDeploymentStatus(
      final GetLocalDeploymentStatusRequest request) throws InterruptedException {
    return getResponse(this.getLocalDeploymentStatusAsync(request));
  }

  /**
   * Perform the getLocalDeploymentStatus operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<GetLocalDeploymentStatusResponse> getLocalDeploymentStatusAsync(
      final GetLocalDeploymentStatusRequest request) {
    return client.getLocalDeploymentStatus(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the getSecretValue operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public GetSecretValueResponse getSecretValue(final GetSecretValueRequest request) throws
      InterruptedException {
    return getResponse(this.getSecretValueAsync(request));
  }

  /**
   * Perform the getSecretValue operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<GetSecretValueResponse> getSecretValueAsync(
      final GetSecretValueRequest request) {
    return client.getSecretValue(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the getThingShadow operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public GetThingShadowResponse getThingShadow(final GetThingShadowRequest request) throws
      InterruptedException {
    return getResponse(this.getThingShadowAsync(request));
  }

  /**
   * Perform the getThingShadow operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<GetThingShadowResponse> getThingShadowAsync(
      final GetThingShadowRequest request) {
    return client.getThingShadow(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the listComponents operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public ListComponentsResponse listComponents(final ListComponentsRequest request) throws
      InterruptedException {
    return getResponse(this.listComponentsAsync(request));
  }

  /**
   * Perform the listComponents operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<ListComponentsResponse> listComponentsAsync(
      final ListComponentsRequest request) {
    return client.listComponents(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the listLocalDeployments operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public ListLocalDeploymentsResponse listLocalDeployments(
      final ListLocalDeploymentsRequest request) throws InterruptedException {
    return getResponse(this.listLocalDeploymentsAsync(request));
  }

  /**
   * Perform the listLocalDeployments operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<ListLocalDeploymentsResponse> listLocalDeploymentsAsync(
      final ListLocalDeploymentsRequest request) {
    return client.listLocalDeployments(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the listNamedShadowsForThing operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public ListNamedShadowsForThingResponse listNamedShadowsForThing(
      final ListNamedShadowsForThingRequest request) throws InterruptedException {
    return getResponse(this.listNamedShadowsForThingAsync(request));
  }

  /**
   * Perform the listNamedShadowsForThing operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<ListNamedShadowsForThingResponse> listNamedShadowsForThingAsync(
      final ListNamedShadowsForThingRequest request) {
    return client.listNamedShadowsForThing(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the pauseComponent operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public PauseComponentResponse pauseComponent(final PauseComponentRequest request) throws
      InterruptedException {
    return getResponse(this.pauseComponentAsync(request));
  }

  /**
   * Perform the pauseComponent operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<PauseComponentResponse> pauseComponentAsync(
      final PauseComponentRequest request) {
    return client.pauseComponent(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the publishToIoTCore operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public PublishToIoTCoreResponse publishToIoTCore(final PublishToIoTCoreRequest request) throws
      InterruptedException {
    return getResponse(this.publishToIoTCoreAsync(request));
  }

  /**
   * Perform the publishToIoTCore operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<PublishToIoTCoreResponse> publishToIoTCoreAsync(
      final PublishToIoTCoreRequest request) {
    return client.publishToIoTCore(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the publishToTopic operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public PublishToTopicResponse publishToTopic(final PublishToTopicRequest request) throws
      InterruptedException {
    return getResponse(this.publishToTopicAsync(request));
  }

  /**
   * Perform the publishToTopic operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<PublishToTopicResponse> publishToTopicAsync(
      final PublishToTopicRequest request) {
    return client.publishToTopic(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the putComponentMetric operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public PutComponentMetricResponse putComponentMetric(final PutComponentMetricRequest request)
      throws InterruptedException {
    return getResponse(this.putComponentMetricAsync(request));
  }

  /**
   * Perform the putComponentMetric operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<PutComponentMetricResponse> putComponentMetricAsync(
      final PutComponentMetricRequest request) {
    return client.putComponentMetric(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the restartComponent operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public RestartComponentResponse restartComponent(final RestartComponentRequest request) throws
      InterruptedException {
    return getResponse(this.restartComponentAsync(request));
  }

  /**
   * Perform the restartComponent operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<RestartComponentResponse> restartComponentAsync(
      final RestartComponentRequest request) {
    return client.restartComponent(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the resumeComponent operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public ResumeComponentResponse resumeComponent(final ResumeComponentRequest request) throws
      InterruptedException {
    return getResponse(this.resumeComponentAsync(request));
  }

  /**
   * Perform the resumeComponent operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<ResumeComponentResponse> resumeComponentAsync(
      final ResumeComponentRequest request) {
    return client.resumeComponent(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the sendConfigurationValidityReport operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public SendConfigurationValidityReportResponse sendConfigurationValidityReport(
      final SendConfigurationValidityReportRequest request) throws InterruptedException {
    return getResponse(this.sendConfigurationValidityReportAsync(request));
  }

  /**
   * Perform the sendConfigurationValidityReport operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<SendConfigurationValidityReportResponse> sendConfigurationValidityReportAsync(
      final SendConfigurationValidityReportRequest request) {
    return client.sendConfigurationValidityReport(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the stopComponent operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public StopComponentResponse stopComponent(final StopComponentRequest request) throws
      InterruptedException {
    return getResponse(this.stopComponentAsync(request));
  }

  /**
   * Perform the stopComponent operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<StopComponentResponse> stopComponentAsync(
      final StopComponentRequest request) {
    return client.stopComponent(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the subscribeToCertificateUpdates operation asynchronously.
   * The initial response or error will be returned as the result of the asynchronous future, further events will
   * arrive via the streaming callbacks.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   * @param onStreamEvent Callback for stream events. If an executor is provided, this method will run in the executor.
   * @param onStreamError Callback for stream errors. Return true to close the stream,
   *     return false to keep the stream open. Even if an executor is provided,
   *     this method will not run in the executor.
   * @param onStreamClosed Callback for when the stream closes. If an executor is provided, this method will run in the executor.
   */
  public StreamingResponse<CompletableFuture<SubscribeToCertificateUpdatesResponse>, SubscribeToCertificateUpdatesResponseHandler> subscribeToCertificateUpdatesAsync(
      final SubscribeToCertificateUpdatesRequest request,
      Consumer<CertificateUpdateEvent> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed) {
    return this.subscribeToCertificateUpdatesAsync(request, getStreamingResponseHandler(onStreamEvent, onStreamError, onStreamClosed));
  }

  /**
   * Perform the subscribeToCertificateUpdates operation synchronously.
   * The initial response or error will be returned synchronously,
   * further events will arrive via the streaming callbacks.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   * @param onStreamEvent Callback for stream events. If an executor is provided, this method will run in the executor.
   * @param onStreamError Callback for stream errors. Return true to close the stream,
   *     return false to keep the stream open. Even if an executor is provided,
   *     this method will not run in the executor.
   * @param onStreamClosed Callback for when the stream closes. If an executor is provided, this method will run in the executor.
   */
  public StreamingResponse<SubscribeToCertificateUpdatesResponse, SubscribeToCertificateUpdatesResponseHandler> subscribeToCertificateUpdates(
      final SubscribeToCertificateUpdatesRequest request,
      Consumer<CertificateUpdateEvent> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed)
      throws InterruptedException {
    StreamingResponse<CompletableFuture<SubscribeToCertificateUpdatesResponse>, SubscribeToCertificateUpdatesResponseHandler> r = this.subscribeToCertificateUpdatesAsync(request, onStreamEvent, onStreamError, onStreamClosed);
    return new StreamingResponse<>(getResponse(r.getResponse()), r.getHandler());
  }

  /**
   * Perform the subscribeToCertificateUpdates operation synchronously.
   * The initial response or error will be returned synchronously, further events will
   * arrive via the streaming callbacks.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   * @param streamResponseHandler Methods on this object will be called as stream events happen on this operation.
   *     If an executor is provided, the onStreamEvent and onStreamClosed methods will run in the executor.
   */
  public StreamingResponse<SubscribeToCertificateUpdatesResponse, SubscribeToCertificateUpdatesResponseHandler> subscribeToCertificateUpdates(
      final SubscribeToCertificateUpdatesRequest request,
      final StreamResponseHandler<CertificateUpdateEvent> streamResponseHandler) throws
      InterruptedException {
    StreamingResponse<CompletableFuture<SubscribeToCertificateUpdatesResponse>, SubscribeToCertificateUpdatesResponseHandler> r = this.subscribeToCertificateUpdatesAsync(request, streamResponseHandler);
    return new StreamingResponse<>(getResponse(r.getResponse()), r.getHandler());
  }

  /**
   * Perform the subscribeToCertificateUpdates operation asynchronously.
   * The initial response or error will be returned as the result of the asynchronous future, further events will
   * arrive via the streaming callbacks.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   * @param streamResponseHandler Methods on this object will be called as stream events happen on this operation.
   *     If an executor is provided, the onStreamEvent and onStreamClosed methods will run in the executor.
   */
  public StreamingResponse<CompletableFuture<SubscribeToCertificateUpdatesResponse>, SubscribeToCertificateUpdatesResponseHandler> subscribeToCertificateUpdatesAsync(
      final SubscribeToCertificateUpdatesRequest request,
      final StreamResponseHandler<CertificateUpdateEvent> streamResponseHandler) {
    SubscribeToCertificateUpdatesResponseHandler r = client.subscribeToCertificateUpdates(request, Optional.ofNullable(getStreamingResponseHandler(streamResponseHandler)));
    return new StreamingResponse<>(r.getResponse(), r);
  }

  /**
   * Perform the subscribeToComponentUpdates operation asynchronously.
   * The initial response or error will be returned as the result of the asynchronous future, further events will
   * arrive via the streaming callbacks.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   * @param onStreamEvent Callback for stream events. If an executor is provided, this method will run in the executor.
   * @param onStreamError Callback for stream errors. Return true to close the stream,
   *     return false to keep the stream open. Even if an executor is provided,
   *     this method will not run in the executor.
   * @param onStreamClosed Callback for when the stream closes. If an executor is provided, this method will run in the executor.
   */
  public StreamingResponse<CompletableFuture<SubscribeToComponentUpdatesResponse>, SubscribeToComponentUpdatesResponseHandler> subscribeToComponentUpdatesAsync(
      final SubscribeToComponentUpdatesRequest request,
      Consumer<ComponentUpdatePolicyEvents> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed) {
    return this.subscribeToComponentUpdatesAsync(request, getStreamingResponseHandler(onStreamEvent, onStreamError, onStreamClosed));
  }

  /**
   * Perform the subscribeToComponentUpdates operation synchronously.
   * The initial response or error will be returned synchronously,
   * further events will arrive via the streaming callbacks.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   * @param onStreamEvent Callback for stream events. If an executor is provided, this method will run in the executor.
   * @param onStreamError Callback for stream errors. Return true to close the stream,
   *     return false to keep the stream open. Even if an executor is provided,
   *     this method will not run in the executor.
   * @param onStreamClosed Callback for when the stream closes. If an executor is provided, this method will run in the executor.
   */
  public StreamingResponse<SubscribeToComponentUpdatesResponse, SubscribeToComponentUpdatesResponseHandler> subscribeToComponentUpdates(
      final SubscribeToComponentUpdatesRequest request,
      Consumer<ComponentUpdatePolicyEvents> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed)
      throws InterruptedException {
    StreamingResponse<CompletableFuture<SubscribeToComponentUpdatesResponse>, SubscribeToComponentUpdatesResponseHandler> r = this.subscribeToComponentUpdatesAsync(request, onStreamEvent, onStreamError, onStreamClosed);
    return new StreamingResponse<>(getResponse(r.getResponse()), r.getHandler());
  }

  /**
   * Perform the subscribeToComponentUpdates operation synchronously.
   * The initial response or error will be returned synchronously, further events will
   * arrive via the streaming callbacks.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   * @param streamResponseHandler Methods on this object will be called as stream events happen on this operation.
   *     If an executor is provided, the onStreamEvent and onStreamClosed methods will run in the executor.
   */
  public StreamingResponse<SubscribeToComponentUpdatesResponse, SubscribeToComponentUpdatesResponseHandler> subscribeToComponentUpdates(
      final SubscribeToComponentUpdatesRequest request,
      final StreamResponseHandler<ComponentUpdatePolicyEvents> streamResponseHandler) throws
      InterruptedException {
    StreamingResponse<CompletableFuture<SubscribeToComponentUpdatesResponse>, SubscribeToComponentUpdatesResponseHandler> r = this.subscribeToComponentUpdatesAsync(request, streamResponseHandler);
    return new StreamingResponse<>(getResponse(r.getResponse()), r.getHandler());
  }

  /**
   * Perform the subscribeToComponentUpdates operation asynchronously.
   * The initial response or error will be returned as the result of the asynchronous future, further events will
   * arrive via the streaming callbacks.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   * @param streamResponseHandler Methods on this object will be called as stream events happen on this operation.
   *     If an executor is provided, the onStreamEvent and onStreamClosed methods will run in the executor.
   */
  public StreamingResponse<CompletableFuture<SubscribeToComponentUpdatesResponse>, SubscribeToComponentUpdatesResponseHandler> subscribeToComponentUpdatesAsync(
      final SubscribeToComponentUpdatesRequest request,
      final StreamResponseHandler<ComponentUpdatePolicyEvents> streamResponseHandler) {
    SubscribeToComponentUpdatesResponseHandler r = client.subscribeToComponentUpdates(request, Optional.ofNullable(getStreamingResponseHandler(streamResponseHandler)));
    return new StreamingResponse<>(r.getResponse(), r);
  }

  /**
   * Perform the subscribeToConfigurationUpdate operation asynchronously.
   * The initial response or error will be returned as the result of the asynchronous future, further events will
   * arrive via the streaming callbacks.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   * @param onStreamEvent Callback for stream events. If an executor is provided, this method will run in the executor.
   * @param onStreamError Callback for stream errors. Return true to close the stream,
   *     return false to keep the stream open. Even if an executor is provided,
   *     this method will not run in the executor.
   * @param onStreamClosed Callback for when the stream closes. If an executor is provided, this method will run in the executor.
   */
  public StreamingResponse<CompletableFuture<SubscribeToConfigurationUpdateResponse>, SubscribeToConfigurationUpdateResponseHandler> subscribeToConfigurationUpdateAsync(
      final SubscribeToConfigurationUpdateRequest request,
      Consumer<ConfigurationUpdateEvents> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed) {
    return this.subscribeToConfigurationUpdateAsync(request, getStreamingResponseHandler(onStreamEvent, onStreamError, onStreamClosed));
  }

  /**
   * Perform the subscribeToConfigurationUpdate operation synchronously.
   * The initial response or error will be returned synchronously,
   * further events will arrive via the streaming callbacks.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   * @param onStreamEvent Callback for stream events. If an executor is provided, this method will run in the executor.
   * @param onStreamError Callback for stream errors. Return true to close the stream,
   *     return false to keep the stream open. Even if an executor is provided,
   *     this method will not run in the executor.
   * @param onStreamClosed Callback for when the stream closes. If an executor is provided, this method will run in the executor.
   */
  public StreamingResponse<SubscribeToConfigurationUpdateResponse, SubscribeToConfigurationUpdateResponseHandler> subscribeToConfigurationUpdate(
      final SubscribeToConfigurationUpdateRequest request,
      Consumer<ConfigurationUpdateEvents> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed)
      throws InterruptedException {
    StreamingResponse<CompletableFuture<SubscribeToConfigurationUpdateResponse>, SubscribeToConfigurationUpdateResponseHandler> r = this.subscribeToConfigurationUpdateAsync(request, onStreamEvent, onStreamError, onStreamClosed);
    return new StreamingResponse<>(getResponse(r.getResponse()), r.getHandler());
  }

  /**
   * Perform the subscribeToConfigurationUpdate operation synchronously.
   * The initial response or error will be returned synchronously, further events will
   * arrive via the streaming callbacks.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   * @param streamResponseHandler Methods on this object will be called as stream events happen on this operation.
   *     If an executor is provided, the onStreamEvent and onStreamClosed methods will run in the executor.
   */
  public StreamingResponse<SubscribeToConfigurationUpdateResponse, SubscribeToConfigurationUpdateResponseHandler> subscribeToConfigurationUpdate(
      final SubscribeToConfigurationUpdateRequest request,
      final StreamResponseHandler<ConfigurationUpdateEvents> streamResponseHandler) throws
      InterruptedException {
    StreamingResponse<CompletableFuture<SubscribeToConfigurationUpdateResponse>, SubscribeToConfigurationUpdateResponseHandler> r = this.subscribeToConfigurationUpdateAsync(request, streamResponseHandler);
    return new StreamingResponse<>(getResponse(r.getResponse()), r.getHandler());
  }

  /**
   * Perform the subscribeToConfigurationUpdate operation asynchronously.
   * The initial response or error will be returned as the result of the asynchronous future, further events will
   * arrive via the streaming callbacks.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   * @param streamResponseHandler Methods on this object will be called as stream events happen on this operation.
   *     If an executor is provided, the onStreamEvent and onStreamClosed methods will run in the executor.
   */
  public StreamingResponse<CompletableFuture<SubscribeToConfigurationUpdateResponse>, SubscribeToConfigurationUpdateResponseHandler> subscribeToConfigurationUpdateAsync(
      final SubscribeToConfigurationUpdateRequest request,
      final StreamResponseHandler<ConfigurationUpdateEvents> streamResponseHandler) {
    SubscribeToConfigurationUpdateResponseHandler r = client.subscribeToConfigurationUpdate(request, Optional.ofNullable(getStreamingResponseHandler(streamResponseHandler)));
    return new StreamingResponse<>(r.getResponse(), r);
  }

  /**
   * Perform the subscribeToIoTCore operation asynchronously.
   * The initial response or error will be returned as the result of the asynchronous future, further events will
   * arrive via the streaming callbacks.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   * @param onStreamEvent Callback for stream events. If an executor is provided, this method will run in the executor.
   * @param onStreamError Callback for stream errors. Return true to close the stream,
   *     return false to keep the stream open. Even if an executor is provided,
   *     this method will not run in the executor.
   * @param onStreamClosed Callback for when the stream closes. If an executor is provided, this method will run in the executor.
   */
  public StreamingResponse<CompletableFuture<SubscribeToIoTCoreResponse>, SubscribeToIoTCoreResponseHandler> subscribeToIoTCoreAsync(
      final SubscribeToIoTCoreRequest request, Consumer<IoTCoreMessage> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed) {
    return this.subscribeToIoTCoreAsync(request, getStreamingResponseHandler(onStreamEvent, onStreamError, onStreamClosed));
  }

  /**
   * Perform the subscribeToIoTCore operation synchronously.
   * The initial response or error will be returned synchronously,
   * further events will arrive via the streaming callbacks.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   * @param onStreamEvent Callback for stream events. If an executor is provided, this method will run in the executor.
   * @param onStreamError Callback for stream errors. Return true to close the stream,
   *     return false to keep the stream open. Even if an executor is provided,
   *     this method will not run in the executor.
   * @param onStreamClosed Callback for when the stream closes. If an executor is provided, this method will run in the executor.
   */
  public StreamingResponse<SubscribeToIoTCoreResponse, SubscribeToIoTCoreResponseHandler> subscribeToIoTCore(
      final SubscribeToIoTCoreRequest request, Consumer<IoTCoreMessage> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed)
      throws InterruptedException {
    StreamingResponse<CompletableFuture<SubscribeToIoTCoreResponse>, SubscribeToIoTCoreResponseHandler> r = this.subscribeToIoTCoreAsync(request, onStreamEvent, onStreamError, onStreamClosed);
    return new StreamingResponse<>(getResponse(r.getResponse()), r.getHandler());
  }

  /**
   * Perform the subscribeToIoTCore operation synchronously.
   * The initial response or error will be returned synchronously, further events will
   * arrive via the streaming callbacks.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   * @param streamResponseHandler Methods on this object will be called as stream events happen on this operation.
   *     If an executor is provided, the onStreamEvent and onStreamClosed methods will run in the executor.
   */
  public StreamingResponse<SubscribeToIoTCoreResponse, SubscribeToIoTCoreResponseHandler> subscribeToIoTCore(
      final SubscribeToIoTCoreRequest request,
      final StreamResponseHandler<IoTCoreMessage> streamResponseHandler) throws
      InterruptedException {
    StreamingResponse<CompletableFuture<SubscribeToIoTCoreResponse>, SubscribeToIoTCoreResponseHandler> r = this.subscribeToIoTCoreAsync(request, streamResponseHandler);
    return new StreamingResponse<>(getResponse(r.getResponse()), r.getHandler());
  }

  /**
   * Perform the subscribeToIoTCore operation asynchronously.
   * The initial response or error will be returned as the result of the asynchronous future, further events will
   * arrive via the streaming callbacks.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   * @param streamResponseHandler Methods on this object will be called as stream events happen on this operation.
   *     If an executor is provided, the onStreamEvent and onStreamClosed methods will run in the executor.
   */
  public StreamingResponse<CompletableFuture<SubscribeToIoTCoreResponse>, SubscribeToIoTCoreResponseHandler> subscribeToIoTCoreAsync(
      final SubscribeToIoTCoreRequest request,
      final StreamResponseHandler<IoTCoreMessage> streamResponseHandler) {
    SubscribeToIoTCoreResponseHandler r = client.subscribeToIoTCore(request, Optional.ofNullable(getStreamingResponseHandler(streamResponseHandler)));
    return new StreamingResponse<>(r.getResponse(), r);
  }

  /**
   * Perform the subscribeToTopic operation asynchronously.
   * The initial response or error will be returned as the result of the asynchronous future, further events will
   * arrive via the streaming callbacks.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   * @param onStreamEvent Callback for stream events. If an executor is provided, this method will run in the executor.
   * @param onStreamError Callback for stream errors. Return true to close the stream,
   *     return false to keep the stream open. Even if an executor is provided,
   *     this method will not run in the executor.
   * @param onStreamClosed Callback for when the stream closes. If an executor is provided, this method will run in the executor.
   */
  public StreamingResponse<CompletableFuture<SubscribeToTopicResponse>, SubscribeToTopicResponseHandler> subscribeToTopicAsync(
      final SubscribeToTopicRequest request, Consumer<SubscriptionResponseMessage> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed) {
    return this.subscribeToTopicAsync(request, getStreamingResponseHandler(onStreamEvent, onStreamError, onStreamClosed));
  }

  /**
   * Perform the subscribeToTopic operation synchronously.
   * The initial response or error will be returned synchronously,
   * further events will arrive via the streaming callbacks.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   * @param onStreamEvent Callback for stream events. If an executor is provided, this method will run in the executor.
   * @param onStreamError Callback for stream errors. Return true to close the stream,
   *     return false to keep the stream open. Even if an executor is provided,
   *     this method will not run in the executor.
   * @param onStreamClosed Callback for when the stream closes. If an executor is provided, this method will run in the executor.
   */
  public StreamingResponse<SubscribeToTopicResponse, SubscribeToTopicResponseHandler> subscribeToTopic(
      final SubscribeToTopicRequest request, Consumer<SubscriptionResponseMessage> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed)
      throws InterruptedException {
    StreamingResponse<CompletableFuture<SubscribeToTopicResponse>, SubscribeToTopicResponseHandler> r = this.subscribeToTopicAsync(request, onStreamEvent, onStreamError, onStreamClosed);
    return new StreamingResponse<>(getResponse(r.getResponse()), r.getHandler());
  }

  /**
   * Perform the subscribeToTopic operation synchronously.
   * The initial response or error will be returned synchronously, further events will
   * arrive via the streaming callbacks.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   * @param streamResponseHandler Methods on this object will be called as stream events happen on this operation.
   *     If an executor is provided, the onStreamEvent and onStreamClosed methods will run in the executor.
   */
  public StreamingResponse<SubscribeToTopicResponse, SubscribeToTopicResponseHandler> subscribeToTopic(
      final SubscribeToTopicRequest request,
      final StreamResponseHandler<SubscriptionResponseMessage> streamResponseHandler) throws
      InterruptedException {
    StreamingResponse<CompletableFuture<SubscribeToTopicResponse>, SubscribeToTopicResponseHandler> r = this.subscribeToTopicAsync(request, streamResponseHandler);
    return new StreamingResponse<>(getResponse(r.getResponse()), r.getHandler());
  }

  /**
   * Perform the subscribeToTopic operation asynchronously.
   * The initial response or error will be returned as the result of the asynchronous future, further events will
   * arrive via the streaming callbacks.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   * @param streamResponseHandler Methods on this object will be called as stream events happen on this operation.
   *     If an executor is provided, the onStreamEvent and onStreamClosed methods will run in the executor.
   */
  public StreamingResponse<CompletableFuture<SubscribeToTopicResponse>, SubscribeToTopicResponseHandler> subscribeToTopicAsync(
      final SubscribeToTopicRequest request,
      final StreamResponseHandler<SubscriptionResponseMessage> streamResponseHandler) {
    SubscribeToTopicResponseHandler r = client.subscribeToTopic(request, Optional.ofNullable(getStreamingResponseHandler(streamResponseHandler)));
    return new StreamingResponse<>(r.getResponse(), r);
  }

  /**
   * Perform the subscribeToValidateConfigurationUpdates operation asynchronously.
   * The initial response or error will be returned as the result of the asynchronous future, further events will
   * arrive via the streaming callbacks.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   * @param onStreamEvent Callback for stream events. If an executor is provided, this method will run in the executor.
   * @param onStreamError Callback for stream errors. Return true to close the stream,
   *     return false to keep the stream open. Even if an executor is provided,
   *     this method will not run in the executor.
   * @param onStreamClosed Callback for when the stream closes. If an executor is provided, this method will run in the executor.
   */
  public StreamingResponse<CompletableFuture<SubscribeToValidateConfigurationUpdatesResponse>, SubscribeToValidateConfigurationUpdatesResponseHandler> subscribeToValidateConfigurationUpdatesAsync(
      final SubscribeToValidateConfigurationUpdatesRequest request,
      Consumer<ValidateConfigurationUpdateEvents> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed) {
    return this.subscribeToValidateConfigurationUpdatesAsync(request, getStreamingResponseHandler(onStreamEvent, onStreamError, onStreamClosed));
  }

  /**
   * Perform the subscribeToValidateConfigurationUpdates operation synchronously.
   * The initial response or error will be returned synchronously,
   * further events will arrive via the streaming callbacks.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   * @param onStreamEvent Callback for stream events. If an executor is provided, this method will run in the executor.
   * @param onStreamError Callback for stream errors. Return true to close the stream,
   *     return false to keep the stream open. Even if an executor is provided,
   *     this method will not run in the executor.
   * @param onStreamClosed Callback for when the stream closes. If an executor is provided, this method will run in the executor.
   */
  public StreamingResponse<SubscribeToValidateConfigurationUpdatesResponse, SubscribeToValidateConfigurationUpdatesResponseHandler> subscribeToValidateConfigurationUpdates(
      final SubscribeToValidateConfigurationUpdatesRequest request,
      Consumer<ValidateConfigurationUpdateEvents> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed)
      throws InterruptedException {
    StreamingResponse<CompletableFuture<SubscribeToValidateConfigurationUpdatesResponse>, SubscribeToValidateConfigurationUpdatesResponseHandler> r = this.subscribeToValidateConfigurationUpdatesAsync(request, onStreamEvent, onStreamError, onStreamClosed);
    return new StreamingResponse<>(getResponse(r.getResponse()), r.getHandler());
  }

  /**
   * Perform the subscribeToValidateConfigurationUpdates operation synchronously.
   * The initial response or error will be returned synchronously, further events will
   * arrive via the streaming callbacks.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   * @param streamResponseHandler Methods on this object will be called as stream events happen on this operation.
   *     If an executor is provided, the onStreamEvent and onStreamClosed methods will run in the executor.
   */
  public StreamingResponse<SubscribeToValidateConfigurationUpdatesResponse, SubscribeToValidateConfigurationUpdatesResponseHandler> subscribeToValidateConfigurationUpdates(
      final SubscribeToValidateConfigurationUpdatesRequest request,
      final StreamResponseHandler<ValidateConfigurationUpdateEvents> streamResponseHandler) throws
      InterruptedException {
    StreamingResponse<CompletableFuture<SubscribeToValidateConfigurationUpdatesResponse>, SubscribeToValidateConfigurationUpdatesResponseHandler> r = this.subscribeToValidateConfigurationUpdatesAsync(request, streamResponseHandler);
    return new StreamingResponse<>(getResponse(r.getResponse()), r.getHandler());
  }

  /**
   * Perform the subscribeToValidateConfigurationUpdates operation asynchronously.
   * The initial response or error will be returned as the result of the asynchronous future, further events will
   * arrive via the streaming callbacks.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   * @param streamResponseHandler Methods on this object will be called as stream events happen on this operation.
   *     If an executor is provided, the onStreamEvent and onStreamClosed methods will run in the executor.
   */
  public StreamingResponse<CompletableFuture<SubscribeToValidateConfigurationUpdatesResponse>, SubscribeToValidateConfigurationUpdatesResponseHandler> subscribeToValidateConfigurationUpdatesAsync(
      final SubscribeToValidateConfigurationUpdatesRequest request,
      final StreamResponseHandler<ValidateConfigurationUpdateEvents> streamResponseHandler) {
    SubscribeToValidateConfigurationUpdatesResponseHandler r = client.subscribeToValidateConfigurationUpdates(request, Optional.ofNullable(getStreamingResponseHandler(streamResponseHandler)));
    return new StreamingResponse<>(r.getResponse(), r);
  }

  /**
   * Perform the updateConfiguration operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public UpdateConfigurationResponse updateConfiguration(final UpdateConfigurationRequest request)
      throws InterruptedException {
    return getResponse(this.updateConfigurationAsync(request));
  }

  /**
   * Perform the updateConfiguration operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<UpdateConfigurationResponse> updateConfigurationAsync(
      final UpdateConfigurationRequest request) {
    return client.updateConfiguration(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the updateState operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public UpdateStateResponse updateState(final UpdateStateRequest request) throws
      InterruptedException {
    return getResponse(this.updateStateAsync(request));
  }

  /**
   * Perform the updateState operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<UpdateStateResponse> updateStateAsync(final UpdateStateRequest request) {
    return client.updateState(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the updateThingShadow operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public UpdateThingShadowResponse updateThingShadow(final UpdateThingShadowRequest request) throws
      InterruptedException {
    return getResponse(this.updateThingShadowAsync(request));
  }

  /**
   * Perform the updateThingShadow operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<UpdateThingShadowResponse> updateThingShadowAsync(
      final UpdateThingShadowRequest request) {
    return client.updateThingShadow(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the validateAuthorizationToken operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public ValidateAuthorizationTokenResponse validateAuthorizationToken(
      final ValidateAuthorizationTokenRequest request) throws InterruptedException {
    return getResponse(this.validateAuthorizationTokenAsync(request));
  }

  /**
   * Perform the validateAuthorizationToken operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<ValidateAuthorizationTokenResponse> validateAuthorizationTokenAsync(
      final ValidateAuthorizationTokenRequest request) {
    return client.validateAuthorizationToken(request, Optional.empty()).getResponse();
  }

  /**
   * Perform the verifyClientDeviceIdentity operation synchronously.
   *
   * @throws InterruptedException if thread is interrupted while waiting for the response
   * @return the response
   *
   * @param request request object
   */
  public VerifyClientDeviceIdentityResponse verifyClientDeviceIdentity(
      final VerifyClientDeviceIdentityRequest request) throws InterruptedException {
    return getResponse(this.verifyClientDeviceIdentityAsync(request));
  }

  /**
   * Perform the verifyClientDeviceIdentity operation asynchronously.
   *
   * @return a future which resolves to the response
   *
   * @param request request object
   */
  public CompletableFuture<VerifyClientDeviceIdentityResponse> verifyClientDeviceIdentityAsync(
      final VerifyClientDeviceIdentityRequest request) {
    return client.verifyClientDeviceIdentity(request, Optional.empty()).getResponse();
  }

  protected static <T> T getResponse(Future<T> fut) throws InterruptedException {
    try {
      return fut.get();
    }
    catch (ExecutionException e) {
      if (e.getCause() instanceof RuntimeException) {
        throw ((RuntimeException) e.getCause());
      }
      // the cause should always be RuntimeException, but we will handle this case anyway
      throw new RuntimeException(e.getCause());
    }
  }

  protected <T> StreamResponseHandler<T> getStreamingResponseHandler(StreamResponseHandler<T> h) {
    if (h == null || executor == null) {
      return h;
    }
    return new StreamResponseHandler<T>() {
      @Override public void onStreamEvent(T event) {
        executor.execute(() -> h.onStreamEvent(event));
      }
      @Override public boolean onStreamError(Throwable error) {
        return h.onStreamError(error);
      }
      @Override public void onStreamClosed() {
        executor.execute(h::onStreamClosed);
      }
    }
    ;
  }

  protected <T> StreamResponseHandler<T> getStreamingResponseHandler(Consumer<T> onStreamEvent,
      Optional<Function<Throwable, Boolean>> onStreamError, Optional<Runnable> onStreamClosed) {
    return new StreamResponseHandler<T>() {
      @Override public void onStreamEvent(T event) {
        onStreamEvent.accept(event);
      }
      @Override public boolean onStreamError(Throwable error) {
        if (onStreamError != null && onStreamError.isPresent()) {
          return onStreamError.get().apply(error);
        }
        return true;
      }
      @Override public void onStreamClosed() {
        if (onStreamClosed != null && onStreamClosed.isPresent()) {
          onStreamClosed.get().run();
        }
      }
    }
    ;
  }

  public static class StreamingResponse<T, U> {
    protected final T r;

    protected final U h;

    public StreamingResponse(T r, U h) {
      this.r = r;
      this.h = h;
    }

    public T getResponse() {
      return r;
    }

    public U getHandler() {
      return h;
    }
  }

  public static class Builder {
    protected GreengrassCoreIPC client;

    protected Executor executor;

    protected boolean useExecutor = true;

    protected String socketPath = System.getenv("AWS_GG_NUCLEUS_DOMAIN_SOCKET_FILEPATH_FOR_COMPONENT");

    protected String authToken = System.getenv("SVCUID");

    protected int port = 8888;

    protected EventStreamRPCConnection connection = null;

    protected SocketDomain socketDomain = SocketDomain.LOCAL;

    public GreengrassCoreIPCClientV2 build() throws IOException {
      if (client == null) {
        String ipcServerSocketPath = this.socketPath;
        String authToken = this.authToken;
        try (EventLoopGroup elGroup = new EventLoopGroup(1);
             ClientBootstrap clientBootstrap = new ClientBootstrap(elGroup, null);
             SocketOptions socketOptions = new SocketOptions()) {
          socketOptions.connectTimeoutMs = 3000;
          socketOptions.domain = this.socketDomain;
          socketOptions.type = SocketOptions.SocketType.STREAM;

          final EventStreamRPCConnectionConfig config = new EventStreamRPCConnectionConfig(clientBootstrap, elGroup, socketOptions, null, ipcServerSocketPath, this.port, GreengrassConnectMessageSupplier.connectMessageSupplier(authToken));
          connection = new EventStreamRPCConnection(config);
          CompletableFuture<Void> connected = new CompletableFuture<>();
          connection.connect(new EventStreamRPCConnection.LifecycleHandler() {
            @Override public void onConnect() {
              connected.complete(null);
            }
            @Override public void onDisconnect(int errorCode) {
            }
            @Override public boolean onError(Throwable t) {
              connected.completeExceptionally(t);
              return true;
            }
          } );
          try {
            connected.get();
          }
          catch (ExecutionException | InterruptedException e) {
            connection.close();
            throw new IOException(e);
          }
          this.client = new GreengrassCoreIPCClient(connection);
        }
      }
      if (this.useExecutor && this.executor == null) {
        this.executor = Executors.newCachedThreadPool();
      }
      return new GreengrassCoreIPCClientV2(this.client, this.connection, this.executor);
    }

    public Builder withClient(GreengrassCoreIPC client) {
      this.client = client;
      return this;
    }

    public Builder withAuthToken(String authToken) {
      this.authToken = authToken;
      return this;
    }

    public Builder withSocketPath(String socketPath) {
      this.socketPath = socketPath;
      return this;
    }

    public Builder withSocketDomain(SocketDomain domain) {
      this.socketDomain = domain;
      return this;
    }

    public Builder withPort(int port) {
      this.port = port;
      return this;
    }

    public Builder withExecutor(Executor executor) {
      this.useExecutor = true;
      this.executor = executor;
      return this;
    }

    public Builder withoutExecutor() {
      this.useExecutor = false;
      this.executor = null;
      return this;
    }
  }
}
