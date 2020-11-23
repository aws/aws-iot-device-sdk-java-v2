package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.crt.eventstream.MessageType;

/**
 * 
 */
public class InvalidDataException extends RuntimeException {
    public InvalidDataException(MessageType unexpectedType) {
        super(String.format("Unexpected message type received: %s", unexpectedType.name()));
    }
}
