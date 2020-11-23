package software.amazon.awssdk.aws.greengrass.model;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class UpdateRecipesAndArtifactsResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#UpdateRecipesAndArtifactsResponse";

  public static final UpdateRecipesAndArtifactsResponse VOID;

  static {
    VOID = new UpdateRecipesAndArtifactsResponse() {
      @Override
      public boolean isVoid() {
        return true;
      }
    };
  }

  public UpdateRecipesAndArtifactsResponse() {
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof UpdateRecipesAndArtifactsResponse)) return false;
    if (this == rhs) return true;
    final UpdateRecipesAndArtifactsResponse other = (UpdateRecipesAndArtifactsResponse)rhs;
    boolean isEquals = true;
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
