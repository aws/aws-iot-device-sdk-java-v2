package software.amazon.awssdk.awstest.model;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class CauseServiceErrorRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#CauseServiceErrorRequest";

  public static final CauseServiceErrorRequest VOID;

  static {
    VOID = new CauseServiceErrorRequest() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public CauseServiceErrorRequest() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof CauseServiceErrorRequest)) return false;
    if (this == rhs) return true;
    final CauseServiceErrorRequest other = (CauseServiceErrorRequest)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
