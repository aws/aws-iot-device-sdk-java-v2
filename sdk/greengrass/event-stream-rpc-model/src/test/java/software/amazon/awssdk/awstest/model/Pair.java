package software.amazon.awssdk.awstest.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class Pair implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#Pair";

  public static final Pair VOID;

  static {
    VOID = new Pair() {
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
  private Optional<String> key;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> value;

  public Pair() {
    this.key = Optional.empty();
    this.value = Optional.empty();
  }

  public String getKey() {
    if (key.isPresent()) {
      return key.get();
    }
    return null;
  }

  public void setKey(final String key) {
    this.key = Optional.ofNullable(key);
  }

  public Pair withKey(final String key) {
    setKey(key);
    return this;
  }

  public String getValue() {
    if (value.isPresent()) {
      return value.get();
    }
    return null;
  }

  public void setValue(final String value) {
    this.value = Optional.ofNullable(value);
  }

  public Pair withValue(final String value) {
    setValue(value);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof Pair)) return false;
    if (this == rhs) return true;
    final Pair other = (Pair)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.key.equals(other.key);
    isEquals = isEquals && this.value.equals(other.value);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }
}
