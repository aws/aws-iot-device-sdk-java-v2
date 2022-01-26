package greengrass;

import software.amazon.awssdk.aws.greengrass.GreengrassCoreIPCServiceModel;
import software.amazon.awssdk.crt.eventstream.ServerConnectionContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceHandler;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class GreengrassCoreIPCService extends EventStreamRPCServiceHandler {
  public static final String SERVICE_NAMESPACE = "aws.greengrass";

  protected static final Set<String> SERVICE_OPERATION_SET;

  public static final String SUBSCRIBE_TO_TOPIC = SERVICE_NAMESPACE + "#SubscribeToTopic";

  public static final String CREATE_LOCAL_DEPLOYMENT = SERVICE_NAMESPACE + "#CreateLocalDeployment";

  static {
    SERVICE_OPERATION_SET = new HashSet<>();
    SERVICE_OPERATION_SET.add(SUBSCRIBE_TO_TOPIC);
    SERVICE_OPERATION_SET.add(CREATE_LOCAL_DEPLOYMENT);
  }

  private final Map<String, Function<OperationContinuationHandlerContext, ? extends ServerConnectionContinuationHandler>> operationSupplierMap;

  public GreengrassCoreIPCService() {
    this.operationSupplierMap = new HashMap<>();
  }

  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return GreengrassCoreIPCServiceModel.getInstance();
  }

  public void setSubscribeToTopicHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractSubscribeToTopicOperationHandler> handler) {
    operationSupplierMap.put(SUBSCRIBE_TO_TOPIC, handler);
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
