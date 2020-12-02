package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class ConfigurationValidityReport implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ConfigurationValidityReport";

  public static final ConfigurationValidityReport VOID;

  static {
    VOID = new ConfigurationValidityReport() {
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
  private Optional<String> status;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> deploymentId;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> message;

  public ConfigurationValidityReport() {
    this.status = Optional.empty();
    this.deploymentId = Optional.empty();
    this.message = Optional.empty();
  }

  public ConfigurationValidityStatus getStatus() {
    if (status.isPresent()) {
      return ConfigurationValidityStatus.get(status.get());
    }
    return null;
  }

  public String getStatusAsString() {
    if (status.isPresent()) {
      return status.get();
    }
    return null;
  }

  public void setStatus(final ConfigurationValidityStatus status) {
    this.status = Optional.ofNullable(status.getValue());
  }

  public void setStatus(final String status) {
    this.status = Optional.ofNullable(status);
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

  public String getMessage() {
    if (message.isPresent()) {
      return message.get();
    }
    return null;
  }

  public void setMessage(final String message) {
    this.message = Optional.ofNullable(message);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof ConfigurationValidityReport)) return false;
    if (this == rhs) return true;
    final ConfigurationValidityReport other = (ConfigurationValidityReport)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.status.equals(other.status);
    isEquals = isEquals && this.deploymentId.equals(other.deploymentId);
    isEquals = isEquals && this.message.equals(other.message);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, deploymentId, message);
  }
}
