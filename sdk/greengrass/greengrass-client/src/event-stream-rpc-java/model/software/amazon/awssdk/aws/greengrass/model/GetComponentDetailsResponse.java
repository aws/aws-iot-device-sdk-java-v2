package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetComponentDetailsResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#GetComponentDetailsResponse";

  public static final GetComponentDetailsResponse VOID;

  static {
    VOID = new GetComponentDetailsResponse() {
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
  private Optional<ComponentDetails> componentDetails;

  public GetComponentDetailsResponse() {
    this.componentDetails = Optional.empty();
  }

  public ComponentDetails getComponentDetails() {
    if (componentDetails.isPresent()) {
      return componentDetails.get();
    }
    return null;
  }

  public void setComponentDetails(final ComponentDetails componentDetails) {
    this.componentDetails = Optional.ofNullable(componentDetails);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetComponentDetailsResponse)) return false;
    if (this == rhs) return true;
    final GetComponentDetailsResponse other = (GetComponentDetailsResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.componentDetails.equals(other.componentDetails);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(componentDetails);
  }
}
