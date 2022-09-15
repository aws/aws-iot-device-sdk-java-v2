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

public enum RequestStatus implements EventStreamJsonMessage {
  @SerializedName("SUCCEEDED")
  SUCCEEDED("SUCCEEDED"),

  @SerializedName("FAILED")
  FAILED("FAILED");

  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#RequestStatus";

  private static final Map<String, RequestStatus> lookup = new HashMap<String, RequestStatus>();

  static {
    for (RequestStatus value:RequestStatus.values()) {
      lookup.put(value.getValue(), value);
    }
  }

  String value;

  RequestStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  public static RequestStatus get(String value) {
    return lookup.get(value);
  }
}
