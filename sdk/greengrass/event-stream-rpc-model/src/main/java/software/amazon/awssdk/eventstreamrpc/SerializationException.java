package software.amazon.awssdk.eventstreamrpc;

public class SerializationException extends RuntimeException {
    public SerializationException(Object object) {
        this(object, null);
    }

    public SerializationException(Object object, Throwable cause) {
        super("Could not serialize object: " + object.toString());
    }
}
