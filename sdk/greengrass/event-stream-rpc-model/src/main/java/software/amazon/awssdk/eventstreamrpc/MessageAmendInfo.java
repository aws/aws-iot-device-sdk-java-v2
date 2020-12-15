package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.crt.eventstream.Header;

import java.util.List;

/**
 * Small data class used to hold information that may be used differently depending on the context
 *
 * For sending messages, the headers stored in the object may be used to append to existing headers,
 * where it won't overwrite an existing one that may be outgoing.
 *
 * The payload may or may not be used. Refer to the method that accepts or expects this structure to
 * understand what parts are used and which are not. For example, for EventStreamRPCConnection's
 * connect() method, the headers are used verbatim into the activate request and the payload too.
 *
 * For a sendStreamEvent(obj, amendInfo) hypothetical overload, only the headers that don't overwrite
 * :content-type, service-model-type would be used, and the payload would be completely ignored from
 * this structure.
 */
public class MessageAmendInfo {
    private final List<Header> headers;
    private final byte[] payload;

    public MessageAmendInfo(List<Header> headers, byte[] payload) {
        this.headers = headers;
        this.payload = payload;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public byte[] getPayload() {
        return payload;
    }
}
