package software.amazon.awssdk.awstest.model;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetAllCustomersRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#GetAllCustomersRequest";

  public static final GetAllCustomersRequest VOID;

  static {
    VOID = new GetAllCustomersRequest() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public GetAllCustomersRequest() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetAllCustomersRequest)) return false;
    if (this == rhs) return true;
    final GetAllCustomersRequest other = (GetAllCustomersRequest)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
