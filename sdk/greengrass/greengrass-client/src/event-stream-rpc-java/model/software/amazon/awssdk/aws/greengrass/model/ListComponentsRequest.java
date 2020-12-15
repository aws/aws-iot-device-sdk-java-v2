package software.amazon.awssdk.aws.greengrass.model;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class ListComponentsRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ListComponentsRequest";

  public static final ListComponentsRequest VOID;

  static {
    VOID = new ListComponentsRequest() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public ListComponentsRequest() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof ListComponentsRequest)) return false;
    if (this == rhs) return true;
    final ListComponentsRequest other = (ListComponentsRequest)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
