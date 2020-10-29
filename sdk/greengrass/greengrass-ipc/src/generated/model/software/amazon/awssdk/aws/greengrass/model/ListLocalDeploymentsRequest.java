package software.amazon.awssdk.aws.greengrass.model;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Generated empty model type not defined in model
 */
public class ListLocalDeploymentsRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ListLocalDeploymentsRequest";

  public static final ListLocalDeploymentsRequest VOID;

  static {
    VOID = new ListLocalDeploymentsRequest() {
      @Override
      public final boolean isVoid() {
        return true;
      }
    };
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean isVoid() {
    return true;
  }

  @Override
  public int hashCode() {
    return Objects.hash(ListLocalDeploymentsRequest.class);
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    return (rhs instanceof ListLocalDeploymentsRequest);
  }
}
