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
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class DeleteThingShadowResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#DeleteThingShadowResponse";

  public static final DeleteThingShadowResponse VOID;

  static {
    VOID = new DeleteThingShadowResponse() {
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
  private Optional<byte[]> payload;

  public DeleteThingShadowResponse() {
    this.payload = Optional.empty();
  }

  /**
   * An empty response state document.
   */
  public byte[] getPayload() {
    if (payload.isPresent()) {
      return payload.get();
    }
    return null;
  }

  /**
   * An empty response state document.
   */
  public void setPayload(final byte[] payload) {
    this.payload = Optional.ofNullable(payload);
  }

  /**
   * An empty response state document.
   */
  public DeleteThingShadowResponse withPayload(final byte[] payload) {
    setPayload(payload);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof DeleteThingShadowResponse)) return false;
    if (this == rhs) return true;
    final DeleteThingShadowResponse other = (DeleteThingShadowResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && EventStreamRPCServiceModel.blobTypeEquals(this.payload, other.payload);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(payload);
  }
}
