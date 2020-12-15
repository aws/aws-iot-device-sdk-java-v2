package software.amazon.awssdk.aws.greengrass.model;

import com.google.gson.annotations.SerializedName;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public enum ReportedLifecycleState implements EventStreamJsonMessage {
  @SerializedName("RUNNING")
  RUNNING("RUNNING"),

  @SerializedName("ERRORED")
  ERRORED("ERRORED");

  public static final String APPLICATION_MODEL_TYPE = "aws.greengrass#ReportedLifecycleState";

  private static final Map<String, ReportedLifecycleState> lookup = new HashMap<String, ReportedLifecycleState>();

  static {
    for (ReportedLifecycleState value:ReportedLifecycleState.values()) {
      lookup.put(value.getValue(), value);
    }
  }

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

  public static ReportedLifecycleState get(String value) {
    return lookup.get(value);
  }
}
