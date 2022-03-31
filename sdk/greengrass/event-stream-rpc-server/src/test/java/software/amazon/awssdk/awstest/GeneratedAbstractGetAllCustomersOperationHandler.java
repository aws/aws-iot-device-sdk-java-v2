package software.amazon.awssdk.awstest;

import software.amazon.awssdk.awstest.EchoTestRPCServiceModel;
import software.amazon.awssdk.awstest.model.GetAllCustomersRequest;
import software.amazon.awssdk.awstest.model.GetAllCustomersResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractGetAllCustomersOperationHandler extends
        OperationContinuationHandler<GetAllCustomersRequest, GetAllCustomersResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractGetAllCustomersOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<GetAllCustomersRequest, GetAllCustomersResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return EchoTestRPCServiceModel.getGetAllCustomersModelContext();
  }
}
