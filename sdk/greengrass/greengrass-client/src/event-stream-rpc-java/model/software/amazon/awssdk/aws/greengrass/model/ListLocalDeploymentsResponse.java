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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public class ListLocalDeploymentsResponse implements EventStreamJsonMessage {
  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ListLocalDeploymentsResponse";

  public static final ListLocalDeploymentsResponse VOID;

  static {
    VOID = new ListLocalDeploymentsResponse() {
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
  private Optional<List<LocalDeployment>> localDeployments;

  public ListLocalDeploymentsResponse() {
    this.localDeployments = Optional.empty();
  }

  /**
   * The list of local deployments.
   */
  public List<LocalDeployment> getLocalDeployments() {
    if (localDeployments.isPresent()) {
      return localDeployments.get();
    }
    return null;
  }

  /**
   * The list of local deployments.
   */
  public void setLocalDeployments(final List<LocalDeployment> localDeployments) {
    this.localDeployments = Optional.ofNullable(localDeployments);
  }

  /**
   * The list of local deployments.
   */
  public ListLocalDeploymentsResponse withLocalDeployments(
      final List<LocalDeployment> localDeployments) {
    setLocalDeployments(localDeployments);
    return this;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  @Override
  public boolean equals(Object rhs) {
    if (rhs == null) return false;
    if (!(rhs instanceof ListLocalDeploymentsResponse)) return false;
    if (this == rhs) return true;
    final ListLocalDeploymentsResponse other = (ListLocalDeploymentsResponse)rhs;
    boolean isEquals = true;
    isEquals = isEquals && this.localDeployments.equals(other.localDeployments);
    return isEquals;
  }

  @Override
  public int hashCode() {
    return Objects.hash(localDeployments);
  }
}
