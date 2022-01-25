package software.amazon.awssdk.awstest.model;

import com.google.gson.annotations.Expose;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GetAllCustomersResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#GetAllCustomersResponse";

  public static final GetAllCustomersResponse VOID;

  static {
    VOID = new GetAllCustomersResponse() {
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
  private Optional<List<Customer>> customers;

  public GetAllCustomersResponse() {
    this.customers = Optional.empty();
  }

  public List<Customer> getCustomers() {
    if (customers.isPresent()) {
      return customers.get();
    }
    return null;
  }

  public GetAllCustomersResponse setCustomers(final List<Customer> customers) {
    this.customers = Optional.ofNullable(customers);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetAllCustomersResponse)) return false;
    if (this == rhs) return true;
    final GetAllCustomersResponse other = (GetAllCustomersResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.customers.equals(other.customers);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(customers);
  }
}
