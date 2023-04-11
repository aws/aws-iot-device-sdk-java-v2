package software.amazon.awssdk.awstest.model;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class EchoStreamingResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#EchoStreamingResponse";

  public static final EchoStreamingResponse VOID;

  static {
    VOID = new EchoStreamingResponse() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public EchoStreamingResponse() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof EchoStreamingResponse)) return false;
    if (this == rhs) return true;
    final EchoStreamingResponse other = (EchoStreamingResponse)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
