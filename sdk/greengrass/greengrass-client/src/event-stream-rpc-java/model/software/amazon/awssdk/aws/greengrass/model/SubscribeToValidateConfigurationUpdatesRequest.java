package software.amazon.awssdk.aws.greengrass.model;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class SubscribeToValidateConfigurationUpdatesRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SubscribeToValidateConfigurationUpdatesRequest";

  public static final SubscribeToValidateConfigurationUpdatesRequest VOID;

  static {
    VOID = new SubscribeToValidateConfigurationUpdatesRequest() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public SubscribeToValidateConfigurationUpdatesRequest() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SubscribeToValidateConfigurationUpdatesRequest)) return false;
    if (this == rhs) return true;
    final SubscribeToValidateConfigurationUpdatesRequest other = (SubscribeToValidateConfigurationUpdatesRequest)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
