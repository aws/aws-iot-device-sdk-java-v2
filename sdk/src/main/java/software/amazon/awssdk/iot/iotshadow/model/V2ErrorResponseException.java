package software.amazon.awssdk.iot.iotshadow.model;

import software.amazon.awssdk.crt.CrtRuntimeException;

public class V2ErrorResponseException extends CrtRuntimeException {
    private final V2ErrorResponse modeledError;

    public V2ErrorResponseException(String msg) {
        super(msg);
        this.modeledError = null;
    }

    public V2ErrorResponseException(String msg, V2ErrorResponse modeledError) {
        super(msg);
        this.modeledError = modeledError;
    }

    public V2ErrorResponse getModeledError() {
        return this.modeledError;
    }
}