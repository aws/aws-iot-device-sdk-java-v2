package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class SubscribeToValidateConfigurationUpdatesResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SubscribeToValidateConfigurationUpdatesResponse";

  public static final SubscribeToValidateConfigurationUpdatesResponse VOID;

  static {
    VOID = new SubscribeToValidateConfigurationUpdatesResponse() {
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
  private Optional<ValidateConfigurationUpdateEvents> messages;

  public SubscribeToValidateConfigurationUpdatesResponse() {
    this.messages = Optional.empty();
  }

  public ValidateConfigurationUpdateEvents getMessages() {
    if (messages.isPresent()) {
      return messages.get();
    }
    return null;
  }

  public void setMessages(final ValidateConfigurationUpdateEvents messages) {
    this.messages = Optional.ofNullable(messages);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SubscribeToValidateConfigurationUpdatesResponse)) return false;
    if (this == rhs) return true;
    final SubscribeToValidateConfigurationUpdatesResponse other = (SubscribeToValidateConfigurationUpdatesResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.messages.equals(other.messages);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(messages);
  }
}
