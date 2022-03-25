package software.amazon.awssdk.awstest.model;

import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.Objects;

public class CauseServiceErrorResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#CauseServiceErrorResponse";

  public static final CauseServiceErrorResponse VOID;

  static {
    VOID = new CauseServiceErrorResponse() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public CauseServiceErrorResponse() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof CauseServiceErrorResponse)) return false;
    if (this == rhs) return true;
    final CauseServiceErrorResponse other = (CauseServiceErrorResponse)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
