package software.amazon.awssdk.eventstreamrpc;

import software.amazon.awssdk.crt.eventstream.ClientConnectionContinuation;

public class OperationResponseHandlerContext {
    final ClientConnectionContinuation continuation;

    public OperationResponseHandlerContext(ClientConnectionContinuation continuation) {
        this.continuation = continuation;
    }

    public ClientConnectionContinuation getContinuation() {
        return continuation;
    }
}
