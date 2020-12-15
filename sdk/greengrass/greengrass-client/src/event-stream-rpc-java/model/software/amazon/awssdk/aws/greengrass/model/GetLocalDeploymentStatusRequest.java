package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetLocalDeploymentStatusRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#GetLocalDeploymentStatusRequest";

  public static final GetLocalDeploymentStatusRequest VOID;

  static {
    VOID = new GetLocalDeploymentStatusRequest() {
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
  private Optional<String> deploymentId;

  public GetLocalDeploymentStatusRequest() {
    this.deploymentId = Optional.empty();
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
    if (!(rhs instanceof GetLocalDeploymentStatusRequest)) return false;
    if (this == rhs) return true;
    final GetLocalDeploymentStatusRequest other = (GetLocalDeploymentStatusRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.deploymentId.equals(other.deploymentId);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(deploymentId);
  }
}
