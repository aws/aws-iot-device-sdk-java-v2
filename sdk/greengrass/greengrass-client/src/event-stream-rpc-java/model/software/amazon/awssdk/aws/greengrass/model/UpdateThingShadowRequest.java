package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class UpdateThingShadowRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#UpdateThingShadowRequest";

  public static final UpdateThingShadowRequest VOID;

  static {
    VOID = new UpdateThingShadowRequest() {
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
  private Optional<String> thingName;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> shadowName;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<byte[]> payload;

  public UpdateThingShadowRequest() {
    this.thingName = Optional.empty();
    this.shadowName = Optional.empty();
    this.payload = Optional.empty();
  }

  public String getThingName() {
    if (thingName.isPresent()) {
      return thingName.get();
    }
    return null;
  }

  public void setThingName(final String thingName) {
    this.thingName = Optional.ofNullable(thingName);
  }

  public String getShadowName() {
    if (shadowName.isPresent()) {
      return shadowName.get();
    }
    return null;
  }

  public void setShadowName(final String shadowName) {
    this.shadowName = Optional.ofNullable(shadowName);
  }

  public byte[] getPayload() {
    if (payload.isPresent()) {
      return payload.get();
    }
    return null;
  }

  public void setPayload(final byte[] payload) {
    this.payload = Optional.ofNullable(payload);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof UpdateThingShadowRequest)) return false;
    if (this == rhs) return true;
    final UpdateThingShadowRequest other = (UpdateThingShadowRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.thingName.equals(other.thingName);
    isEquals = isEquals && this.shadowName.equals(other.shadowName);
    isEquals = isEquals && EventStreamRPCServiceModel.blobTypeEquals(this.payload, other.payload);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(thingName, shadowName, payload);
  }
}
