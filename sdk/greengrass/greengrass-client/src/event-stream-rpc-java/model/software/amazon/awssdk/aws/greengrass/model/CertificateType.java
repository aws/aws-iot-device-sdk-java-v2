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

public enum CertificateType implements EventStreamJsonMessage {
  @SerializedName("SERVER")
  SERVER("SERVER");

  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#CertificateType";

  private static final Map<String, CertificateType> lookup = new HashMap<String, CertificateType>();

  static {
    for (CertificateType value:CertificateType.values()) {
      lookup.put(value.getValue(), value);
    }
  }

  String value;

  CertificateType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  public static CertificateType get(String value) {
    return lookup.get(value);
  }
}
