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
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class UpdateConfigurationRequest implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#UpdateConfigurationRequest";

  public static final UpdateConfigurationRequest VOID;

  static {
    VOID = new UpdateConfigurationRequest() {
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
  private Optional<List<String>> keyPath;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Instant> timestamp;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<Map<String, Object>> valueToMerge;

  public UpdateConfigurationRequest() {
    this.keyPath = Optional.empty();
    this.timestamp = Optional.empty();
    this.valueToMerge = Optional.empty();
  }

  /**
   * (Optional) The key path to the container node (the object) to update. Specify a list where each entry is the key for a single level in the configuration object. Defaults to the root of the configuration object.
   */
  public List<String> getKeyPath() {
    if (keyPath.isPresent()) {
      return keyPath.get();
    }
    return null;
  }

  /**
   * (Optional) The key path to the container node (the object) to update. Specify a list where each entry is the key for a single level in the configuration object. Defaults to the root of the configuration object.
   */
  public void setKeyPath(final List<String> keyPath) {
    this.keyPath = Optional.ofNullable(keyPath);
  }

  /**
   * (Optional) The key path to the container node (the object) to update. Specify a list where each entry is the key for a single level in the configuration object. Defaults to the root of the configuration object.
   */
  public UpdateConfigurationRequest withKeyPath(final List<String> keyPath) {
    setKeyPath(keyPath);
    return this;
  }

  /**
   * The current Unix epoch time in milliseconds. This operation uses this timestamp to resolve concurrent updates to the key. If the key in the component configuration has a greater timestamp than the timestamp in the request, then the request fails.
   */
  public Instant getTimestamp() {
    if (timestamp.isPresent()) {
      return timestamp.get();
    }
    return null;
  }

  /**
   * The current Unix epoch time in milliseconds. This operation uses this timestamp to resolve concurrent updates to the key. If the key in the component configuration has a greater timestamp than the timestamp in the request, then the request fails.
   */
  public void setTimestamp(final Instant timestamp) {
    this.timestamp = Optional.ofNullable(timestamp);
  }

  /**
   * The current Unix epoch time in milliseconds. This operation uses this timestamp to resolve concurrent updates to the key. If the key in the component configuration has a greater timestamp than the timestamp in the request, then the request fails.
   */
  public UpdateConfigurationRequest withTimestamp(final Instant timestamp) {
    setTimestamp(timestamp);
    return this;
  }

  /**
   * The configuration object to merge at the location that you specify in keyPath.
   */
  public Map<String, Object> getValueToMerge() {
    if (valueToMerge.isPresent()) {
      return valueToMerge.get();
    }
    return null;
  }

  /**
   * The configuration object to merge at the location that you specify in keyPath.
   */
  public void setValueToMerge(final Map<String, Object> valueToMerge) {
    this.valueToMerge = Optional.ofNullable(valueToMerge);
  }

  /**
   * The configuration object to merge at the location that you specify in keyPath.
   */
  public UpdateConfigurationRequest withValueToMerge(final Map<String, Object> valueToMerge) {
    setValueToMerge(valueToMerge);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof UpdateConfigurationRequest)) return false;
    if (this == rhs) return true;
    final UpdateConfigurationRequest other = (UpdateConfigurationRequest)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.keyPath.equals(other.keyPath);
    isEquals = isEquals && this.timestamp.equals(other.timestamp);
    isEquals = isEquals && this.valueToMerge.equals(other.valueToMerge);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(keyPath, timestamp, valueToMerge);
  }
}
