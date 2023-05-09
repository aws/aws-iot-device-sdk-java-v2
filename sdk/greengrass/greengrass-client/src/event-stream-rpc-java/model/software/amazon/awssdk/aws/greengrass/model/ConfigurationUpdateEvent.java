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

public class ConfigurationUpdateEvent implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ConfigurationUpdateEvent";

  public static final ConfigurationUpdateEvent VOID;

  static {
    VOID = new ConfigurationUpdateEvent() {
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

  public ConfigurationUpdateEvent() {
    this.componentName = Optional.empty();
    this.keyPath = Optional.empty();
  }

  /**
   * The name of the component.
   */
  public String getComponentName() {
    if (componentName.isPresent()) {
      return componentName.get();
    }
    return null;
  }

  /**
   * The name of the component.
   */
  public void setComponentName(final String componentName) {
    this.componentName = Optional.ofNullable(componentName);
  }

  /**
   * The name of the component.
   */
  public ConfigurationUpdateEvent withComponentName(final String componentName) {
    setComponentName(componentName);
    return this;
  }

  /**
   * The key path to the configuration value that updated.
   */
  public List<String> getKeyPath() {
    if (keyPath.isPresent()) {
      return keyPath.get();
    }
    return null;
  }

  /**
   * The key path to the configuration value that updated.
   */
  public void setKeyPath(final List<String> keyPath) {
    this.keyPath = Optional.ofNullable(keyPath);
  }

  /**
   * The key path to the configuration value that updated.
   */
  public ConfigurationUpdateEvent withKeyPath(final List<String> keyPath) {
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
    if (!(rhs instanceof ConfigurationUpdateEvent)) return false;
    if (this == rhs) return true;
    final ConfigurationUpdateEvent other = (ConfigurationUpdateEvent)rhs;
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
