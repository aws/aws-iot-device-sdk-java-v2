package software.amazon.awssdk.awstest.model;

import com.google.gson.annotations.Expose;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class GetAllProductsResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "awstest#GetAllProductsResponse";

  public static final GetAllProductsResponse VOID;

  static {
    VOID = new GetAllProductsResponse() {
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
  private Optional<Map<String, Product>> products;

  public GetAllProductsResponse() {
    this.products = Optional.empty();
  }

  public Map<String, Product> getProducts() {
    if (products.isPresent()) {
      return products.get();
    }
    return null;
  }

  public GetAllProductsResponse setProducts(final Map<String, Product> products) {
    this.products = Optional.ofNullable(products);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof GetAllProductsResponse)) return false;
    if (this == rhs) return true;
    final GetAllProductsResponse other = (GetAllProductsResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.products.equals(other.products);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(products);
  }
}
