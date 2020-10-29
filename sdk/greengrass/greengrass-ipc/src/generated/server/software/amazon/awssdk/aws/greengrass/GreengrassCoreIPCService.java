package software.amazon.awssdk.aws.greengrass;

import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import software.amazon.awssdk.crt.eventstream.ServerConnectionContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceHandler;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;

public final class GreengrassCoreIPCService extends EventStreamRPCServiceHandler {
  public static final String SERVICE_NAMESPACE = "aws.greengrass";

  protected static final Set<String> SERVICE_OPERATION_SET;

  public static final String SUBSCRIBE_TO_IOT_CORE = SERVICE_NAMESPACE + "#SubscribeToIoTCore";

  public static final String PUBLISH_TO_TOPIC = SERVICE_NAMESPACE + "#PublishToTopic";

  public static final String PUBLISH_TO_IOT_CORE = SERVICE_NAMESPACE + "#PublishToIoTCore";

  public static final String SUBSCRIBE_TO_CONFIGURATION_UPDATE = SERVICE_NAMESPACE + "#SubscribeToConfigurationUpdate";

  public static final String UNSUBSCRIBE_FROM_IOT_CORE = SERVICE_NAMESPACE + "#UnsubscribeFromIoTCore";

  public static final String LIST_COMPONENTS = SERVICE_NAMESPACE + "#ListComponents";

  public static final String DEFER_COMPONENT_UPDATE = SERVICE_NAMESPACE + "#DeferComponentUpdate";

  public static final String SEND_CONFIGURATION_VALIDITY_REPORT = SERVICE_NAMESPACE + "#SendConfigurationValidityReport";

  public static final String UPDATE_CONFIGURATION = SERVICE_NAMESPACE + "#UpdateConfiguration";

  public static final String SUBSCRIBE_TO_VALIDATE_CONFIGURATION_UPDATES = SERVICE_NAMESPACE + "#SubscribeToValidateConfigurationUpdates";

  public static final String VALIDATE_AUTHORIZATION_TOKEN = SERVICE_NAMESPACE + "#ValidateAuthorizationToken";

  public static final String UPDATE_RECIPES_AND_ARTIFACTS = SERVICE_NAMESPACE + "#UpdateRecipesAndArtifacts";

  public static final String RESTART_COMPONENT = SERVICE_NAMESPACE + "#RestartComponent";

  public static final String GET_LOCAL_DEPLOYMENT_STATUS = SERVICE_NAMESPACE + "#GetLocalDeploymentStatus";

  public static final String GET_SECRET_VALUE = SERVICE_NAMESPACE + "#GetSecretValue";

  public static final String UPDATE_STATE = SERVICE_NAMESPACE + "#UpdateState";

  public static final String GET_CONFIGURATION = SERVICE_NAMESPACE + "#GetConfiguration";

  public static final String SUBSCRIBE_TO_TOPIC = SERVICE_NAMESPACE + "#SubscribeToTopic";

  public static final String GET_COMPONENT_DETAILS = SERVICE_NAMESPACE + "#GetComponentDetails";

  public static final String SUBSCRIBE_TO_COMPONENT_UPDATES = SERVICE_NAMESPACE + "#SubscribeToComponentUpdates";

  public static final String LIST_LOCAL_DEPLOYMENTS = SERVICE_NAMESPACE + "#ListLocalDeployments";

  public static final String STOP_COMPONENT = SERVICE_NAMESPACE + "#StopComponent";

  public static final String CREATE_LOCAL_DEPLOYMENT = SERVICE_NAMESPACE + "#CreateLocalDeployment";

  static {
    SERVICE_OPERATION_SET = new HashSet();
    SERVICE_OPERATION_SET.add(SUBSCRIBE_TO_IOT_CORE);
    SERVICE_OPERATION_SET.add(PUBLISH_TO_TOPIC);
    SERVICE_OPERATION_SET.add(PUBLISH_TO_IOT_CORE);
    SERVICE_OPERATION_SET.add(SUBSCRIBE_TO_CONFIGURATION_UPDATE);
    SERVICE_OPERATION_SET.add(UNSUBSCRIBE_FROM_IOT_CORE);
    SERVICE_OPERATION_SET.add(LIST_COMPONENTS);
    SERVICE_OPERATION_SET.add(DEFER_COMPONENT_UPDATE);
    SERVICE_OPERATION_SET.add(SEND_CONFIGURATION_VALIDITY_REPORT);
    SERVICE_OPERATION_SET.add(UPDATE_CONFIGURATION);
    SERVICE_OPERATION_SET.add(SUBSCRIBE_TO_VALIDATE_CONFIGURATION_UPDATES);
    SERVICE_OPERATION_SET.add(VALIDATE_AUTHORIZATION_TOKEN);
    SERVICE_OPERATION_SET.add(UPDATE_RECIPES_AND_ARTIFACTS);
    SERVICE_OPERATION_SET.add(RESTART_COMPONENT);
    SERVICE_OPERATION_SET.add(GET_LOCAL_DEPLOYMENT_STATUS);
    SERVICE_OPERATION_SET.add(GET_SECRET_VALUE);
    SERVICE_OPERATION_SET.add(UPDATE_STATE);
    SERVICE_OPERATION_SET.add(GET_CONFIGURATION);
    SERVICE_OPERATION_SET.add(SUBSCRIBE_TO_TOPIC);
    SERVICE_OPERATION_SET.add(GET_COMPONENT_DETAILS);
    SERVICE_OPERATION_SET.add(SUBSCRIBE_TO_COMPONENT_UPDATES);
    SERVICE_OPERATION_SET.add(LIST_LOCAL_DEPLOYMENTS);
    SERVICE_OPERATION_SET.add(STOP_COMPONENT);
    SERVICE_OPERATION_SET.add(CREATE_LOCAL_DEPLOYMENT);
  }

