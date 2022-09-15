/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass.model;

import java.lang.String;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamOperationError;

public abstract class GreengrassCoreIPCError extends EventStreamOperationError implements EventStreamJsonMessage {
  GreengrassCoreIPCError(String errorCode, String errorMessage) {
    super("aws.greengrass#GreengrassCoreIPC", errorCode, errorMessage);
  }

  public abstract String getErrorTypeString();

  public boolean isRetryable() {
    return getErrorTypeString().equals("server");
  }

  public boolean isServerError() {
    return getErrorTypeString().equals("server");
  }

  public boolean isClientError() {
    return getErrorTypeString().equals("client");
  }
}
