package software.amazon.awssdk.eventstreamrpc.model;

public class UnsupportedOperationException extends EventStreamOperationError {
    public static final String ERROR_CODE = "aws#UnsupportedOperation";

    public UnsupportedOperationException(String serviceName, String operationName) {
        super(serviceName, ERROR_CODE, "UnsupportedOperation: " + operationName);
    }

    /**
     * Returns the named model type. May be used for a header.
     *
     * @return - Application Model Type
     */
    @Override
    public String getApplicationModelType() {
        return ERROR_CODE;
    }
}
