package software.amazon.awssdk.eventstreamrpc.model;

import software.amazon.awssdk.crt.eventstream.Header;
import software.amazon.awssdk.crt.eventstream.MessageType;
import software.amazon.awssdk.eventstreamrpc.EventStreamRPCServiceModel;

import java.util.HashMap;
import java.util.List;

/**
 * Used to hold event stream RPC error messages that are not tied to any service.
 * Message info comes back with a payload of JSON like:
 *
 * { "message": "..." }
 *
 * And we map that to this exception type to convey the information
 */
public class EventStreamError
        extends RuntimeException {

    private final List<Header> headers;
    private final MessageType messageType;

    /**
     * Put
     * @param headers - currently unusued, but likely a useful element for output
     * @param payload - payload
     * @param messageType - message type
     * @return {@link EventStreamError}
     */
    public static EventStreamError create(final List<Header> headers, final byte[] payload, final MessageType messageType) {
        final HashMap<String, Object> map = EventStreamRPCServiceModel.getStaticGson().fromJson(new String(payload), HashMap.class);
        final String message = map.getOrDefault("message", "no message").toString();
        return new EventStreamError(String.format("%s: %s", messageType.name(), message), headers, messageType);
    }

    public EventStreamError(String message) {
        super(message);
        this.messageType = null;
        this.headers = null;
    }

    public EventStreamError(String message, List<Header> headers, MessageType messageType) {
        super(message);
        this.messageType = messageType;
        this.headers = headers;
    }

    public List<Header> getMessageHeaders() {
        return headers;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
