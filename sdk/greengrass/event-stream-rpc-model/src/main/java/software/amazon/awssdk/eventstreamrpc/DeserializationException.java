package software.amazon.awssdk.eventstreamrpc;

import java.util.Arrays;

/**
 * Thrown when a deserialization exception occurs
 */
public class DeserializationException extends RuntimeException {
    /**
     * Creates a new DeserializationException from the given data
     * @param lexicalData The data that could not be deserialized
     */
    public DeserializationException(Object lexicalData) {
        this(lexicalData, null);
    }

    /**
     * Creates a new DeserializationException from the given data
     * @param lexicalData The data that could not be deserialized
     * @param cause The reason the data could not be deserialized
     */
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
