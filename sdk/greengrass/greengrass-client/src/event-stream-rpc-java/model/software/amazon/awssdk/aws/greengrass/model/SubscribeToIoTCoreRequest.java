package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class SubscribeToIoTCoreRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SubscribeToIoTCoreRequest";

  public static final SubscribeToIoTCoreRequest VOID;

  static {
    VOID = new SubscribeToIoTCoreRequest() {
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

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> qos;

  public SubscribeToIoTCoreRequest() {
    this.topicName = Optional.empty();
    this.qos = Optional.empty();
  }

  public String getTopicName() {
    if (topicName.isPresent()) {
      return topicName.get();
    }
    return null;
  }

  public void setTopicName(final String topicName) {
    this.topicName = Optional.ofNullable(topicName);
  }

  public QOS getQos() {
    if (qos.isPresent()) {
      return QOS.get(qos.get());
    }
    return null;
  }

  public String getQosAsString() {
    if (qos.isPresent()) {
      return qos.get();
    }
    return null;
  }

  public void setQos(final QOS qos) {
    this.qos = Optional.ofNullable(qos.getValue());
  }

  public void setQos(final String qos) {
    this.qos = Optional.ofNullable(qos);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SubscribeToIoTCoreRequest)) return false;
    if (this == rhs) return true;
    final SubscribeToIoTCoreRequest other = (SubscribeToIoTCoreRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.topicName.equals(other.topicName);
    isEquals = isEquals && this.qos.equals(other.qos);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(topicName, qos);
  }
}
