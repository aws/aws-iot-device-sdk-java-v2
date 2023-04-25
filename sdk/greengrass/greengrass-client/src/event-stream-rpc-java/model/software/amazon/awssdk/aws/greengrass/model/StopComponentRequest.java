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
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class StopComponentRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#StopComponentRequest";

  public static final StopComponentRequest VOID;

  static {
    VOID = new StopComponentRequest() {
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

  public StopComponentRequest() {
    this.componentName = Optional.empty();
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
  public StopComponentRequest withComponentName(final String componentName) {
    setComponentName(componentName);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof StopComponentRequest)) return false;
    if (this == rhs) return true;
    final StopComponentRequest other = (StopComponentRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.componentName.equals(other.componentName);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(componentName);
  }
}