  private final Map<String, Function<OperationContinuationHandlerContext, ? extends ServerConnectionContinuationHandler>> operationSupplierMap;

  public GreengrassCoreIPCService() {
    this.operationSupplierMap = new HashMap();
  }

  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  public void setSubscribeToIoTCoreHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractSubscribeToIoTCoreOperationHandler> handler) {
    operationSupplierMap.put(SUBSCRIBE_TO_IOT_CORE, handler);
  }

  public void setPublishToTopicHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractPublishToTopicOperationHandler> handler) {
    operationSupplierMap.put(PUBLISH_TO_TOPIC, handler);
  }

  public void setPublishToIoTCoreHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractPublishToIoTCoreOperationHandler> handler) {
    operationSupplierMap.put(PUBLISH_TO_IOT_CORE, handler);
  }

  public void setSubscribeToConfigurationUpdateHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractSubscribeToConfigurationUpdateOperationHandler> handler) {
    operationSupplierMap.put(SUBSCRIBE_TO_CONFIGURATION_UPDATE, handler);
  }

  public void setUnsubscribeFromIoTCoreHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractUnsubscribeFromIoTCoreOperationHandler> handler) {
    operationSupplierMap.put(UNSUBSCRIBE_FROM_IOT_CORE, handler);
  }

  public void setListComponentsHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractListComponentsOperationHandler> handler) {
    operationSupplierMap.put(LIST_COMPONENTS, handler);
  }

  public void setDeferComponentUpdateHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractDeferComponentUpdateOperationHandler> handler) {
    operationSupplierMap.put(DEFER_COMPONENT_UPDATE, handler);
  }

  public void setSendConfigurationValidityReportHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractSendConfigurationValidityReportOperationHandler> handler) {
    operationSupplierMap.put(SEND_CONFIGURATION_VALIDITY_REPORT, handler);
  }

  public void setUpdateConfigurationHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractUpdateConfigurationOperationHandler> handler) {
    operationSupplierMap.put(UPDATE_CONFIGURATION, handler);
  }

  public void setSubscribeToValidateConfigurationUpdatesHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractSubscribeToValidateConfigurationUpdatesOperationHandler> handler) {
    operationSupplierMap.put(SUBSCRIBE_TO_VALIDATE_CONFIGURATION_UPDATES, handler);
  }

  public void setValidateAuthorizationTokenHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractValidateAuthorizationTokenOperationHandler> handler) {
    operationSupplierMap.put(VALIDATE_AUTHORIZATION_TOKEN, handler);
  }

  public void setUpdateRecipesAndArtifactsHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractUpdateRecipesAndArtifactsOperationHandler> handler) {
    operationSupplierMap.put(UPDATE_RECIPES_AND_ARTIFACTS, handler);
  }

  public void setRestartComponentHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractRestartComponentOperationHandler> handler) {
    operationSupplierMap.put(RESTART_COMPONENT, handler);
  }

  public void setGetLocalDeploymentStatusHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractGetLocalDeploymentStatusOperationHandler> handler) {
    operationSupplierMap.put(GET_LOCAL_DEPLOYMENT_STATUS, handler);
  }

  public void setGetSecretValueHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractGetSecretValueOperationHandler> handler) {
    operationSupplierMap.put(GET_SECRET_VALUE, handler);
  }

  public void setUpdateStateHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractUpdateStateOperationHandler> handler) {
    operationSupplierMap.put(UPDATE_STATE, handler);
  }

  public void setGetConfigurationHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractGetConfigurationOperationHandler> handler) {
    operationSupplierMap.put(GET_CONFIGURATION, handler);
  }

  public void setSubscribeToTopicHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractSubscribeToTopicOperationHandler> handler) {
    operationSupplierMap.put(SUBSCRIBE_TO_TOPIC, handler);
  }

  public void setGetComponentDetailsHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractGetComponentDetailsOperationHandler> handler) {
    operationSupplierMap.put(GET_COMPONENT_DETAILS, handler);
  }

  public void setSubscribeToComponentUpdatesHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractSubscribeToComponentUpdatesOperationHandler> handler) {
    operationSupplierMap.put(SUBSCRIBE_TO_COMPONENT_UPDATES, handler);
  }

  public void setListLocalDeploymentsHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractListLocalDeploymentsOperationHandler> handler) {
    operationSupplierMap.put(LIST_LOCAL_DEPLOYMENTS, handler);
  }

  public void setStopComponentHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractStopComponentOperationHandler> handler) {
    operationSupplierMap.put(STOP_COMPONENT, handler);
  }

  public void setCreateLocalDeploymentHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractCreateLocalDeploymentOperationHandler> handler) {
    operationSupplierMap.put(CREATE_LOCAL_DEPLOYMENT, handler);
  }

  @Override
  public Set<String> getAllOperations() {
    return SERVICE_OPERATION_SET;
  }

  @Override
  public boolean hasHandlerForOperation(String operation) {
    return operationSupplierMap.containsKey(operation);
  }

  @Override
  public Function<OperationContinuationHandlerContext, ? extends ServerConnectionContinuationHandler> getOperationHandler(
      String operation) {
    return operationSupplierMap.get(operation);
  }

  public void setOperationHandler(String operation,
      Function<OperationContinuationHandlerContext, ? extends ServerConnectionContinuationHandler> handler) {
    operationSupplierMap.put(operation, handler);
  }
}
