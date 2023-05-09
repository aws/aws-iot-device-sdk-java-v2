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

public class RunWithInfo implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#RunWithInfo";

  public static final RunWithInfo VOID;

  static {
    VOID = new RunWithInfo() {
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
  private Optional<String> posixUser;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<String> windowsUser;

  @Expose(
      serialize = true,
      deserialize = true
  )
  private Optional<SystemResourceLimits> systemResourceLimits;

  public RunWithInfo() {
    this.posixUser = Optional.empty();
    this.windowsUser = Optional.empty();
    this.systemResourceLimits = Optional.empty();
  }

  /**
   * (Optional) The POSIX system user and, optionally, group to use to run this component on Linux core devices.
   */
  public String getPosixUser() {
    if (posixUser.isPresent()) {
      return posixUser.get();
    }
    return null;
  }

  /**
   * (Optional) The POSIX system user and, optionally, group to use to run this component on Linux core devices.
   */
  public void setPosixUser(final String posixUser) {
    this.posixUser = Optional.ofNullable(posixUser);
  }

  /**
   * (Optional) The POSIX system user and, optionally, group to use to run this component on Linux core devices.
   */
  public RunWithInfo withPosixUser(final String posixUser) {
    setPosixUser(posixUser);
    return this;
  }

  /**
   * (Optional) The Windows user to use to run this component on Windows core devices.
   */
  public String getWindowsUser() {
    if (windowsUser.isPresent()) {
      return windowsUser.get();
    }
    return null;
  }

  /**
   * (Optional) The Windows user to use to run this component on Windows core devices.
   */
  public void setWindowsUser(final String windowsUser) {
    this.windowsUser = Optional.ofNullable(windowsUser);
  }

  /**
   * (Optional) The Windows user to use to run this component on Windows core devices.
   */
  public RunWithInfo withWindowsUser(final String windowsUser) {
    setWindowsUser(windowsUser);
    return this;
  }

  /**
   * (Optional) The system resource limits to apply to this component's processes.
   */
  public SystemResourceLimits getSystemResourceLimits() {
    if (systemResourceLimits.isPresent()) {
      return systemResourceLimits.get();
    }
    return null;
  }

  /**
   * (Optional) The system resource limits to apply to this component's processes.
   */
  public void setSystemResourceLimits(final SystemResourceLimits systemResourceLimits) {
    this.systemResourceLimits = Optional.ofNullable(systemResourceLimits);
  }

  /**
   * (Optional) The system resource limits to apply to this component's processes.
   */
  public RunWithInfo withSystemResourceLimits(final SystemResourceLimits systemResourceLimits) {
    setSystemResourceLimits(systemResourceLimits);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof RunWithInfo)) return false;
    if (this == rhs) return true;
    final RunWithInfo other = (RunWithInfo)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.posixUser.equals(other.posixUser);
    isEquals = isEquals && this.windowsUser.equals(other.windowsUser);
    isEquals = isEquals && this.systemResourceLimits.equals(other.systemResourceLimits);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(posixUser, windowsUser, systemResourceLimits);
  }
}
