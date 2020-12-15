package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class SubscribeToConfigurationUpdateRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SubscribeToConfigurationUpdateRequest";

  public static final SubscribeToConfigurationUpdateRequest VOID;

  static {
    VOID = new SubscribeToConfigurationUpdateRequest() {
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

  public SubscribeToConfigurationUpdateRequest() {
    this.componentName = Optional.empty();
    this.keyPath = Optional.empty();
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
    this.keyPath = Optional.ofNullable(keyPath);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SubscribeToConfigurationUpdateRequest)) return false;
    if (this == rhs) return true;
    final SubscribeToConfigurationUpdateRequest other = (SubscribeToConfigurationUpdateRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.componentName.equals(other.componentName);
    isEquals = isEquals && this.keyPath.equals(other.keyPath);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(componentName, keyPath);
  }
}
