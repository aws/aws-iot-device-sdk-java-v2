package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class UpdateRecipesAndArtifactsRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#UpdateRecipesAndArtifactsRequest";

  public static final UpdateRecipesAndArtifactsRequest VOID;

  static {
    VOID = new UpdateRecipesAndArtifactsRequest() {
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
  private Optional<String> recipeDirectoryPath;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> artifactsDirectoryPath;

  public UpdateRecipesAndArtifactsRequest() {
    this.recipeDirectoryPath = Optional.empty();
    this.artifactsDirectoryPath = Optional.empty();
  }

  public String getRecipeDirectoryPath() {
    if (recipeDirectoryPath.isPresent()) {
      return recipeDirectoryPath.get();
    }
    return null;
  }

  public void setRecipeDirectoryPath(final String recipeDirectoryPath) {
    this.recipeDirectoryPath = Optional.ofNullable(recipeDirectoryPath);
  }

  public String getArtifactsDirectoryPath() {
    if (artifactsDirectoryPath.isPresent()) {
      return artifactsDirectoryPath.get();
    }
    return null;
  }

  public void setArtifactsDirectoryPath(final String artifactsDirectoryPath) {
    this.artifactsDirectoryPath = Optional.ofNullable(artifactsDirectoryPath);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof UpdateRecipesAndArtifactsRequest)) return false;
    if (this == rhs) return true;
    final UpdateRecipesAndArtifactsRequest other = (UpdateRecipesAndArtifactsRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.recipeDirectoryPath.equals(other.recipeDirectoryPath);
    isEquals = isEquals && this.artifactsDirectoryPath.equals(other.artifactsDirectoryPath);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(recipeDirectoryPath, artifactsDirectoryPath);
  }
}
