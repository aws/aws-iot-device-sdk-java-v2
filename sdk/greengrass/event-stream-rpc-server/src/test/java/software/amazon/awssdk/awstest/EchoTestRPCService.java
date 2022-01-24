package software.amazon.awssdk.awstest;

import software.amazon.awssdk.crt.eventstream.ServerConnectionContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceHandler;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class EchoTestRPCService extends EventStreamRPCServiceHandler {
  public static final String SERVICE_NAMESPACE = "awstest";

  protected static final Set<String> SERVICE_OPERATION_SET;

  public static final String GET_ALL_PRODUCTS = SERVICE_NAMESPACE + "#GetAllProducts";

  public static final String CAUSE_SERVICE_ERROR = SERVICE_NAMESPACE + "#CauseServiceError";

  public static final String CAUSE_STREAM_SERVICE_TO_ERROR = SERVICE_NAMESPACE + "#CauseStreamServiceToError";

  public static final String ECHO_STREAM_MESSAGES = SERVICE_NAMESPACE + "#EchoStreamMessages";

  public static final String ECHO_MESSAGE = SERVICE_NAMESPACE + "#EchoMessage";

  public static final String GET_ALL_CUSTOMERS = SERVICE_NAMESPACE + "#GetAllCustomers";

  static {
    SERVICE_OPERATION_SET = new HashSet<>();
    SERVICE_OPERATION_SET.add(GET_ALL_PRODUCTS);
    SERVICE_OPERATION_SET.add(CAUSE_SERVICE_ERROR);
    SERVICE_OPERATION_SET.add(CAUSE_STREAM_SERVICE_TO_ERROR);
    SERVICE_OPERATION_SET.add(ECHO_STREAM_MESSAGES);
    SERVICE_OPERATION_SET.add(ECHO_MESSAGE);
    SERVICE_OPERATION_SET.add(GET_ALL_CUSTOMERS);
  }

  private final Map<String, Function<OperationContinuationHandlerContext, ? extends ServerConnectionContinuationHandler>> operationSupplierMap;

  public EchoTestRPCService() {
    this.operationSupplierMap = new HashMap<>();
  }

  @Override
  public EventStreamRPCServiceModel getServiceModel() {
    return EchoTestRPCServiceModel.getInstance();
  }

  public void setGetAllProductsHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractGetAllProductsOperationHandler> handler) {
    operationSupplierMap.put(GET_ALL_PRODUCTS, handler);
  }

  public void setCauseServiceErrorHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractCauseServiceErrorOperationHandler> handler) {
    operationSupplierMap.put(CAUSE_SERVICE_ERROR, handler);
  }

  public void setCauseStreamServiceToErrorHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractCauseStreamServiceToErrorOperationHandler> handler) {
    operationSupplierMap.put(CAUSE_STREAM_SERVICE_TO_ERROR, handler);
  }

  public void setEchoStreamMessagesHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractEchoStreamMessagesOperationHandler> handler) {
    operationSupplierMap.put(ECHO_STREAM_MESSAGES, handler);
  }

  public void setEchoMessageHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractEchoMessageOperationHandler> handler) {
    operationSupplierMap.put(ECHO_MESSAGE, handler);
  }

  public void setGetAllCustomersHandler(
      Function<OperationContinuationHandlerContext, GeneratedAbstractGetAllCustomersOperationHandler> handler) {
    operationSupplierMap.put(GET_ALL_CUSTOMERS, handler);
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
