package software.amazon.awssdk.awstest.model;

import com.google.gson.annotations.Expose;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.Objects;
import java.util.Optional;

public class EchoMessageResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#EchoMessageResponse";

  public static final EchoMessageResponse VOID;

  static {
    VOID = new EchoMessageResponse() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<MessageData> message;

  public EchoMessageResponse() {
    this.message = Optional.empty();
  }

  public MessageData getMessage() {
    if (message.isPresent()) {
      return message.get();
    }
    return null;
  }

  public EchoMessageResponse setMessage(final MessageData message) {
    this.message = Optional.ofNullable(message);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof EchoMessageResponse)) return false;
    if (this == rhs) return true;
    final EchoMessageResponse other = (EchoMessageResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.message.equals(other.message);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(message);
  }
}
