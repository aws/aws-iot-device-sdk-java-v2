package software.amazon.awssdk.eventstreamrpc;

/**
 * Thrown when a invalid service configuration exception occurs
 */
public class InvalidServiceConfigurationException extends RuntimeException {
    /**
     * Constructs a new InvalidServiceConfigurationException with the given message
     * @param msg The message to associate with the exception
     */
    public InvalidServiceConfigurationException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new InvalidServiceConfigurationException with the given message and cause
     * @param msg The message to associate with the exception
     * @param cause The cause to associate with the exception
     */
    public InvalidServiceConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs a new InvalidServiceConfigurationException with the given cause
     * @param cause The cause to associate with the exception
     */
    public InvalidServiceConfigurationException(Throwable cause) {
        super(cause);
    }
}
