package software.amazon.awssdk.eventstreamrpc;

import com.google.gson.Gson;
import software.amazon.awssdk.crt.eventstream.Header;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class GreengrassConnectMessageSupplier {
    
    public static Supplier<MessageAmendInfo> connectMessageSupplier(String authToken) {
        return () -> {
            final List<Header> headers = new LinkedList<>();
            GreengrassEventStreamConnectMessage connectMessage = new GreengrassEventStreamConnectMessage();
            connectMessage.setAuthToken(authToken);
            String payload = new Gson().toJson(connectMessage);
            return new MessageAmendInfo(headers, payload.getBytes(StandardCharsets.UTF_8));
        };
    }
}
