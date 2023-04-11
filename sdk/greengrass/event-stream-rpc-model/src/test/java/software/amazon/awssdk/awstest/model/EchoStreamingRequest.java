package software.amazon.awssdk.awstest.model;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class EchoStreamingRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#EchoStreamingRequest";

  public static final EchoStreamingRequest VOID;

  static {
    VOID = new EchoStreamingRequest() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public EchoStreamingRequest() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof EchoStreamingRequest)) return false;
    if (this == rhs) return true;
    final EchoStreamingRequest other = (EchoStreamingRequest)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
