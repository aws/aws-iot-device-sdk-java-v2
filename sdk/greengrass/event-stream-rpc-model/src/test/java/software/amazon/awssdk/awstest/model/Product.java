package software.amazon.awssdk.awstest.model;

import com.google.gson.annotations.Expose;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.Objects;
import java.util.Optional;

public class Product implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#Product";

  public static final Product VOID;

  static {
    VOID = new Product() {
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
  private Optional<String> name;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Integer> price;

  public Product() {
    this.name = Optional.empty();
    this.price = Optional.empty();
  }

  public String getName() {
    if (name.isPresent()) {
      return name.get();
    }
    return null;
  }

  public Product setName(final String name) {
    this.name = Optional.ofNullable(name);
    return this;
  }

  public Integer getPrice() {
    if (price.isPresent()) {
      return price.get();
    }
    return null;
  }

  public Product setPrice(final Integer price) {
    this.price = Optional.ofNullable(price);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof Product)) return false;
    if (this == rhs) return true;
    final Product other = (Product)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.name.equals(other.name);
    isEquals = isEquals && this.price.equals(other.price);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, price);
  }
}
