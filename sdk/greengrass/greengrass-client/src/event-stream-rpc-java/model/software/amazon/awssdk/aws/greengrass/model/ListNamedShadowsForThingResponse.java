package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class ListNamedShadowsForThingResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ListNamedShadowsForThingResponse";

  public static final ListNamedShadowsForThingResponse VOID;

  static {
    VOID = new ListNamedShadowsForThingResponse() {
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
  private Optional<List<String>> results;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Instant> timestamp;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> nextToken;

  public ListNamedShadowsForThingResponse() {
    this.results = Optional.empty();
    this.timestamp = Optional.empty();
    this.nextToken = Optional.empty();
  }

  public List<String> getResults() {
    if (results.isPresent()) {
      return results.get();
    }
    return null;
  }

  public void setResults(final List<String> results) {
    this.results = Optional.ofNullable(results);
  }

  public Instant getTimestamp() {
    if (timestamp.isPresent()) {
      return timestamp.get();
    }
    return null;
  }

  public void setTimestamp(final Instant timestamp) {
    this.timestamp = Optional.ofNullable(timestamp);
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

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof ListNamedShadowsForThingResponse)) return false;
    if (this == rhs) return true;
    final ListNamedShadowsForThingResponse other = (ListNamedShadowsForThingResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.results.equals(other.results);
    isEquals = isEquals && this.timestamp.equals(other.timestamp);
    isEquals = isEquals && this.nextToken.equals(other.nextToken);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(results, timestamp, nextToken);
  }
}
