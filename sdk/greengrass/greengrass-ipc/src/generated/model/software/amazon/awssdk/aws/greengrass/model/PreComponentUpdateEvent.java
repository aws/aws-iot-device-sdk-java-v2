package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Boolean;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class PreComponentUpdateEvent implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#PreComponentUpdateEvent";

  public static final PreComponentUpdateEvent VOID;

  static {
    VOID = new PreComponentUpdateEvent() {
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
  private Optional<Boolean> isGgcRestarting;

  public PreComponentUpdateEvent() {
    this.isGgcRestarting = Optional.empty();
  }

  public Boolean isIsGgcRestarting() {
    if (isGgcRestarting.isPresent()) {
      return isGgcRestarting.get();
    }
    return null;
  }

  public void setIsGgcRestarting(final Boolean isGgcRestarting) {
    this.isGgcRestarting = Optional.of(isGgcRestarting);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof PreComponentUpdateEvent)) return false;
    if (this == rhs) return true;
    final PreComponentUpdateEvent other = (PreComponentUpdateEvent)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.isGgcRestarting.equals(other.isGgcRestarting);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(isGgcRestarting);
  }
}
