package software.amazon.awssdk.eventstreamrpc;

import java.util.Arrays;

public class DeserializationException extends RuntimeException {
    public DeserializationException(Object lexicalData) {
        this(lexicalData, null);
    }

    public DeserializationException(Object lexicalData, Throwable cause) {
        super("Could not deserialize data: [" + stringify(lexicalData) + "]", cause);
    }

    private static String stringify(Object lexicalData) {
        if (lexicalData instanceof byte[]) {
            return Arrays.toString((byte[]) lexicalData);
        }

        return lexicalData.toString();
    }
}
