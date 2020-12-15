package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class UpdateStateRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#UpdateStateRequest";

  public static final UpdateStateRequest VOID;

  static {
    VOID = new UpdateStateRequest() {
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
  private Optional<String> state;

  public UpdateStateRequest() {
    this.state = Optional.empty();
  }

  public ReportedLifecycleState getState() {
    if (state.isPresent()) {
      return ReportedLifecycleState.get(state.get());
    }
    return null;
  }

  public String getStateAsString() {
    if (state.isPresent()) {
      return state.get();
    }
    return null;
  }

  public void setState(final ReportedLifecycleState state) {
    this.state = Optional.ofNullable(state.getValue());
  }

  public void setState(final String state) {
    this.state = Optional.ofNullable(state);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof UpdateStateRequest)) return false;
    if (this == rhs) return true;
    final UpdateStateRequest other = (UpdateStateRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.state.equals(other.state);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(state);
  }
}
