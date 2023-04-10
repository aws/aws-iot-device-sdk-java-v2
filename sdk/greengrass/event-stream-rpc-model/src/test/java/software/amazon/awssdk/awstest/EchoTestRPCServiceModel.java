package software.amazon.awssdk.awstest;

import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import software.amazon.awssdk.awstest.model.CauseServiceErrorRequest;
import software.amazon.awssdk.awstest.model.CauseServiceErrorResponse;
import software.amazon.awssdk.awstest.model.Customer;
import software.amazon.awssdk.awstest.model.EchoMessageRequest;
import software.amazon.awssdk.awstest.model.EchoMessageResponse;
import software.amazon.awssdk.awstest.model.EchoStreamingMessage;
import software.amazon.awssdk.awstest.model.EchoStreamingRequest;
import software.amazon.awssdk.awstest.model.EchoStreamingResponse;
import software.amazon.awssdk.awstest.model.FruitEnum;
import software.amazon.awssdk.awstest.model.GetAllCustomersRequest;
import software.amazon.awssdk.awstest.model.GetAllCustomersResponse;
import software.amazon.awssdk.awstest.model.GetAllProductsRequest;
import software.amazon.awssdk.awstest.model.GetAllProductsResponse;
import software.amazon.awssdk.awstest.model.MessageData;
import software.amazon.awssdk.awstest.model.Pair;
import software.amazon.awssdk.awstest.model.Product;
import software.amazon.awssdk.awstest.model.ServiceError;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class EchoTestRPCServiceModel extends EventStreamRPCServiceModel {
  private static final EchoTestRPCServiceModel INSTANCE = new EchoTestRPCServiceModel();

  public static final String SERVICE_NAMESPACE = "awstest";

  public static final String SERVICE_NAME = SERVICE_NAMESPACE + "#" + "EchoTestRPC";

  private static final Set<String> SERVICE_OPERATION_SET = new HashSet<String>();

  private static final Map<String, OperationModelContext> SERVICE_OPERATION_MODEL_MAP = new HashMap<String, OperationModelContext>();

  private static final Map<String, Class<? extends EventStreamJsonMessage>> SERVICE_OBJECT_MODEL_MAP = new HashMap<String, Class<? extends EventStreamJsonMessage>>();

  public static final String CAUSE_SERVICE_ERROR = SERVICE_NAMESPACE + "#" + "CauseServiceError";

  private static final CauseServiceErrorOperationContext _CAUSE_SERVICE_ERROR_OPERATION_CONTEXT = new CauseServiceErrorOperationContext();

  public static final String CAUSE_STREAM_SERVICE_TO_ERROR = SERVICE_NAMESPACE + "#" + "CauseStreamServiceToError";

  private static final CauseStreamServiceToErrorOperationContext _CAUSE_STREAM_SERVICE_TO_ERROR_OPERATION_CONTEXT = new CauseStreamServiceToErrorOperationContext();

  public static final String ECHO_MESSAGE = SERVICE_NAMESPACE + "#" + "EchoMessage";

  private static final EchoMessageOperationContext _ECHO_MESSAGE_OPERATION_CONTEXT = new EchoMessageOperationContext();

  public static final String ECHO_STREAM_MESSAGES = SERVICE_NAMESPACE + "#" + "EchoStreamMessages";

  private static final EchoStreamMessagesOperationContext _ECHO_STREAM_MESSAGES_OPERATION_CONTEXT = new EchoStreamMessagesOperationContext();

  public static final String GET_ALL_CUSTOMERS = SERVICE_NAMESPACE + "#" + "GetAllCustomers";

  private static final GetAllCustomersOperationContext _GET_ALL_CUSTOMERS_OPERATION_CONTEXT = new GetAllCustomersOperationContext();

  public static final String GET_ALL_PRODUCTS = SERVICE_NAMESPACE + "#" + "GetAllProducts";

  private static final GetAllProductsOperationContext _GET_ALL_PRODUCTS_OPERATION_CONTEXT = new GetAllProductsOperationContext();

  static {
    SERVICE_OPERATION_MODEL_MAP.put(CAUSE_SERVICE_ERROR, _CAUSE_SERVICE_ERROR_OPERATION_CONTEXT);
    SERVICE_OPERATION_SET.add(CAUSE_SERVICE_ERROR);
    SERVICE_OPERATION_MODEL_MAP.put(CAUSE_STREAM_SERVICE_TO_ERROR, _CAUSE_STREAM_SERVICE_TO_ERROR_OPERATION_CONTEXT);
    SERVICE_OPERATION_SET.add(CAUSE_STREAM_SERVICE_TO_ERROR);
    SERVICE_OPERATION_MODEL_MAP.put(ECHO_MESSAGE, _ECHO_MESSAGE_OPERATION_CONTEXT);
    SERVICE_OPERATION_SET.add(ECHO_MESSAGE);
    SERVICE_OPERATION_MODEL_MAP.put(ECHO_STREAM_MESSAGES, _ECHO_STREAM_MESSAGES_OPERATION_CONTEXT);
    SERVICE_OPERATION_SET.add(ECHO_STREAM_MESSAGES);
    SERVICE_OPERATION_MODEL_MAP.put(GET_ALL_CUSTOMERS, _GET_ALL_CUSTOMERS_OPERATION_CONTEXT);
    SERVICE_OPERATION_SET.add(GET_ALL_CUSTOMERS);
    SERVICE_OPERATION_MODEL_MAP.put(GET_ALL_PRODUCTS, _GET_ALL_PRODUCTS_OPERATION_CONTEXT);
    SERVICE_OPERATION_SET.add(GET_ALL_PRODUCTS);
    SERVICE_OBJECT_MODEL_MAP.put(CauseServiceErrorRequest.APPLICATION_MODEL_TYPE, CauseServiceErrorRequest.class);
    SERVICE_OBJECT_MODEL_MAP.put(CauseServiceErrorResponse.APPLICATION_MODEL_TYPE, CauseServiceErrorResponse.class);
    SERVICE_OBJECT_MODEL_MAP.put(Customer.APPLICATION_MODEL_TYPE, Customer.class);
    SERVICE_OBJECT_MODEL_MAP.put(EchoMessageRequest.APPLICATION_MODEL_TYPE, EchoMessageRequest.class);
    SERVICE_OBJECT_MODEL_MAP.put(EchoMessageResponse.APPLICATION_MODEL_TYPE, EchoMessageResponse.class);
    SERVICE_OBJECT_MODEL_MAP.put(EchoStreamingMessage.APPLICATION_MODEL_TYPE, EchoStreamingMessage.class);
    SERVICE_OBJECT_MODEL_MAP.put(EchoStreamingRequest.APPLICATION_MODEL_TYPE, EchoStreamingRequest.class);
    SERVICE_OBJECT_MODEL_MAP.put(EchoStreamingResponse.APPLICATION_MODEL_TYPE, EchoStreamingResponse.class);
    SERVICE_OBJECT_MODEL_MAP.put(FruitEnum.APPLICATION_MODEL_TYPE, FruitEnum.class);
    SERVICE_OBJECT_MODEL_MAP.put(GetAllCustomersRequest.APPLICATION_MODEL_TYPE, GetAllCustomersRequest.class);
    SERVICE_OBJECT_MODEL_MAP.put(GetAllCustomersResponse.APPLICATION_MODEL_TYPE, GetAllCustomersResponse.class);
    SERVICE_OBJECT_MODEL_MAP.put(GetAllProductsRequest.APPLICATION_MODEL_TYPE, GetAllProductsRequest.class);
    SERVICE_OBJECT_MODEL_MAP.put(GetAllProductsResponse.APPLICATION_MODEL_TYPE, GetAllProductsResponse.class);
    SERVICE_OBJECT_MODEL_MAP.put(MessageData.APPLICATION_MODEL_TYPE, MessageData.class);
    SERVICE_OBJECT_MODEL_MAP.put(Pair.APPLICATION_MODEL_TYPE, Pair.class);
    SERVICE_OBJECT_MODEL_MAP.put(Product.APPLICATION_MODEL_TYPE, Product.class);
    SERVICE_OBJECT_MODEL_MAP.put(ServiceError.APPLICATION_MODEL_TYPE, ServiceError.class);
  }

  private EchoTestRPCServiceModel() {
  }

  public static EchoTestRPCServiceModel getInstance() {
    return INSTANCE;
  }

  @Override
  public String getServiceName() {
    return "awstest#EchoTestRPC";
  }

  public static CauseServiceErrorOperationContext getCauseServiceErrorModelContext() {
    return _CAUSE_SERVICE_ERROR_OPERATION_CONTEXT;
  }

  public static CauseStreamServiceToErrorOperationContext getCauseStreamServiceToErrorModelContext(
      ) {
    return _CAUSE_STREAM_SERVICE_TO_ERROR_OPERATION_CONTEXT;
  }

  public static EchoMessageOperationContext getEchoMessageModelContext() {
    return _ECHO_MESSAGE_OPERATION_CONTEXT;
  }

  public static EchoStreamMessagesOperationContext getEchoStreamMessagesModelContext() {
    return _ECHO_STREAM_MESSAGES_OPERATION_CONTEXT;
  }

  public static GetAllCustomersOperationContext getGetAllCustomersModelContext() {
    return _GET_ALL_CUSTOMERS_OPERATION_CONTEXT;
  }

  public static GetAllProductsOperationContext getGetAllProductsModelContext() {
    return _GET_ALL_PRODUCTS_OPERATION_CONTEXT;
  }

  @Override
  public final Collection<String> getAllOperations() {
    // Return a defensive copy so caller cannot change internal structure of service model
    return new HashSet<String>(SERVICE_OPERATION_SET);
  }

  @Override
  protected Optional<Class<? extends EventStreamJsonMessage>> getServiceClassType(
      String applicationModelType) {
    if (SERVICE_OBJECT_MODEL_MAP.containsKey(applicationModelType)) {
      return Optional.of(SERVICE_OBJECT_MODEL_MAP.get(applicationModelType));
    }
    return Optional.empty();
  }

  @Override
  public OperationModelContext getOperationModelContext(String operationName) {
    return SERVICE_OPERATION_MODEL_MAP.get(operationName);
  }
}
