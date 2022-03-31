package software.amazon.awssdk.awstest;

import software.amazon.awssdk.awstest.EchoTestRPCServiceModel;
import software.amazon.awssdk.awstest.model.GetAllProductsRequest;
import software.amazon.awssdk.awstest.model.GetAllProductsResponse;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandler;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;
import software.amazon.awssdk.eventstreamrpc.OperationModelContext;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public abstract class GeneratedAbstractGetAllProductsOperationHandler extends
        OperationContinuationHandler<GetAllProductsRequest, GetAllProductsResponse, EventStreamJsonMessage, EventStreamJsonMessage> {
  protected GeneratedAbstractGetAllProductsOperationHandler(
      OperationContinuationHandlerContext context) {
    super(context);
  }

  @Override
  public OperationModelContext<GetAllProductsRequest, GetAllProductsResponse, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext(
      ) {
    return EchoTestRPCServiceModel.getGetAllProductsModelContext();
  }
}
