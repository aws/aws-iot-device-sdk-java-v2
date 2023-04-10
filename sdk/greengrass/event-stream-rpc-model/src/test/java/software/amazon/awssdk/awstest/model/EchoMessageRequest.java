package software.amazon.awssdk.awstest.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class EchoMessageRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#EchoMessageRequest";

  public static final EchoMessageRequest VOID;

  static {
    VOID = new EchoMessageRequest() {
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

  public EchoMessageRequest() {
    this.message = Optional.empty();
  }

  public MessageData getMessage() {
    if (message.isPresent()) {
      return message.get();
    }
    return null;
  }

  public void setMessage(final MessageData message) {
    this.message = Optional.ofNullable(message);
  }

  public EchoMessageRequest withMessage(final MessageData message) {
    setMessage(message);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof EchoMessageRequest)) return false;
    if (this == rhs) return true;
    final EchoMessageRequest other = (EchoMessageRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.message.equals(other.message);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(message);
  }
}
