package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.SerializedName;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
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

  private static final Map<String, DeploymentStatus> lookup = new HashMap<String, DeploymentStatus>();

  static {
    for (DeploymentStatus value:DeploymentStatus.values()) {
      lookup.put(value.getValue(), value);
    }
  }

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

  public static DeploymentStatus get(String value) {
    return lookup.get(value);
  }
}
