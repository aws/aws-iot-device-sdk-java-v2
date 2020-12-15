package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class RestartComponentResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#RestartComponentResponse";

  public static final RestartComponentResponse VOID;

  static {
    VOID = new RestartComponentResponse() {
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
  private Optional<String> restartStatus;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> message;

  public RestartComponentResponse() {
    this.restartStatus = Optional.empty();
    this.message = Optional.empty();
  }

  public RequestStatus getRestartStatus() {
    if (restartStatus.isPresent()) {
      return RequestStatus.get(restartStatus.get());
    }
    return null;
  }

  public String getRestartStatusAsString() {
    if (restartStatus.isPresent()) {
      return restartStatus.get();
    }
    return null;
  }

  public void setRestartStatus(final RequestStatus restartStatus) {
    this.restartStatus = Optional.ofNullable(restartStatus.getValue());
  }

  public void setRestartStatus(final String restartStatus) {
    this.restartStatus = Optional.ofNullable(restartStatus);
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
    if (!(rhs instanceof RestartComponentResponse)) return false;
    if (this == rhs) return true;
    final RestartComponentResponse other = (RestartComponentResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.restartStatus.equals(other.restartStatus);
    isEquals = isEquals && this.message.equals(other.message);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(restartStatus, message);
  }
}
