package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetLocalDeploymentStatusResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#GetLocalDeploymentStatusResponse";

  public static final GetLocalDeploymentStatusResponse VOID;

  static {
    VOID = new GetLocalDeploymentStatusResponse() {
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
  private Optional<LocalDeployment> deployment;

  public GetLocalDeploymentStatusResponse() {
    this.deployment = Optional.empty();
  }

  public LocalDeployment getDeployment() {
    if (deployment.isPresent()) {
      return deployment.get();
    }
    return null;
  }

  public void setDeployment(final LocalDeployment deployment) {
    this.deployment = Optional.ofNullable(deployment);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetLocalDeploymentStatusResponse)) return false;
    if (this == rhs) return true;
    final GetLocalDeploymentStatusResponse other = (GetLocalDeploymentStatusResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.deployment.equals(other.deployment);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(deployment);
  }
}
