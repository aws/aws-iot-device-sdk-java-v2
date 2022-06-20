package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class JsonMessage implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#JsonMessage";

  public static final JsonMessage VOID;

  static {
    VOID = new JsonMessage() {
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
  private Optional<Map<String, Object>> message;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<MessageContext> context;

  public JsonMessage() {
    this.message = Optional.empty();
    this.context = Optional.empty();
  }

  public Map<String, Object> getMessage() {
    if (message.isPresent()) {
      return message.get();
    }
    return null;
  }

  public void setMessage(final Map<String, Object> message) {
    this.message = Optional.ofNullable(message);
  }

  public JsonMessage withMessage(final Map<String, Object> message) {
    setMessage(message);
    return this;
  }

  /**
   * The context is ignored if used in PublishMessage.
   */
  public MessageContext getContext() {
    if (context.isPresent()) {
      return context.get();
    }
    return null;
  }

  /**
   * The context is ignored if used in PublishMessage.
   */
  public void setContext(final MessageContext context) {
    this.context = Optional.ofNullable(context);
  }

  /**
   * The context is ignored if used in PublishMessage.
   */
  public JsonMessage withContext(final MessageContext context) {
    setContext(context);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof JsonMessage)) return false;
    if (this == rhs) return true;
    final JsonMessage other = (JsonMessage)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.message.equals(other.message);
    isEquals = isEquals && this.context.equals(other.context);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, context);
  }
}
