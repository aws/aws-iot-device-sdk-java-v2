package software.amazon.awssdk.awstest.model;

import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.Objects;

public class GetAllProductsRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#GetAllProductsRequest";

  public static final GetAllProductsRequest VOID;

  static {
    VOID = new GetAllProductsRequest() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public GetAllProductsRequest() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetAllProductsRequest)) return false;
    if (this == rhs) return true;
    final GetAllProductsRequest other = (GetAllProductsRequest)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
