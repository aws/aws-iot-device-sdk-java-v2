package dautils.datestutils;

import java.util.UUID;

public class DATestUtils {
    public static final String ENV_ENDPONT = "DA_ENDPOINT";
    public static  final String ENV_CERTI = "DA_CERTI";
    public static  final String ENV_KEY = "DA_KEY";
    public static  final String ENV_TOPIC = "DA_TOPIC";
    public static  final String ENV_THING_NAME = "DA_THING_NAME";
    public static  final String ENV_SHADOW_PROPERTY = "DA_SHADOW_PROPERTY";
    public static  final String ENV_SHADOW_VALUE_SET = "DA_SHADOW_VALUE_SET";
    public static  final String ENV_SHADOW_VALUE_DEFAULT = "DA_SHADOW_VALUE_DEFAULT";
    
    public static String getEnvVar(String envName)
    {
        try
        {
            return System.getenv(envName);
        }catch (Exception e) {
            System.out.println("Failed to set environment variables " + envName + ": " + e.getMessage());
            return "";
        }
    }


}


