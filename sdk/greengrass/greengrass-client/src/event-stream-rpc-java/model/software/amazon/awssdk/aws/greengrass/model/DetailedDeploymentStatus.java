/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.SerializedName;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public enum DetailedDeploymentStatus implements EventStreamJsonMessage {
  @SerializedName("SUCCESSFUL")
  SUCCESSFUL("SUCCESSFUL"),

  @SerializedName("FAILED_NO_STATE_CHANGE")
  FAILED_NO_STATE_CHANGE("FAILED_NO_STATE_CHANGE"),

  @SerializedName("FAILED_ROLLBACK_NOT_REQUESTED")
  FAILED_ROLLBACK_NOT_REQUESTED("FAILED_ROLLBACK_NOT_REQUESTED"),

  @SerializedName("FAILED_ROLLBACK_COMPLETE")
  FAILED_ROLLBACK_COMPLETE("FAILED_ROLLBACK_COMPLETE"),

  @SerializedName("REJECTED")
  REJECTED("REJECTED");

  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#DetailedDeploymentStatus";

  private static final Map<String, DetailedDeploymentStatus> lookup = new HashMap<String, DetailedDeploymentStatus>();

  static {
    for (DetailedDeploymentStatus value:DetailedDeploymentStatus.values()) {
      lookup.put(value.getValue(), value);
    }
  }

  String value;

  DetailedDeploymentStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  public static DetailedDeploymentStatus get(String value) {
    return lookup.get(value);
  }
}
