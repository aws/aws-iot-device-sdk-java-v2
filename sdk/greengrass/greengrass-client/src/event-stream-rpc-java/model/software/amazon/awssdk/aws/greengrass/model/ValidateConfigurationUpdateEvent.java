package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class ValidateConfigurationUpdateEvent implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ValidateConfigurationUpdateEvent";

  public static final ValidateConfigurationUpdateEvent VOID;

  static {
    VOID = new ValidateConfigurationUpdateEvent() {
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
  private Optional<Map<String, Object>> configuration;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> deploymentId;

  public ValidateConfigurationUpdateEvent() {
    this.configuration = Optional.empty();
    this.deploymentId = Optional.empty();
  }

  public Map<String, Object> getConfiguration() {
    if (configuration.isPresent()) {
      return configuration.get();
    }
    return null;
  }

  public void setConfiguration(final Map<String, Object> configuration) {
    this.configuration = Optional.ofNullable(configuration);
  }

  public String getDeploymentId() {
    if (deploymentId.isPresent()) {
      return deploymentId.get();
    }
    return null;
  }

  public void setDeploymentId(final String deploymentId) {
    this.deploymentId = Optional.ofNullable(deploymentId);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof ValidateConfigurationUpdateEvent)) return false;
    if (this == rhs) return true;
    final ValidateConfigurationUpdateEvent other = (ValidateConfigurationUpdateEvent)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.configuration.equals(other.configuration);
    isEquals = isEquals && this.deploymentId.equals(other.deploymentId);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(configuration, deploymentId);
  }
}
