package software.amazon.awssdk.awstest.model;

import java.lang.String;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamOperationError;

public abstract class EchoTestRPCError extends EventStreamOperationError implements EventStreamJsonMessage {
  EchoTestRPCError(String errorCode, String errorMessage) {
    super("awstest#EchoTestRPC", errorCode, errorMessage);
  }

  public abstract String getErrorTypeString();

  public boolean isRetryable() {
    return getErrorTypeString().equals("server");
  }

  public boolean isServerError() {
    return getErrorTypeString().equals("server");
  }

  public boolean isClientError() {
    return getErrorTypeString().equals("client");
  }
}
