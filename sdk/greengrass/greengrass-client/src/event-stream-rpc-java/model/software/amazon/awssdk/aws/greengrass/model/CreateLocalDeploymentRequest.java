/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

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

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> failureHandlingPolicy;

  public CreateLocalDeploymentRequest() {
    this.groupName = Optional.empty();
    this.rootComponentVersionsToAdd = Optional.empty();
    this.rootComponentsToRemove = Optional.empty();
    this.componentToConfiguration = Optional.empty();
    this.componentToRunWithInfo = Optional.empty();
    this.recipeDirectoryPath = Optional.empty();
    this.artifactsDirectoryPath = Optional.empty();
    this.failureHandlingPolicy = Optional.empty();
  }

  /**
   * The thing group name the deployment is targeting. If the group name is not specified, "LOCAL_DEPLOYMENT" will be used.
   */
  public String getGroupName() {
    if (groupName.isPresent()) {
      return groupName.get();
    }
    return null;
  }

  /**
   * The thing group name the deployment is targeting. If the group name is not specified, "LOCAL_DEPLOYMENT" will be used.
   */
  public void setGroupName(final String groupName) {
    this.groupName = Optional.ofNullable(groupName);
  }

  /**
   * The thing group name the deployment is targeting. If the group name is not specified, "LOCAL_DEPLOYMENT" will be used.
   */
  public CreateLocalDeploymentRequest withGroupName(final String groupName) {
    setGroupName(groupName);
    return this;
  }

  /**
   * Map of component name to version. Components will be added to the group's existing root components.
   */
  public Map<String, String> getRootComponentVersionsToAdd() {
    if (rootComponentVersionsToAdd.isPresent()) {
      return rootComponentVersionsToAdd.get();
    }
    return null;
  }

  /**
   * Map of component name to version. Components will be added to the group's existing root components.
   */
  public void setRootComponentVersionsToAdd(final Map<String, String> rootComponentVersionsToAdd) {
    this.rootComponentVersionsToAdd = Optional.ofNullable(rootComponentVersionsToAdd);
  }

  /**
   * Map of component name to version. Components will be added to the group's existing root components.
   */
  public CreateLocalDeploymentRequest withRootComponentVersionsToAdd(
      final Map<String, String> rootComponentVersionsToAdd) {
    setRootComponentVersionsToAdd(rootComponentVersionsToAdd);
    return this;
  }

  /**
   * List of components that need to be removed from the group, for example if new artifacts were loaded in this request but recipe version did not change.
   */
  public List<String> getRootComponentsToRemove() {
    if (rootComponentsToRemove.isPresent()) {
      return rootComponentsToRemove.get();
    }
    return null;
  }

  /**
   * List of components that need to be removed from the group, for example if new artifacts were loaded in this request but recipe version did not change.
   */
  public void setRootComponentsToRemove(final List<String> rootComponentsToRemove) {
    this.rootComponentsToRemove = Optional.ofNullable(rootComponentsToRemove);
  }

  /**
   * List of components that need to be removed from the group, for example if new artifacts were loaded in this request but recipe version did not change.
   */
  public CreateLocalDeploymentRequest withRootComponentsToRemove(
      final List<String> rootComponentsToRemove) {
    setRootComponentsToRemove(rootComponentsToRemove);
    return this;
  }

  /**
   * Map of component names to configuration.
   */
  public Map<String, Map<String, Object>> getComponentToConfiguration() {
    if (componentToConfiguration.isPresent()) {
      return componentToConfiguration.get();
    }
    return null;
  }

  /**
   * Map of component names to configuration.
   */
  public void setComponentToConfiguration(
      final Map<String, Map<String, Object>> componentToConfiguration) {
    this.componentToConfiguration = Optional.ofNullable(componentToConfiguration);
  }

  /**
   * Map of component names to configuration.
   */
  public CreateLocalDeploymentRequest withComponentToConfiguration(
      final Map<String, Map<String, Object>> componentToConfiguration) {
    setComponentToConfiguration(componentToConfiguration);
    return this;
  }

  /**
   * Map of component names to component run as info.
   */
  public Map<String, RunWithInfo> getComponentToRunWithInfo() {
    if (componentToRunWithInfo.isPresent()) {
      return componentToRunWithInfo.get();
    }
    return null;
  }

  /**
   * Map of component names to component run as info.
   */
  public void setComponentToRunWithInfo(final Map<String, RunWithInfo> componentToRunWithInfo) {
    this.componentToRunWithInfo = Optional.ofNullable(componentToRunWithInfo);
  }

  /**
   * Map of component names to component run as info.
   */
  public CreateLocalDeploymentRequest withComponentToRunWithInfo(
      final Map<String, RunWithInfo> componentToRunWithInfo) {
    setComponentToRunWithInfo(componentToRunWithInfo);
    return this;
  }

  /**
   * All recipes files in this directory will be copied over to the Greengrass package store.
   */
  public String getRecipeDirectoryPath() {
    if (recipeDirectoryPath.isPresent()) {
      return recipeDirectoryPath.get();
    }
    return null;
  }

  /**
   * All recipes files in this directory will be copied over to the Greengrass package store.
   */
  public void setRecipeDirectoryPath(final String recipeDirectoryPath) {
    this.recipeDirectoryPath = Optional.ofNullable(recipeDirectoryPath);
  }

  /**
   * All recipes files in this directory will be copied over to the Greengrass package store.
   */
  public CreateLocalDeploymentRequest withRecipeDirectoryPath(final String recipeDirectoryPath) {
    setRecipeDirectoryPath(recipeDirectoryPath);
    return this;
  }

  /**
   * All artifact files in this directory will be copied over to the Greengrass package store.
   */
  public String getArtifactsDirectoryPath() {
    if (artifactsDirectoryPath.isPresent()) {
      return artifactsDirectoryPath.get();
    }
    return null;
  }

  /**
   * All artifact files in this directory will be copied over to the Greengrass package store.
   */
  public void setArtifactsDirectoryPath(final String artifactsDirectoryPath) {
    this.artifactsDirectoryPath = Optional.ofNullable(artifactsDirectoryPath);
  }

  /**
   * All artifact files in this directory will be copied over to the Greengrass package store.
   */
  public CreateLocalDeploymentRequest withArtifactsDirectoryPath(
      final String artifactsDirectoryPath) {
    setArtifactsDirectoryPath(artifactsDirectoryPath);
    return this;
  }

  public FailureHandlingPolicy getFailureHandlingPolicy() {
    if (failureHandlingPolicy.isPresent()) {
      return FailureHandlingPolicy.get(failureHandlingPolicy.get());
    }
    return null;
  }

  /**
   * Deployment failure handling policy.
   */
  public String getFailureHandlingPolicyAsString() {
    if (failureHandlingPolicy.isPresent()) {
      return failureHandlingPolicy.get();
    }
    return null;
  }

  /**
   * Deployment failure handling policy.
   */
  public void setFailureHandlingPolicy(final String failureHandlingPolicy) {
    this.failureHandlingPolicy = Optional.ofNullable(failureHandlingPolicy);
  }

  /**
   * Deployment failure handling policy.
   */
  public CreateLocalDeploymentRequest withFailureHandlingPolicy(
      final String failureHandlingPolicy) {
    setFailureHandlingPolicy(failureHandlingPolicy);
    return this;
  }

  /**
   * Deployment failure handling policy.
   */
  public void setFailureHandlingPolicy(final FailureHandlingPolicy failureHandlingPolicy) {
    this.failureHandlingPolicy = Optional.ofNullable(failureHandlingPolicy.getValue());
  }

  /**
   * Deployment failure handling policy.
   */
  public CreateLocalDeploymentRequest withFailureHandlingPolicy(
      final FailureHandlingPolicy failureHandlingPolicy) {
    setFailureHandlingPolicy(failureHandlingPolicy);
    return this;
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
    isEquals = isEquals && this.recipeDirectoryPath.equals(other.recipeDirectoryPath);
    isEquals = isEquals && this.artifactsDirectoryPath.equals(other.artifactsDirectoryPath);
    isEquals = isEquals && this.failureHandlingPolicy.equals(other.failureHandlingPolicy);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupName, rootComponentVersionsToAdd, rootComponentsToRemove, componentToConfiguration, componentToRunWithInfo, recipeDirectoryPath, artifactsDirectoryPath, failureHandlingPolicy);
  }
}
