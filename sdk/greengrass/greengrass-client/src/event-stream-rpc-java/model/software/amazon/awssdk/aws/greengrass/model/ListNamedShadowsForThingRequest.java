package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class ListNamedShadowsForThingRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ListNamedShadowsForThingRequest";

  public static final ListNamedShadowsForThingRequest VOID;

  static {
    VOID = new ListNamedShadowsForThingRequest() {
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
  private Optional<String> nextToken;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Integer> pageSize;

  public ListNamedShadowsForThingRequest() {
    this.thingName = Optional.empty();
    this.nextToken = Optional.empty();
    this.pageSize = Optional.empty();
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

  public String getNextToken() {
    if (nextToken.isPresent()) {
      return nextToken.get();
    }
    return null;
  }

  public void setNextToken(final String nextToken) {
    this.nextToken = Optional.ofNullable(nextToken);
  }

  public Integer getPageSize() {
    if (pageSize.isPresent()) {
      return pageSize.get();
    }
    return null;
  }

  public void setPageSize(final Integer pageSize) {
    this.pageSize = Optional.ofNullable(pageSize);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof ListNamedShadowsForThingRequest)) return false;
    if (this == rhs) return true;
    final ListNamedShadowsForThingRequest other = (ListNamedShadowsForThingRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.thingName.equals(other.thingName);
    isEquals = isEquals && this.nextToken.equals(other.nextToken);
    isEquals = isEquals && this.pageSize.equals(other.pageSize);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(thingName, nextToken, pageSize);
  }
}
