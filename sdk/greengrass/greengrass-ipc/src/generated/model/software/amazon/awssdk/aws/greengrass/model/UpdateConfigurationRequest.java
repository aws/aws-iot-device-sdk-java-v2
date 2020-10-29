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
  private Optional<String> componentName;

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
  private Optional<Map<String, Object>> newValue;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Map<String, Object>> oldValue;

  public UpdateConfigurationRequest() {
    this.componentName = Optional.empty();
    this.keyPath = Optional.empty();
    this.timestamp = Optional.empty();
    this.newValue = Optional.empty();
    this.oldValue = Optional.empty();
  }

  public String getComponentName() {
    if (componentName.isPresent()) {
      return componentName.get();
    }
    return null;
  }

  public void setComponentName(final String componentName) {
    this.componentName = Optional.ofNullable(componentName);
  }

  public List<String> getKeyPath() {
    if (keyPath.isPresent()) {
      return keyPath.get();
    }
    return null;
  }

  public void setKeyPath(final List<String> keyPath) {
    this.keyPath = Optional.of(keyPath);
  }

  public Instant getTimestamp() {
    if (timestamp.isPresent()) {
      return timestamp.get();
    }
    return null;
  }

  public void setTimestamp(final Instant timestamp) {
    this.timestamp = Optional.of(timestamp);
  }

  public Map<String, Object> getNewValue() {
    if (newValue.isPresent()) {
      return newValue.get();
    }
    return null;
  }

  public void setNewValue(final Map<String, Object> newValue) {
    this.newValue = Optional.of(newValue);
  }

  public Map<String, Object> getOldValue() {
    if (oldValue.isPresent()) {
      return oldValue.get();
    }
    return null;
  }

  public void setOldValue(final Map<String, Object> oldValue) {
    this.oldValue = Optional.ofNullable(oldValue);
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
    isEquals = isEquals && this.componentName.equals(other.componentName);
    isEquals = isEquals && this.keyPath.equals(other.keyPath);
    isEquals = isEquals && this.timestamp.equals(other.timestamp);
    isEquals = isEquals && this.newValue.equals(other.newValue);
    isEquals = isEquals && this.oldValue.equals(other.oldValue);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(componentName, keyPath, timestamp, newValue, oldValue);
  }
}
