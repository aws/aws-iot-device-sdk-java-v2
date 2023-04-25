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
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class SubscribeToConfigurationUpdateRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#SubscribeToConfigurationUpdateRequest";

  public static final SubscribeToConfigurationUpdateRequest VOID;

  static {
    VOID = new SubscribeToConfigurationUpdateRequest() {
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
  private Optional<String> componentName;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<List<String>> keyPath;

  public SubscribeToConfigurationUpdateRequest() {
    this.componentName = Optional.empty();
    this.keyPath = Optional.empty();
  }

  /**
   * (Optional) The name of the component. Defaults to the name of the component that makes the request.
   */
  public String getComponentName() {
    if (componentName.isPresent()) {
      return componentName.get();
    }
    return null;
  }

  /**
   * (Optional) The name of the component. Defaults to the name of the component that makes the request.
   */
  public void setComponentName(final String componentName) {
    this.componentName = Optional.ofNullable(componentName);
  }

  /**
   * (Optional) The name of the component. Defaults to the name of the component that makes the request.
   */
  public SubscribeToConfigurationUpdateRequest withComponentName(final String componentName) {
    setComponentName(componentName);
    return this;
  }

  /**
   * The key path to the configuration value for which to subscribe. Specify a list where each entry is the key for a single level in the configuration object.
   */
  public List<String> getKeyPath() {
    if (keyPath.isPresent()) {
      return keyPath.get();
    }
    return null;
  }

  /**
   * The key path to the configuration value for which to subscribe. Specify a list where each entry is the key for a single level in the configuration object.
   */
  public void setKeyPath(final List<String> keyPath) {
    this.keyPath = Optional.ofNullable(keyPath);
  }

  /**
   * The key path to the configuration value for which to subscribe. Specify a list where each entry is the key for a single level in the configuration object.
   */
  public SubscribeToConfigurationUpdateRequest withKeyPath(final List<String> keyPath) {
    setKeyPath(keyPath);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof SubscribeToConfigurationUpdateRequest)) return false;
    if (this == rhs) return true;
    final SubscribeToConfigurationUpdateRequest other = (SubscribeToConfigurationUpdateRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.componentName.equals(other.componentName);
    isEquals = isEquals && this.keyPath.equals(other.keyPath);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(componentName, keyPath);
  }
}
