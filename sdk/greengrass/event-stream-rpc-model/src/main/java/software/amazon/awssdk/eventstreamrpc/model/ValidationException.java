package software.amazon.awssdk.eventstreamrpc.model;

public class ValidationException extends EventStreamOperationError {
    public static final String ERROR_CODE = "aws#ValidationException";

    public ValidationException(String serviceName, String message) {
        super(serviceName, ERROR_CODE, message);
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
