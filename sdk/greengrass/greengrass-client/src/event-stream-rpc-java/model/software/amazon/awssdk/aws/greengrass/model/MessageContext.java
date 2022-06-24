package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class MessageContext implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#MessageContext";

  public static final MessageContext VOID;

  static {
    VOID = new MessageContext() {
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
  private Optional<String> topic;

  public MessageContext() {
    this.topic = Optional.empty();
  }

  public String getTopic() {
    if (topic.isPresent()) {
      return topic.get();
    }
    return null;
  }

  public void setTopic(final String topic) {
    this.topic = Optional.ofNullable(topic);
  }

  public MessageContext withTopic(final String topic) {
    setTopic(topic);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof MessageContext)) return false;
    if (this == rhs) return true;
    final MessageContext other = (MessageContext)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.topic.equals(other.topic);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(topic);
  }
}
