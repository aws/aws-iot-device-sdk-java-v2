package software.amazon.awssdk.eventstreamrpc;

public class EventStreamClosedException extends RuntimeException {
    public EventStreamClosedException(long continauationId) {
        //TODO: Is hex formatting here useful? It is short, but not consistent anywhere else yet
        super(String.format("EventStream continuation [%s] is already closed!", Long.toHexString(continauationId)));
    }

    public EventStreamClosedException(String msg) {
        //TODO: Is hex formatting here useful? It is short, but not consistent anywhere else yet
        super(msg);
    }
}
