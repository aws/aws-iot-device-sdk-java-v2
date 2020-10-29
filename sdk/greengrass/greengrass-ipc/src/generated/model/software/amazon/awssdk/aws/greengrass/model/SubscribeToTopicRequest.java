package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class SubscribeToTopicRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SubscribeToTopicRequest";

  public static final SubscribeToTopicRequest VOID;

  static {
    VOID = new SubscribeToTopicRequest() {
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

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> source;

  public SubscribeToTopicRequest() {
    this.topic = Optional.empty();
    this.source = Optional.empty();
  }

  public String getTopic() {
    if (topic.isPresent()) {
      return topic.get();
    }
    return null;
  }

  public void setTopic(final String topic) {
    this.topic = Optional.of(topic);
  }

  public String getSource() {
    if (source.isPresent()) {
      return source.get();
    }
    return null;
  }

  public void setSource(final String source) {
    this.source = Optional.ofNullable(source);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SubscribeToTopicRequest)) return false;
    if (this == rhs) return true;
    final SubscribeToTopicRequest other = (SubscribeToTopicRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.topic.equals(other.topic);
    isEquals = isEquals && this.source.equals(other.source);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(topic, source);
  }
}
