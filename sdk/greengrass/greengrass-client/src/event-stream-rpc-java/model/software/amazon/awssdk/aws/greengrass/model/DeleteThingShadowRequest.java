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

public class DeleteThingShadowRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#DeleteThingShadowRequest";

  public static final DeleteThingShadowRequest VOID;

  static {
    VOID = new DeleteThingShadowRequest() {
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
  private Optional<String> thingName;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> shadowName;

  public DeleteThingShadowRequest() {
    this.thingName = Optional.empty();
    this.shadowName = Optional.empty();
  }

  /**
   * The name of the thing.
   */
  public String getThingName() {
    if (thingName.isPresent()) {
      return thingName.get();
    }
    return null;
  }

  /**
   * The name of the thing.
   */
  public void setThingName(final String thingName) {
    this.thingName = Optional.ofNullable(thingName);
  }

  /**
   * The name of the thing.
   */
  public DeleteThingShadowRequest withThingName(final String thingName) {
    setThingName(thingName);
    return this;
  }

  /**
   * The name of the shadow. To specify the thing's classic shadow, set this parameter to an empty string ("").
   */
  public String getShadowName() {
    if (shadowName.isPresent()) {
      return shadowName.get();
    }
    return null;
  }

  /**
   * The name of the shadow. To specify the thing's classic shadow, set this parameter to an empty string ("").
   */
  public void setShadowName(final String shadowName) {
    this.shadowName = Optional.ofNullable(shadowName);
  }

  /**
   * The name of the shadow. To specify the thing's classic shadow, set this parameter to an empty string ("").
   */
  public DeleteThingShadowRequest withShadowName(final String shadowName) {
    setShadowName(shadowName);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof DeleteThingShadowRequest)) return false;
    if (this == rhs) return true;
    final DeleteThingShadowRequest other = (DeleteThingShadowRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.thingName.equals(other.thingName);
    isEquals = isEquals && this.shadowName.equals(other.shadowName);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(thingName, shadowName);
  }
}
