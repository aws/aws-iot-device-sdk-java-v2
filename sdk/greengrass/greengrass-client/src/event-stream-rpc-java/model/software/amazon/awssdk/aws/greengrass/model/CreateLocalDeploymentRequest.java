package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.Expose;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class CreateLocalDeploymentRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#CreateLocalDeploymentRequest";

  public static final CreateLocalDeploymentRequest VOID;

  static {
    VOID = new CreateLocalDeploymentRequest() {
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
  private Optional<String> groupName;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Map<String, String>> rootComponentVersionsToAdd;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<List<String>> rootComponentsToRemove;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Map<String, Map<String, Object>>> componentToConfiguration;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Map<String, RunWithInfo>> componentToRunWithInfo;

  public CreateLocalDeploymentRequest() {
    this.groupName = Optional.empty();
    this.rootComponentVersionsToAdd = Optional.empty();
    this.rootComponentsToRemove = Optional.empty();
    this.componentToConfiguration = Optional.empty();
    this.componentToRunWithInfo = Optional.empty();
  }

  public String getGroupName() {
    if (groupName.isPresent()) {
      return groupName.get();
    }
    return null;
  }

  public void setGroupName(final String groupName) {
    this.groupName = Optional.ofNullable(groupName);
  }

  public Map<String, String> getRootComponentVersionsToAdd() {
    if (rootComponentVersionsToAdd.isPresent()) {
      return rootComponentVersionsToAdd.get();
    }
    return null;
  }

  public void setRootComponentVersionsToAdd(final Map<String, String> rootComponentVersionsToAdd) {
    this.rootComponentVersionsToAdd = Optional.ofNullable(rootComponentVersionsToAdd);
  }

  public List<String> getRootComponentsToRemove() {
    if (rootComponentsToRemove.isPresent()) {
      return rootComponentsToRemove.get();
    }
    return null;
  }

  public void setRootComponentsToRemove(final List<String> rootComponentsToRemove) {
    this.rootComponentsToRemove = Optional.ofNullable(rootComponentsToRemove);
  }

  public Map<String, Map<String, Object>> getComponentToConfiguration() {
    if (componentToConfiguration.isPresent()) {
      return componentToConfiguration.get();
    }
    return null;
  }

  public void setComponentToConfiguration(
      final Map<String, Map<String, Object>> componentToConfiguration) {
    this.componentToConfiguration = Optional.ofNullable(componentToConfiguration);
  }

  public Map<String, RunWithInfo> getComponentToRunWithInfo() {
    if (componentToRunWithInfo.isPresent()) {
      return componentToRunWithInfo.get();
    }
    return null;
  }

  public void setComponentToRunWithInfo(final Map<String, RunWithInfo> componentToRunWithInfo) {
    this.componentToRunWithInfo = Optional.ofNullable(componentToRunWithInfo);
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof CreateLocalDeploymentRequest)) return false;
    if (this == rhs) return true;
    final CreateLocalDeploymentRequest other = (CreateLocalDeploymentRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.groupName.equals(other.groupName);
    isEquals = isEquals && this.rootComponentVersionsToAdd.equals(other.rootComponentVersionsToAdd);
    isEquals = isEquals && this.rootComponentsToRemove.equals(other.rootComponentsToRemove);
    isEquals = isEquals && this.componentToConfiguration.equals(other.componentToConfiguration);
    isEquals = isEquals && this.componentToRunWithInfo.equals(other.componentToRunWithInfo);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupName, rootComponentVersionsToAdd, rootComponentsToRemove, componentToConfiguration, componentToRunWithInfo);
  }
}
