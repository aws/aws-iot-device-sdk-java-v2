package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class GetThingShadowRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#GetThingShadowRequest";

  public static final GetThingShadowRequest VOID;

  static {
    VOID = new GetThingShadowRequest() {
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

  public GetThingShadowRequest() {
    this.thingName = Optional.empty();
    this.shadowName = Optional.empty();
  }

  public String getThingName() {
    if (thingName.isPresent()) {
      return thingName.get();
    }
    return null;
  }

  public GetThingShadowRequest setThingName(final String thingName) {
    this.thingName = Optional.ofNullable(thingName);
    return this;
  }

  public String getShadowName() {
    if (shadowName.isPresent()) {
      return shadowName.get();
    }
    return null;
  }

  public GetThingShadowRequest setShadowName(final String shadowName) {
    this.shadowName = Optional.ofNullable(shadowName);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetThingShadowRequest)) return false;
    if (this == rhs) return true;
    final GetThingShadowRequest other = (GetThingShadowRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.thingName.equals(other.thingName);
    isEquals = isEquals && this.shadowName.equals(other.shadowName);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(thingName, shadowName);
  }
}
