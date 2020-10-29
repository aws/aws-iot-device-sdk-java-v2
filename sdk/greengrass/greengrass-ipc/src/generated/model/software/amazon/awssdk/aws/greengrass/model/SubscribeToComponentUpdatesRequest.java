package software.amazon.awssdk.aws.greengrass.model;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

/**
 * Generated empty model type not defined in model
 */
public class SubscribeToComponentUpdatesRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SubscribeToComponentUpdatesRequest";

  public static final SubscribeToComponentUpdatesRequest VOID;

  static {
    VOID = new SubscribeToComponentUpdatesRequest() {
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
    return Objects.hash(SubscribeToComponentUpdatesRequest.class);
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    return (rhs instanceof SubscribeToComponentUpdatesRequest);
  }
}
