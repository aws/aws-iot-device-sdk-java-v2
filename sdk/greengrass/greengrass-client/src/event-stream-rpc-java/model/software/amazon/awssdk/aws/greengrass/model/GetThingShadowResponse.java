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

public class GetThingShadowResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#GetThingShadowResponse";

  public static final GetThingShadowResponse VOID;

  static {
    VOID = new GetThingShadowResponse() {
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

  public GetThingShadowResponse() {
    this.payload = Optional.empty();
  }

  /**
   * The response state document as a JSON encoded blob.
   */
  public byte[] getPayload() {
    if (payload.isPresent()) {
      return payload.get();
    }
    return null;
  }

  /**
   * The response state document as a JSON encoded blob.
   */
  public void setPayload(final byte[] payload) {
    this.payload = Optional.ofNullable(payload);
  }

  /**
   * The response state document as a JSON encoded blob.
   */
  public GetThingShadowResponse withPayload(final byte[] payload) {
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
    if (!(rhs instanceof GetThingShadowResponse)) return false;
    if (this == rhs) return true;
    final GetThingShadowResponse other = (GetThingShadowResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && EventStreamRPCServiceModel.blobTypeEquals(this.payload, other.payload);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(payload);
  }
}
