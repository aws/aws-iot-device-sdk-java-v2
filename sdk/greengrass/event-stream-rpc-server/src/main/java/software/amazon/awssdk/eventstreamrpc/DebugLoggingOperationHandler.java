package software.amazon.awssdk.eventstreamrpc;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.eventstreamrpc.model.EventStreamJsonMessage;

import java.nio.charset.StandardCharsets;

/**
 * Useful to set as a handler for an operation with no implementation yet.
 */
public class DebugLoggingOperationHandler extends OperationContinuationHandler
        <EventStreamJsonMessage, EventStreamJsonMessage, EventStreamJsonMessage, EventStreamJsonMessage> {
    private static Logger LOGGER = LoggerFactory.getLogger(DebugLoggingOperationHandler.class);
    private final OperationModelContext operationModelContext;

    /**
     * Constructs a new DebugLoggingOperationHandler from the given model and continuation handler contexts
     * @param modelContext The model context
     * @param context The continuation handler model context
     */
    public DebugLoggingOperationHandler(final OperationModelContext modelContext, final OperationContinuationHandlerContext context) {
        super(context);
        this.operationModelContext = modelContext;
    }

    @Override
    public OperationModelContext<EventStreamJsonMessage, EventStreamJsonMessage, EventStreamJsonMessage, EventStreamJsonMessage> getOperationModelContext() {
        return operationModelContext;
    }

    /**
     * Called when the underlying continuation is closed. Gives operations a chance to cleanup whatever
     * resources may be on the other end of an open stream. Also invoked when an underlying ServerConnection
     * is closed associated with the stream/continuation
     */
    @Override
    protected void onStreamClosed() {
        LOGGER.info("{} operation onStreamClosed()", operationModelContext.getOperationName());
    }

    @Override
    public EventStreamJsonMessage handleRequest(EventStreamJsonMessage request) {
        LOGGER.info("{} operation handleRequest() ::  {}", operationModelContext.getOperationName(),
                operationModelContext.getServiceModel().toJson(request));
        return new EventStreamJsonMessage() {
            @Override
            public byte[] toPayload(Gson gson) {
                return "{}".getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getApplicationModelType() {
                return operationModelContext.getResponseApplicationModelType();
            }
        };
    }

    @Override
    public void handleStreamEvent(EventStreamJsonMessage streamRequestEvent) {
        LOGGER.info("{} operation handleStreamEvent() ::  {}", operationModelContext.getOperationName(),
                operationModelContext.getServiceModel().toJson(streamRequestEvent));
    }
}
