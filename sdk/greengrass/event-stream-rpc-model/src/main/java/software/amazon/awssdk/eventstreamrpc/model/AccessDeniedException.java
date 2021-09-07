package software.amazon.awssdk.eventstreamrpc.model;

public class AccessDeniedException extends EventStreamOperationError {
    public static final String ERROR_CODE = "aws#AccessDenied";

    /**
     * Message constructor may reveal what operation or resource was denied access
     * or the principal/authN that was rejected
     *
     * Do not overexpose reason or logic for AccessDenied. Prefer internal logging
     *
     * @param serviceName - serviceName
     * @param message - message
     */
    public AccessDeniedException(String serviceName, String message) {
        super(serviceName, ERROR_CODE, message);
    }

    public AccessDeniedException(String serviceName) {
        super(serviceName, ERROR_CODE, "AccessDenied");
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
