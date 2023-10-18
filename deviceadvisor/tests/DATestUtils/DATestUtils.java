package DATestUtils;

import java.util.UUID;

public class DATestUtils {

    public enum TestType {
        CONNECT, SUB_PUB, SHADOW, JOBS
    }

    private final static String ENV_ENDPONT = "DA_ENDPOINT";
    private final static String ENV_CERTI = "DA_CERTI";
    private final static String ENV_KEY = "DA_KEY";
    private final static String ENV_TOPIC = "DA_TOPIC";
    private final static String ENV_THING_NAME = "DA_THING_NAME";
    private final static String ENV_SHADOW_PROPERTY = "DA_SHADOW_PROPERTY";
    private final static String ENV_SHADOW_VALUE_SET = "DA_SHADOW_VALUE_SET";
    private final static String ENV_SHADOW_VALUE_DEFAULT = "DA_SHADOW_VALUE_DEFAULT";
    private final static String ENV_SHADOW_NAME = "DA_SHADOW_NAME";

    public static String endpoint;
    public static String certificatePath;
    public static String keyPath;
    public static String topic;
    public static String thing_name;
    public static String shadowProperty;
    public static String shadowValue;
    public static String shadowName;

    public static Boolean init(TestType type)
    {
        endpoint = System.getenv(ENV_ENDPONT);
        certificatePath = System.getenv(ENV_CERTI);
        keyPath = System.getenv(ENV_KEY);
        topic = System.getenv(ENV_TOPIC);
        thing_name = System.getenv(ENV_THING_NAME);
        shadowProperty = System.getenv(ENV_SHADOW_PROPERTY);
        shadowValue = System.getenv(ENV_SHADOW_VALUE_SET);
        shadowName = System.getenv(ENV_SHADOW_NAME);

        if (endpoint.isEmpty() || certificatePath.isEmpty() || keyPath.isEmpty())
        {
            return false;
        }

        if (type == TestType.SUB_PUB && topic.isEmpty())
        {
            return false;
        }
        if (type == TestType.SHADOW && (thing_name.isEmpty() || shadowProperty.isEmpty() || shadowValue.isEmpty()))
        {
            return false;
        }
        return true;
    }
}
