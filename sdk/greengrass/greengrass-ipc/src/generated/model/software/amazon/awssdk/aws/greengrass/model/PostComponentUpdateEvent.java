package software.amazon.awssdk.aws.greengrass.model;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class PostComponentUpdateEvent implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#PostComponentUpdateEvent";

  public static final PostComponentUpdateEvent VOID;

  static {
    VOID = new PostComponentUpdateEvent() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public PostComponentUpdateEvent() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof PostComponentUpdateEvent)) return false;
    if (this == rhs) return true;
    final PostComponentUpdateEvent other = (PostComponentUpdateEvent)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
