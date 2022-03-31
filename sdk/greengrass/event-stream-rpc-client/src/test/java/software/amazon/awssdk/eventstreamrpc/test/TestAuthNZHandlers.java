package software.amazon.awssdk.eventstreamrpc.test;

import software.amazon.awssdk.crt.eventstream.Header;
import software.amazon.awssdk.crt.eventstream.HeaderType;
import software.amazon.awssdk.eventstreamrpc.AuthenticationData;
import software.amazon.awssdk.eventstreamrpc.AuthenticationHandler;
import software.amazon.awssdk.eventstreamrpc.Authorization;
import software.amazon.awssdk.eventstreamrpc.AuthorizationHandler;
import software.amazon.awssdk.eventstreamrpc.MessageAmendInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Example
 */
public class TestAuthNZHandlers {

    public static CompletableFuture<MessageAmendInfo> getClientAuth(String clientName) {
        final List<Header> headers = new ArrayList<>(1);
        headers.add(Header.createHeader("client-name", clientName));
        return CompletableFuture.completedFuture(new MessageAmendInfo(headers, null));
    }

    public static class ClientNameAuthenicationData implements AuthenticationData {
        private static String clientName;

        public ClientNameAuthenicationData(String clientName) {
            this.clientName = clientName;
        }

        /**
         * Return a human readable string for who the identity of the client/caller is. This
         * string must be appropriate for audit logs and enable tracing specific callers/clients
         * to relevant decision and operations executed
         *
         * @return
         */
        @Override
        public String getIdentityLabel() {
            return clientName;
        }
    }

    public static AuthenticationHandler getAuthNHandler() {
        /**
         * AuthN handler is privy to looking into the connect message and extracting whatever it needs/wants
         * from the headers and/or payload and populating whatever/however it wants into the AuthenticationData
         * type it decides is appropriate. The only restriction is that the associated Authorization handler
         * should understand the authentication produced.
         */
        return (List<Header> headers, byte[] bytes) -> {
            final Optional<Header> nameHeader = headers.stream()
                    .filter(header -> header.getName().equals("client-name")).findFirst();
            if (nameHeader.isPresent() && nameHeader.get().getHeaderType().equals(HeaderType.String)) {
                return new ClientNameAuthenicationData(nameHeader.get().getValueAsString());
            }
            throw new RuntimeException("Authentication failed to find client-name string header!");
        };
    }

    public static AuthorizationHandler getAuthZHandler() {
        return (AuthenticationData authN) -> {
            try {
                //framework checks for null authN so this shouldn't happen
                if (authN == null) {
                    throw new RuntimeException("Null AuthN data passed in!");
                }
                //check if the authN data type matches is a good idea, and necessary if there are other fields
                //to open/inspect
                if (authN instanceof ClientNameAuthenicationData) {
                    final ClientNameAuthenicationData data = (ClientNameAuthenicationData) authN;
                    //WARNING!!! this logic is for demonstration purposes
                    final String identityLabel = data.getIdentityLabel();
                    return identityLabel.startsWith("accepted.") ?
                            Authorization.ACCEPT : Authorization.REJECT;
                }
                //good idea to log reasons for rejection with the authentication label so testers/users can understand
                System.err.println("AuthN data type is not expected object type!");
                return Authorization.REJECT;
            } catch (Exception e) {
                return Authorization.REJECT;
            }
        };
    }
}
