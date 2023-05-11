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

public class AuthorizeClientDeviceActionRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#AuthorizeClientDeviceActionRequest";

  public static final AuthorizeClientDeviceActionRequest VOID;

  static {
    VOID = new AuthorizeClientDeviceActionRequest() {
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
  private Optional<String> clientDeviceAuthToken;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> operation;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> resource;

  public AuthorizeClientDeviceActionRequest() {
    this.clientDeviceAuthToken = Optional.empty();
    this.operation = Optional.empty();
    this.resource = Optional.empty();
  }

  /**
   * The session token for the client device from GetClientDeviceAuthToken.
   */
  public String getClientDeviceAuthToken() {
    if (clientDeviceAuthToken.isPresent()) {
      return clientDeviceAuthToken.get();
    }
    return null;
  }

  /**
   * The session token for the client device from GetClientDeviceAuthToken.
   */
  public void setClientDeviceAuthToken(final String clientDeviceAuthToken) {
    this.clientDeviceAuthToken = Optional.ofNullable(clientDeviceAuthToken);
  }

  /**
   * The session token for the client device from GetClientDeviceAuthToken.
   */
  public AuthorizeClientDeviceActionRequest withClientDeviceAuthToken(
      final String clientDeviceAuthToken) {
    setClientDeviceAuthToken(clientDeviceAuthToken);
    return this;
  }

  /**
   * The operation to authorize.
   */
  public String getOperation() {
    if (operation.isPresent()) {
      return operation.get();
    }
    return null;
  }

  /**
   * The operation to authorize.
   */
  public void setOperation(final String operation) {
    this.operation = Optional.ofNullable(operation);
  }

  /**
   * The operation to authorize.
   */
  public AuthorizeClientDeviceActionRequest withOperation(final String operation) {
    setOperation(operation);
    return this;
  }

  /**
   * The resource the client device performs the operation on.
   */
  public String getResource() {
    if (resource.isPresent()) {
      return resource.get();
    }
    return null;
  }

  /**
   * The resource the client device performs the operation on.
   */
  public void setResource(final String resource) {
    this.resource = Optional.ofNullable(resource);
  }

  /**
   * The resource the client device performs the operation on.
   */
  public AuthorizeClientDeviceActionRequest withResource(final String resource) {
    setResource(resource);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof AuthorizeClientDeviceActionRequest)) return false;
    if (this == rhs) return true;
    final AuthorizeClientDeviceActionRequest other = (AuthorizeClientDeviceActionRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.clientDeviceAuthToken.equals(other.clientDeviceAuthToken);
    isEquals = isEquals && this.operation.equals(other.operation);
    isEquals = isEquals && this.resource.equals(other.resource);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientDeviceAuthToken, operation, resource);
  }
}
