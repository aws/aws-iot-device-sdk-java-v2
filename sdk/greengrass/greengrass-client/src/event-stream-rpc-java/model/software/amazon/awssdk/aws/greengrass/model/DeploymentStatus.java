package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.SerializedName;
import java.lang.Override;
import java.lang.String;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public enum DeploymentStatus implements EventStreamJsonMessage {
  @SerializedName("QUEUED")
  QUEUED("QUEUED"),

  @SerializedName("IN_PROGRESS")
  IN_PROGRESS("IN_PROGRESS"),

  @SerializedName("SUCCEEDED")
  SUCCEEDED("SUCCEEDED"),

  @SerializedName("FAILED")
  FAILED("FAILED");

  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#DeploymentStatus";

  String value;

  DeploymentStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }
}
