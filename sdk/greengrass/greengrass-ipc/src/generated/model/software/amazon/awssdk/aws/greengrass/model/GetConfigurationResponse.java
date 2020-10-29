package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetConfigurationResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#GetConfigurationResponse";

  public static final GetConfigurationResponse VOID;

  static {
    VOID = new GetConfigurationResponse() {
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
  private Optional<Map<String, Object>> value;

  public GetConfigurationResponse() {
    this.componentName = Optional.empty();
    this.value = Optional.empty();
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

  public Map<String, Object> getValue() {
    if (value.isPresent()) {
      return value.get();
    }
    return null;
  }

  public void setValue(final Map<String, Object> value) {
    this.value = Optional.ofNullable(value);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetConfigurationResponse)) return false;
    if (this == rhs) return true;
    final GetConfigurationResponse other = (GetConfigurationResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.componentName.equals(other.componentName);
    isEquals = isEquals && this.value.equals(other.value);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(componentName, value);
  }
}
