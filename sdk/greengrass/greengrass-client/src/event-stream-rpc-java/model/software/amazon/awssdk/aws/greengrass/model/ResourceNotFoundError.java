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

public class ResourceNotFoundError extends GreengrassCoreIPCError implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ResourceNotFoundError";

  public static final ResourceNotFoundError VOID;

  static {
    VOID = new ResourceNotFoundError() {
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
  private Optional<String> message;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> resourceType;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> resourceName;

  public ResourceNotFoundError(String errorMessage) {
    super("ResourceNotFoundError", errorMessage);
    this.message = Optional.ofNullable(errorMessage);
    this.resourceType = Optional.empty();
    this.resourceName = Optional.empty();
  }

  public ResourceNotFoundError() {
    super("ResourceNotFoundError", "");
    this.message = Optional.empty();
    this.resourceType = Optional.empty();
    this.resourceName = Optional.empty();
  }

  @Override
  public String getErrorTypeString() {
    return "client";
  }

  public String getMessage() {
    if (message.isPresent()) {
      return message.get();
    }
    return null;
  }

  public void setMessage(final String message) {
    this.message = Optional.ofNullable(message);
  }

  public ResourceNotFoundError withMessage(final String message) {
    setMessage(message);
    return this;
  }

  public String getResourceType() {
    if (resourceType.isPresent()) {
      return resourceType.get();
    }
    return null;
  }

  public void setResourceType(final String resourceType) {
    this.resourceType = Optional.ofNullable(resourceType);
  }

  public ResourceNotFoundError withResourceType(final String resourceType) {
    setResourceType(resourceType);
    return this;
  }

  public String getResourceName() {
    if (resourceName.isPresent()) {
      return resourceName.get();
    }
    return null;
  }

  public void setResourceName(final String resourceName) {
    this.resourceName = Optional.ofNullable(resourceName);
  }

  public ResourceNotFoundError withResourceName(final String resourceName) {
    setResourceName(resourceName);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof ResourceNotFoundError)) return false;
    if (this == rhs) return true;
    final ResourceNotFoundError other = (ResourceNotFoundError)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.message.equals(other.message);
    isEquals = isEquals && this.resourceType.equals(other.resourceType);
    isEquals = isEquals && this.resourceName.equals(other.resourceName);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, resourceType, resourceName);
  }
}
