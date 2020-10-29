package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class UnsubscribeFromIoTCoreRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#UnsubscribeFromIoTCoreRequest";

  public static final UnsubscribeFromIoTCoreRequest VOID;

  static {
    VOID = new UnsubscribeFromIoTCoreRequest() {
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
  private Optional<String> topicName;

  public UnsubscribeFromIoTCoreRequest() {
    this.topicName = Optional.empty();
  }

  public String getTopicName() {
    if (topicName.isPresent()) {
      return topicName.get();
    }
    return null;
  }

  public void setTopicName(final String topicName) {
    this.topicName = Optional.of(topicName);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof UnsubscribeFromIoTCoreRequest)) return false;
    if (this == rhs) return true;
    final UnsubscribeFromIoTCoreRequest other = (UnsubscribeFromIoTCoreRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.topicName.equals(other.topicName);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(topicName);
  }
}
