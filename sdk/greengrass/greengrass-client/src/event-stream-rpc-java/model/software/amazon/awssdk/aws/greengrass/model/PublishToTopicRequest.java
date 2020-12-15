package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class PublishToTopicRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#PublishToTopicRequest";

  public static final PublishToTopicRequest VOID;

  static {
    VOID = new PublishToTopicRequest() {
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
  private Optional<PublishMessage> publishMessage;

  public PublishToTopicRequest() {
    this.topic = Optional.empty();
    this.publishMessage = Optional.empty();
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

  public PublishMessage getPublishMessage() {
    if (publishMessage.isPresent()) {
      return publishMessage.get();
    }
    return null;
  }

  public void setPublishMessage(final PublishMessage publishMessage) {
    this.publishMessage = Optional.ofNullable(publishMessage);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof PublishToTopicRequest)) return false;
    if (this == rhs) return true;
    final PublishToTopicRequest other = (PublishToTopicRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.topic.equals(other.topic);
    isEquals = isEquals && this.publishMessage.equals(other.publishMessage);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(topic, publishMessage);
  }
}
