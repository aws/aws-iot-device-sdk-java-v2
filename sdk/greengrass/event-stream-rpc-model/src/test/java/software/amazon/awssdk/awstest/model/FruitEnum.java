package software.amazon.awssdk.awstest.model;

import com.google.gson.annotations.SerializedName;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

public enum FruitEnum implements EventStreamJsonMessage {
  @SerializedName("apl")
  APPLE("apl"),

  @SerializedName("org")
  ORANGE("org"),

  @SerializedName("ban")
  BANANA("ban"),

  @SerializedName("pin")
  PINEAPPLE("pin");

  public static final String APPLICATION_MODEL_TYPE = "awstest#FruitEnum";

  private static final Map<String, FruitEnum> lookup = new HashMap<String, FruitEnum>();

  static {
    for (FruitEnum value:FruitEnum.values()) {
      lookup.put(value.getValue(), value);
    }
  }

  String value;

  FruitEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String getApplicationModelType() {
    return APPLICATION_MODEL_TYPE;
  }

  public static FruitEnum get(String value) {
    return lookup.get(value);
  }
}
