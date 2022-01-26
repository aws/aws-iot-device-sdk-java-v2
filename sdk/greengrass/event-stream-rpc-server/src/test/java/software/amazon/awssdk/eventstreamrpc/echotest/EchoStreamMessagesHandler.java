package software.amazon.awssdk.eventstreamrpc.echotest;

import software.amazon.awssdk.awstest.GeneratedAbstractEchoStreamMessagesOperationHandler;
import software.amazon.awssdk.awstest.model.EchoStreamingMessage;
import software.amazon.awssdk.awstest.model.EchoStreamingRequest;
import software.amazon.awssdk.awstest.model.EchoStreamingResponse;
import software.amazon.awssdk.awstest.model.MessageData;
import software.amazon.awssdk.eventstreamrpc.OperationContinuationHandlerContext;

/**
 * Handler responds to any stream message by sending the data right back. Specialhandling
 */
public class EchoStreamMessagesHandler extends GeneratedAbstractEchoStreamMessagesOperationHandler {
    protected EchoStreamMessagesHandler(OperationContinuationHandlerContext context) {
        super(context);
    }

    @Override
    protected void onStreamClosed() {
        //do nothing
    }

    @Override
    public EchoStreamingResponse handleRequest(EchoStreamingRequest request) {
        return EchoStreamingResponse.VOID;
    }

    @Override
    public void handleStreamEvent(EchoStreamingMessage streamRequestEvent) {
        sendStreamEvent(streamRequestEvent);
        if (streamRequestEvent.getSetUnionMember().equals(EchoStreamingMessage.UnionMember.STREAM_MESSAGE)) {
            final MessageData data = streamRequestEvent.getStreamMessage();
            if ("close".equalsIgnoreCase(data.getStringMessage())) {
                //follow with a stream close
                closeStream();
            }
        }
    }
}
