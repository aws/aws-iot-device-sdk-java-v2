package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class ListComponentsResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ListComponentsResponse";

  public static final ListComponentsResponse VOID;

  static {
    VOID = new ListComponentsResponse() {
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
  private Optional<List<ComponentDetails>> components;

  public ListComponentsResponse() {
    this.components = Optional.empty();
  }

  public List<ComponentDetails> getComponents() {
    if (components.isPresent()) {
      return components.get();
    }
    return null;
  }

  public void setComponents(final List<ComponentDetails> components) {
    this.components = Optional.ofNullable(components);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof ListComponentsResponse)) return false;
    if (this == rhs) return true;
    final ListComponentsResponse other = (ListComponentsResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.components.equals(other.components);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(components);
  }
}
