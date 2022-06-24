package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetClientDeviceAuthTokenResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#GetClientDeviceAuthTokenResponse";

  public static final GetClientDeviceAuthTokenResponse VOID;

  static {
    VOID = new GetClientDeviceAuthTokenResponse() {
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
  private Optional<String> clientDeviceAuthToken;

  public GetClientDeviceAuthTokenResponse() {
    this.clientDeviceAuthToken = Optional.empty();
  }

  public String getClientDeviceAuthToken() {
    if (clientDeviceAuthToken.isPresent()) {
      return clientDeviceAuthToken.get();
    }
    return null;
  }

  public void setClientDeviceAuthToken(final String clientDeviceAuthToken) {
    this.clientDeviceAuthToken = Optional.ofNullable(clientDeviceAuthToken);
  }

  public GetClientDeviceAuthTokenResponse withClientDeviceAuthToken(
      final String clientDeviceAuthToken) {
    setClientDeviceAuthToken(clientDeviceAuthToken);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetClientDeviceAuthTokenResponse)) return false;
    if (this == rhs) return true;
    final GetClientDeviceAuthTokenResponse other = (GetClientDeviceAuthTokenResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.clientDeviceAuthToken.equals(other.clientDeviceAuthToken);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientDeviceAuthToken);
  }
}
