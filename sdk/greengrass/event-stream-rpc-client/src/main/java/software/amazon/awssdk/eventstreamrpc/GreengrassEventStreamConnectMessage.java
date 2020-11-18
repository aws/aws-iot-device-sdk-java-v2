package software.amazon.awssdk.eventstreamrpc;

public class GreengrassEventStreamConnectMessage {

    private String authToken;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return this.authToken;
    }
}
