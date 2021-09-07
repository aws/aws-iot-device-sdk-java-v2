package software.amazon.awssdk.eventstreamrpc.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Root error type returned by any continuation error message
 *
 * TODO: To mimic public AWS SDK clients, any exception thrown by
 *       a given service should inherit from it's generated model
 *       service exception.
 */
public abstract class EventStreamOperationError
        extends RuntimeException
        implements EventStreamJsonMessage {
    @SerializedName("_service")
    @Expose(serialize = true, deserialize = true)
    private final String _service;

    @SerializedName("_message")
    @Expose(serialize = true, deserialize = true)
    private final String _message;

    @SerializedName("_errorCode")
    @Expose(serialize = true, deserialize = true)
    private final String _errorCode;

    public EventStreamOperationError(final String serviceName, final String errorCode, final String message) {
        super(String.format("%s[%s]: %s", errorCode, serviceName, message));
        this._service = serviceName;
        this._errorCode = errorCode;
        this._message = message;
    }

    public String getService() {
        return _service;
    }

    /**
     * Likely overridden by a specific field defined in service-operation model
     * @return - message
     */
    public String getMessage() {
        return _message;
    }

    /**
     * Likely subclasses will have a more limited set of valid error codes
     * @return - error code
     */
    public String getErrorCode() { return _errorCode; }
}
