package software.amazon.awssdk.awstest.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class ServiceError extends EchoTestRPCError implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#ServiceError";

  public static final ServiceError VOID;

  static {
    VOID = new ServiceError() {
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
  private Optional<String> message;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> value;

  public ServiceError(String errorMessage) {
    super("ServiceError", errorMessage);
    this.message = Optional.ofNullable(errorMessage);
    this.value = Optional.empty();
  }

  public ServiceError() {
    super("ServiceError", "");
    this.message = Optional.empty();
    this.value = Optional.empty();
  }

  @Override
  public String getErrorTypeString() {
    return "server";
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

  public ServiceError withMessage(final String message) {
    setMessage(message);
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

  public ServiceError withValue(final String value) {
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
    if (!(rhs instanceof ServiceError)) return false;
    if (this == rhs) return true;
    final ServiceError other = (ServiceError)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.message.equals(other.message);
    isEquals = isEquals && this.value.equals(other.value);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, value);
  }
}
