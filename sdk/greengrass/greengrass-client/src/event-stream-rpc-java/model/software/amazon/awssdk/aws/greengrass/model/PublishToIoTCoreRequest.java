package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class PublishToIoTCoreRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#PublishToIoTCoreRequest";

  public static final PublishToIoTCoreRequest VOID;

  static {
    VOID = new PublishToIoTCoreRequest() {
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

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<byte[]> payload;

  public PublishToIoTCoreRequest() {
    this.topicName = Optional.empty();
    this.qos = Optional.empty();
    this.payload = Optional.empty();
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

  public byte[] getPayload() {
    if (payload.isPresent()) {
      return payload.get();
    }
    return null;
  }

  public void setPayload(final byte[] payload) {
    this.payload = Optional.ofNullable(payload);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof PublishToIoTCoreRequest)) return false;
    if (this == rhs) return true;
    final PublishToIoTCoreRequest other = (PublishToIoTCoreRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.topicName.equals(other.topicName);
    isEquals = isEquals && this.qos.equals(other.qos);
    isEquals = isEquals && EventStreamRPCServiceModel.blobTypeEquals(this.payload, other.payload);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(topicName, qos, payload);
  }
}
