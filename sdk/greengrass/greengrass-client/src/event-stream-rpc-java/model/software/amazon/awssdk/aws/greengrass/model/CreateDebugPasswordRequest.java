package software.amazon.awssdk.aws.greengrass.model;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class CreateDebugPasswordRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#CreateDebugPasswordRequest";

  public static final CreateDebugPasswordRequest VOID;

  static {
    VOID = new CreateDebugPasswordRequest() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public CreateDebugPasswordRequest() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof CreateDebugPasswordRequest)) return false;
    if (this == rhs) return true;
    final CreateDebugPasswordRequest other = (CreateDebugPasswordRequest)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
