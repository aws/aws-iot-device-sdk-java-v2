package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.SerializedName;
import java.lang.Override;
import java.lang.String;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public enum ReportedLifecycleState implements EventStreamJsonMessage {
  @SerializedName("RUNNING")
  RUNNING("RUNNING"),

  @SerializedName("ERRORED")
  ERRORED("ERRORED");

  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ReportedLifecycleState";

  String value;

  ReportedLifecycleState(String value) {
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
