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

public class BinaryMessage implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#BinaryMessage";

  public static final BinaryMessage VOID;

  static {
    VOID = new BinaryMessage() {
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
  private Optional<byte[]> message;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<MessageContext> context;

  public BinaryMessage() {
    this.message = Optional.empty();
    this.context = Optional.empty();
  }

  /**
   * The binary message as a blob.
   */
  public byte[] getMessage() {
    if (message.isPresent()) {
      return message.get();
    }
    return null;
  }

  /**
   * The binary message as a blob.
   */
  public void setMessage(final byte[] message) {
    this.message = Optional.ofNullable(message);
  }

  /**
   * The binary message as a blob.
   */
  public BinaryMessage withMessage(final byte[] message) {
    setMessage(message);
    return this;
  }

  /**
   * The context of the message, such as the topic where the message was published.
   */
  public MessageContext getContext() {
    if (context.isPresent()) {
      return context.get();
    }
    return null;
  }

  /**
   * The context of the message, such as the topic where the message was published.
   */
  public void setContext(final MessageContext context) {
    this.context = Optional.ofNullable(context);
  }

  /**
   * The context of the message, such as the topic where the message was published.
   */
  public BinaryMessage withContext(final MessageContext context) {
    setContext(context);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof BinaryMessage)) return false;
    if (this == rhs) return true;
    final BinaryMessage other = (BinaryMessage)rhs;
    boolean isEquals = true;
    isEquals = isEquals && EventStreamRPCServiceModel.blobTypeEquals(this.message, other.message);
    isEquals = isEquals && this.context.equals(other.context);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, context);
  }
}
