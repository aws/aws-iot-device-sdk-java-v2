package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class UpdateConfigurationRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#UpdateConfigurationRequest";

  public static final UpdateConfigurationRequest VOID;

  static {
    VOID = new UpdateConfigurationRequest() {
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
  private Optional<List<String>> keyPath;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Instant> timestamp;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Map<String, Object>> valueToMerge;

  public UpdateConfigurationRequest() {
    this.keyPath = Optional.empty();
    this.timestamp = Optional.empty();
    this.valueToMerge = Optional.empty();
  }

  public List<String> getKeyPath() {
    if (keyPath.isPresent()) {
      return keyPath.get();
    }
    return null;
  }

  public void setKeyPath(final List<String> keyPath) {
    this.keyPath = Optional.ofNullable(keyPath);
  }

  public Instant getTimestamp() {
    if (timestamp.isPresent()) {
      return timestamp.get();
    }
    return null;
  }

  public void setTimestamp(final Instant timestamp) {
    this.timestamp = Optional.ofNullable(timestamp);
  }

  public Map<String, Object> getValueToMerge() {
    if (valueToMerge.isPresent()) {
      return valueToMerge.get();
    }
    return null;
  }

  public void setValueToMerge(final Map<String, Object> valueToMerge) {
    this.valueToMerge = Optional.ofNullable(valueToMerge);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof UpdateConfigurationRequest)) return false;
    if (this == rhs) return true;
    final UpdateConfigurationRequest other = (UpdateConfigurationRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.keyPath.equals(other.keyPath);
    isEquals = isEquals && this.timestamp.equals(other.timestamp);
    isEquals = isEquals && this.valueToMerge.equals(other.valueToMerge);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(keyPath, timestamp, valueToMerge);
  }
}
