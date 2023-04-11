package software.amazon.awssdk.awstest.model;

import com.google.gson.annotations.Expose;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class Customer implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#Customer";

  public static final Customer VOID;

  static {
    VOID = new Customer() {
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
  private Optional<Long> id;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> firstName;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> lastName;

  public Customer() {
    this.id = Optional.empty();
    this.firstName = Optional.empty();
    this.lastName = Optional.empty();
  }

  public Long getId() {
    if (id.isPresent()) {
      return id.get();
    }
    return null;
  }

  public void setId(final Long id) {
    this.id = Optional.ofNullable(id);
  }

  public Customer withId(final Long id) {
    setId(id);
    return this;
  }

  public String getFirstName() {
    if (firstName.isPresent()) {
      return firstName.get();
    }
    return null;
  }

  public void setFirstName(final String firstName) {
    this.firstName = Optional.ofNullable(firstName);
  }

  public Customer withFirstName(final String firstName) {
    setFirstName(firstName);
    return this;
  }

  public String getLastName() {
    if (lastName.isPresent()) {
      return lastName.get();
    }
    return null;
  }

  public void setLastName(final String lastName) {
    this.lastName = Optional.ofNullable(lastName);
  }

  public Customer withLastName(final String lastName) {
    setLastName(lastName);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof Customer)) return false;
    if (this == rhs) return true;
    final Customer other = (Customer)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.id.equals(other.id);
    isEquals = isEquals && this.firstName.equals(other.firstName);
    isEquals = isEquals && this.lastName.equals(other.lastName);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firstName, lastName);
  }
}
