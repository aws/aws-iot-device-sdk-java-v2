# Table of Contents

* [Introduction](#introduction)
* [MQTT5 differences relative to MQTT311 implementation](#mqtt5-differences-relative-to-mqtt311-implementation)
    + [Major Changes](#major-changes)
    + [Minor Changes](#minor-changes)
    + [Not Supported](#not-supported)
* [Getting Started with MQTT5](#getting-started-with-mqtt5)
    + [How to setup a MQTT5 builder based on desired connection method](#how-to-setup-mqtt5-builder-based-on-desired-connection-method)
        * [Direct MQTT with X509-based Mutual TLS Method](#direct-mqtt-with-x509-based-mutual-tls-method)
        * [Direct MQTT with Custom Authorizer Method](#direct-mqtt-with-custom-authorizer-method)
        * [Direct MQTT with PKCS11 Method](#direct-mqtt-with-pkcs11-method)
        * [Direct MQTT with PKCS12 Method](#direct-mqtt-with-pkcs12-method)
        * [Direct MQTT with Custom Key Operations Method](#direct-mqtt-with-custom-key-operation-method)
        * [Direct MQTT with Windows Certificate Store Method](#direct-mqtt-with-windows-certificate-store-method)
        * [Direct MQTT with Java Keystore Method](#direct-mqtt-with-java-keystore-method)
        * [Websocket Connection with Sigv4 Authentication Method](#websocket-connection-with-sigv4-authentication-method)
        * [Websocket Connection with Cognito Authentication Method](#websocket-connection-with-cognito-authentication-method)
    + [Adding an HTTP Proxy](#adding-an-http-proxy)
    + [How to create a MQTT5 client](#how-to-create-a-mqtt5-client)
        * [Lifecycle Management](#lifecycle-management)
    + [How to Start and Stop](#how-to-start-and-stop)
    + [Client Operations](#client-operations)
        + [Publish](#publish)
        + [Subscribe and Unsubscribe](#subscribe-and-unsubscribe)
    + [MQTT5 Best Practices](#mqtt5-best-practices)

# Introduction

This user guide is designed to act as a reference and guide for how to use MQTT5 with the Java SDK. This guide includes code snippets for how to make a MQTT5 client with proper configuration, how to connect to AWS IoT Core, how to perform operations and interact with AWS IoT Core through MQTT5, and some best practices for MQTT5.

If you are completely new to MQTT, it is highly recommended to check out the following resources to learn more about MQTT:

* MQTT.org getting started: https://mqtt.org/getting-started/
* MQTT.org FAQ (includes list of commonly used terms): https://mqtt.org/faq/
* MQTT on AWS IoT Core documentation: https://docs.aws.amazon.com/iot/latest/developerguide/mqtt.html
* MQTT 5 standard: https://docs.oasis-open.org/mqtt/mqtt/v5.0/os/mqtt-v5.0-os.html
* MQTT 311 standard: https://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html

This user guide expects some beginner level familiarity with MQTT and the terms used to describe MQTT.

# MQTT5 differences relative to MQTT311 implementation

MQTT5 support in the Java SDK comes from a separate client implementation. In doing so, we took the opportunity to incorporate feedback about the MQTT311 implementation that we could not apply without making breaking changes. If you're used to the MQTT311 implementation's API contract, there are a number of differences.

## Major Changes

* The MQTT5 client does not treat initial connection failures differently. With the MQTT311 implementation, a failure during initial connect would halt reconnects.
* The set of client lifecycle events is expanded and contains more detailed information whenever possible. All protocol data is exposed to the user.
* MQTT operations are completed with fully associated ACK packets when possible.
* New, optional behavior configuration:
    * [IoT Core specific validation](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.ExtendedValidationAndFlowControlOptions.html) - will validate and fail operations that break IoT Core specific restrictions
    * [IoT Core specific flow control](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.ExtendedValidationAndFlowControlOptions.html) - will apply flow control to honor IoT Core specific per-connection limits and quotas
    * [Flexible queue control](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.ClientOfflineQueueBehavior.html) - provides a number of options to control what happens to incomplete operations on a disconnection event.
* A [new API](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#getOperationStatistics()) has been added to query the internal state of the client’s operation queue. This API allows the user to make more informed flow control decisions before submitting operations to the client.
* Data can no longer back up on the socket. At most one frame of data is every pending-write on the socket.
* The MQTT5 client has a single message-received callback. Per-subscription callbacks are not supported.

## Minor Changes

* Public API terminology has changed. You `start()` or `stop()` the MQTT5 client rather than `Connect` or `Disconnect` like in MQTT311. This removes the semantic confusion with the connect/disconnect as the client-level controls vs. internal recurrent networking events.
* With the MQTT311 implementation, there were two separate objects: a client and a connection. With MQTT5, there is only the client.

## Not Supported

Not all parts of the MQTT5 spec are supported by the implementation. We currently do not support:

* AUTH packets and the authentication fields in the CONNECT packet.
* QoS 2

# Getting Started with MQTT5

This section covers how to use MQTT5 in the Java SDK. This includes how to setup a MQTT5 builder for making MQTT5 clients, how to connect to AWS IoT Core, and how to perform the operations with the MQTT5 client. Each section below contains code snippets showing the functionality in Java.

## How to setup MQTT5 builder based on desired connection method

All MQTT5 clients should be created using a MQTT5 client builder. A MQTT5 client builder is a factory of sorts for making MQTT5 clients, where you setup the builder and then can create fully configured MQTT5 clients from the settings setup in the builder. The Java SDK provides an easy to use builder designed to make it as easy as possible to get a configuration for common configuration types, like direct MQTT connections and websockets. Each configuration has various levels of flexibility and requirements in the information needed to authenticate a connection with AWS IoT Core. While MQTT5 clients can be created without the use of a MQTT5 client builder, it is strongly recommended to use a MQTT5 client builder when connecting to AWS IoT Core.

All MQTT connections to AWS IoT Core require a valid endpoint to connect to. For AWS IoT Core. You can find the endpoint to use for connecting in the AWS IoT Console under “Settings” or you can run the following command from the AWS CLI:

~~~ shell
aws iot describe-endpoint --endpoint-type "iot:Data-ATS"
~~~

Note that some MQTT client builders may also take file paths as inputs. These file paths can be either relative paths, like `../file.txt`, or full paths, like `C:\file.txt`. When possible, it is recommended to use full paths to these files to avoid issues when the application is moved to a different directory. Relative paths can be used for better portability across operating systems and files, but you will need to ensure the files are in the correct locations.

### **Direct MQTT with X509-based Mutual TLS Method**

A direct MQTT5 connection requires a valid endpoint, a client certificate in X.509 format, and a PEM encoded private key. To create a MQTT5 builder configured for this connection, see the following code:

~~~ java
String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(clientEndpoint, "<certificate file path>", "<private key file path>");
~~~

You can also create a client where the certificate and private key are in memory:

~~~ java
// Credit: https://stackoverflow.com/a/326440
static String readFile(String path, Charset encoding)
  throws IOException
{
  byte[] encoded = Files.readAllBytes(Paths.get(path));
  return new String(encoded, encoding);
}

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
String certificateData = readFile("<certificate file path>", StandardCharsets.UTF_8);
String keyData = readFile("<private key file path>", StandardCharsets.UTF_8);
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromMemory(clientEndpoint, certificateData, keyData);
~~~

### **Direct MQTT with Custom Authorizer Method**

A MQTT5 direct connection can be made using a [Custom Authorizer](https://docs.aws.amazon.com/iot/latest/developerguide/custom-authentication.html) rather than a certificate and key file like in the Direct Connection section above. Instead of using Mutual TLS to connect, a Custom Authorizer can be invoked instead and used to authorize the connection. When making a connection to a Custom Authorizer, the MQTT5 client can optionally passing username, password, and/or token signature arguments based on the configuration of the Custom Authorizer on AWS IoT Core.

You will need to setup your Custom Authorizer so that the lambda function returns a policy document to properly connect. See [this page on the documentation](https://docs.aws.amazon.com/iot/latest/developerguide/config-custom-auth.html) for more details and example return results.

If your Custom Authorizer does not use signing, you don't specify anything related to the token signature and can use the following code:

~~~ java
AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig customAuthConfig = new AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig();
customAuthConfig.authorizerName = "<Authorizer name>";
customAuthConfig.username = "<Value of the username field that should be passed to the authorizer's lambda>";
customAuthConfig.password = "<Binary data value of the password field that should be passed to the authorizer's lambda>".getBytes();

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithCustomAuth(clientEndpoint, customAuthConfig);
~~~

If your custom authorizer uses signing, you must specify the three signed token properties as well. It is your responsibility to URI-encode the username, authorizerName, and tokenKeyName parameters.

~~~ java
AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig customAuthConfig = new AwsIotMqtt5ClientBuilder.MqttConnectCustomAuthConfig();
customAuthConfig.authorizerName = "<Authorizer name>";
customAuthConfig.username = "<Value of the username field that should be passed to the authorizer's lambda>";
customAuthConfig.password = "<Binary data value of the password field that should be passed to the authorizer's lambda>".getBytes();
customAuthConfig.tokenValue = "<Name of the username query param that will contain the token value>";
customAuthConfig.tokenKeyName = "<Value of the username query param that holds the token value that has been signed>";
customAuthConfig.tokenSignature = "<URI-encoded base64-encoded digital signature of tokenValue>";

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithCustomAuth(clientEndpoint, customAuthConfig);
~~~

In both cases, the builder will construct a final CONNECT packet username field value for you based on the values configured. Do not add the token-signing fields to the value of the username that you assign within the custom authentication config structure. Similarly, do not add any custom authentication related values to the username in the CONNECT configuration optionally attached to the client configuration. The builder will do everything for you.

### **Direct MQTT with PKCS11 Method**

A MQTT5 direct connection can be made using a PKCS11 device rather than using a PEM encoded private key, the private key for mutual TLS is stored on a PKCS#11 compatible smart card or Hardware Security Module (HSM). To create a MQTT5 builder configured for this connection, see the following code:

~~~ java

Pkcs11Lib pkcs11Lib = new Pkcs11Lib("<path to PKCS11 library>");
TlsContextPkcs11Options pkcs11Options = new TlsContextPkcs11Options(pkcs11Lib)) {
pkcs11Options.withCertificateFilePath("<certificate file path>");
pkcs11Options.withUserPin("<pkcs11 user pin>");

// Pass arguments to help find the correct PKCS#11 token,
// and the private key on that token. You don't need to pass
// any of these arguments if your PKCS#11 device only has one
// token, or the token only has one private key. But if there
// are multiple tokens, or multiple keys to choose from, you
// must narrow down which one should be used.
/*
if (pkcs11TokenLabel != null && pkcs11TokenLabel != "") {
    pkcs11Options.withTokenLabel(pkcs11TokenLabel);
}
if (pkcs11SlotId != null) {
    pkcs11Options.withSlotId(pkcs11SlotId);
}
if (pkcs11KeyLabel != null && pkcs11KeyLabel != "") {
    pkcs11Options.withPrivateKeyObjectLabel(pkcs11KeyLabel);
}
*/

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPkcs11(clientEndpoint, pkcs11Options);
~~~

**Note**: Currently, TLS integration with PKCS#11 is only available on Unix devices.

### **Direct MQTT with PKCS12 Method**

A MQTT5 direct connection can be made using a PKCS12 file rather than using a PEM encoded private key. To create a MQTT5 builder configured for this connection, see the following code:

~~~ java

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPkcs12(clientEndpoint, "<PKCS12 file path>", "<PKCS12 password>");
~~~

**Note**: Currently, TLS integration with PKCS12 is only available on MacOS devices.

### **Direct MQTT with Custom Key Operation Method**

A MQTT5 direct connection can be made with a set of custom private key operations during the mutual TLS handshake. This is necessary if you require an external library to handle private key operations such as signing and decrypting. To create a MQTT5 builder configured for this connection, see the following code:

~~~ java
class MyKeyOperationHandler implements TlsKeyOperationHandler {

    // Implement based on the operation. See CustomKeyOpsConnect sample for example
    public void performOperation(TlsKeyOperation operation) {
        try {
            throw new RuntimeException("This is just an example!");
        } catch (Exception ex) {
            operation.completeExceptionally(ex);
        }
    }
}

MyKeyOperationHandler myKeyOperationHandler = new MyKeyOperationHandler();
TlsContextCustomKeyOperationOptions keyOperationOptions = new TlsContextCustomKeyOperationOptions(myKeyOperationHandler);
keyOperationOptions.withCertificateFilePath("<certificate file path>");

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMtlsCustomKeyOperationsBuilder(clientEndpoint, keyOperationOptions)
~~~

**Note**: Currently, Custom Key Operation support is only available on Linux devices.

### **Direct MQTT with Windows Certificate Store Method**

A MQTT5 direct connection can be made with mutual TLS with the certificate and private key in the [Windows certificate store](https://docs.microsoft.com/en-us/windows-hardware/drivers/install/certificate-stores), rather than simply being files on disk. To create a MQTT5 builder configured for this connection, see the following code:

~~~ java
// Certificate store path below is an example. See WindowsCert Connect sample for more info
String certificateStorePath = "CurrentUser\\MY\\A11F8A9B5DF5B98BA3508FBCA575D09570E0D2C6"
String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromWindowsCertStorePath(clientEndpoint, certificateStorePath)
~~~

**Note**: Windows Certificate Store connection support is only available on Windows devices.

### **Direct MQTT with Java Keystore Method**

A MQTT5 direct connection can be made with mutual TLS using the certificate and private key in a Java Keystore file.

To use the certificate and key files provided by AWS IoT Core, you will need to convert them into PKCS12 format and then import them into your Java keystore. You can convert the certificate and key file to PKCS12 using the following command:

```sh
openssl pkcs12 -export -in <my-certificate.pem.crt> -inkey <my-private-key.pem.key> -out my-pkcs12-key.p12 -name <certificate_alias> -password pass:<PKCS12_password>
```

Once you have a PKCS12 certificate and key, you can import it into a Java keystore using the following:

```sh
keytool -importkeystore -srckeystore my-pkcs12-key.p12 -destkeystore <destination_keystore.keys> -srcstoretype pkcs12 -alias <certificate_alias> -srcstorepass <PKCS12_password> -deststorepass <keystore_password>
```

With those steps completed and the PKCS12 key in the Java keystore, you can use the following code to load the certificate and private key from the Java keystore in the Java V2 SDK:

~~~ java
java.security.KeyStore keyStore;
try {
    keyStore = java.security.KeyStore.getInstance("PKCS12");
} catch (java.security.KeyStoreException ex) {
    throw new CrtRuntimeException("Could not get instance of Java keystore with format PKCS12");
}

String keyStorePath = "destination_keystore.keys";
String keyStorePassword = "keystore_password";

try (java.io.FileInputStream fileInputStream = new java.io.FileInputStream(keyStorePath)) {
    keyStore.load(fileInputStream, keyStorePassword.toCharArray());
} catch (java.io.FileNotFoundException ex) {
    throw new CrtRuntimeException("Could not open Java keystore file");
} catch (java.io.IOException | java.security.NoSuchAlgorithmException | java.security.cert.CertificateException ex) {
    throw new CrtRuntimeException("Could not load Java keystore");
}

String keyStoreCertificateAlias = "certificate_alias";
String keyStoreCertificatePassword = "PKCS12_password";

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithJavaKeystore(clientEndpoint, keyStore, keyStoreCertificateAlias, keyStoreCertificatePassword)
~~~

### **Websocket Connection with Sigv4 Authentication Method**

Sigv4-based authentication requires a credentials provider capable of sourcing valid AWS credentials. Sourced credentials will sign the websocket upgrade request made by the client while connecting. The default credentials provider chain supported by the SDK is capable of resolving credentials in a variety of environments according to a chain of priorities:

~~~
Environment -> Profile (local file system) -> STS Web Identity -> IMDS (ec2) or ECS
~~~

If the default credentials provider chain and built-in AWS region extraction logic are sufficient, you do not need to specify any additional configuration and can use the following code:

~~~ java
String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newWebsocketMqttBuilderWithSigv4Auth(clientEndpoint, null);
~~~

See the [authorizing direct AWS](https://docs.aws.amazon.com/iot/latest/developerguide/authorizing-direct-aws.html) page for documentation on how to get the AWS credentials, which then can be set to the `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, and `AWS_SESSION_TOKEN` environment variables prior to running the application.

Alternatively, if you're connecting to a special region for which standard pattern matching does not work, or if you need a specific credentials provider, you can specify advanced websocket configuration options using the following code:

~~~ java
WebsocketSigv4Config websocketConfig = new WebsocketSigv4Config();
websocketConfig.region = "us-east-1";
DefaultChainCredentialsProvider.DefaultChainCredentialsProviderBuilder providerBuilder = new DefaultChainCredentialsProvider.DefaultChainCredentialsProviderBuilder();
providerBuilder.withClientBootstrap(ClientBootstrap.getOrCreateStaticDefault());
websocketConfig.credentialsProvider = providerBuilder.build();

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newWebsocketMqttBuilderWithSigv4Auth(clientEndpoint, websocketConfig);
~~~

### **Websocket Connection with Cognito Authentication Method**

A MQTT5 websocket connection can be made using Cognito to authenticate rather than the AWS credentials located on the device or via key and certificate. Instead, Cognito can authenticate the connection using a valid Cognito identity ID. This requires a valid Cognito identity ID, which can be retrieved from a Cognito identity pool. A Cognito identity pool can be created from the AWS console.

To create a MQTT5 builder configured for this connection, see the following code:

~~~ java
WebsocketSigv4Config websocketConfig = new WebsocketSigv4Config();

CognitoCredentialsProvider.CognitoCredentialsProviderBuilder cognitoBuilder = new CognitoCredentialsProvider.CognitoCredentialsProviderBuilder();
// See https://docs.aws.amazon.com/general/latest/gr/cognito_identity.html for Cognito endpoints
String cognitoEndpoint = "cognito-identity.<region>.amazonaws.com";
cognitoBuilder.withEndpoint(cognitoEndpoint).withIdentity("<Cognito identity ID>");
cognitoBuilder.withClientBootstrap(ClientBootstrap.getOrCreateStaticDefault());
TlsContextOptions cognitoTlsContextOptions = TlsContextOptions.createDefaultClient();
ClientTlsContext cognitoTlsContext = new ClientTlsContext(cognitoTlsContextOptions);
cognitoTlsContextOptions.close();
cognitoBuilder.withTlsContext(cognitoTlsContext);
websocketConfig.credentialsProvider = cognitoBuilder.build();

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newWebsocketMqttBuilderWithSigv4Auth(clientEndpoint, websocketConfig);
~~~

**Note**: A Cognito identity ID is different from a Cognito identity pool ID and trying to connect with a Cognito identity pool ID will not work. If you are unable to connect, make sure you are passing a Cognito identity ID rather than a Cognito identity pool ID.

## **Adding an HTTP Proxy**

No matter what your connection transport or authentication method is, you may connect through an HTTP proxy by applying proxy configuration to the builder using the following code:

~~~ java
String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(clientEndpoint, "<certificate file path>", "<private key file path>");

HttpProxyOptions proxyOptions = new HttpProxyOptions();
proxyOptions.setHost("<proxy host>");
proxyOptions.setPort(<proxy port>);
builder.withHttpProxyOptions(proxyOptions);
~~~

SDK Proxy support also includes support for basic authentication and TLS-to-proxy. SDK proxy support does not include any additional proxy authentication methods (kerberos, NTLM, etc...) nor does it include non-HTTP proxies (SOCKS5, for example).


## How to create a MQTT5 client

Once a MQTT5 client builder has been created, it is ready to make a [MQTT5 client](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html). Something important to note is that once a MQTT5 client is built and finalized, the resulting MQTT5 client cannot have its settings modified! Further, modifications to the MQTT5 client builder will not change the settings of already created the MQTT5 clients. Before building a MQTT5 client from a MQTT5 client builder, make sure to have everything fully setup.

### Lifecycle Management

For almost every MQTT5 client, it is extremely important to setup [LifecycleEvents](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.LifecycleEvents.html) callbacks. [LifecycleEvents](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.LifecycleEvents.html) are invoked when the MQTT5 client connects, fails to connect, disconnects, and is stopped. Without these callbacks setup, it will be incredibly hard to determine the state of the MQTT5 client. To setup [LifecycleEvents](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.LifecycleEvents.html) callbacks, see the following code:

~~~ java
class MyLifecycleEvents implements Mqtt5ClientOptions.LifecycleEvents {
    @Override
    public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn onAttemptingConnectReturn) {
        System.out.println("Attempting to connect...");
    }
    @Override
    public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn onConnectionSuccessReturn) {
        System.out.println("Connection success!");
    }
    @Override
    public void onConnectionFailure(Mqtt5Client client, OnConnectionFailureReturn onConnectionFailureReturn) {
        System.out.println("Connection failed!");
    }
    @Override
    public void onDisconnection(Mqtt5Client client, OnDisconnectionReturn onDisconnectionReturn) {
        System.out.println("Disconnected!");
    }
    @Override
    public void onStopped(Mqtt5Client client, OnStoppedReturn onStoppedReturn) {
        System.out.println("Stopped!");
    }
}

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(clientEndpoint, "<certificate file path>", "<private key file path>");

MyLifecycleEvents lifecycleEvents = new MyLifecycleEvents();
builder.withLifeCycleEvents(lifecycleEvents);
~~~

[LifecycleEvents](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.LifecycleEvents.html) include the following:

* **onAttemptingConnect**
    * Invoked when the client begins to open a connection to the configured endpoint. A AttemptingConnection event will return a `OnAttemptingConnectionReturn`, which currently is empty but may include additional data in the future.
* **onConnectionSuccess**
    * Invoked when a connection attempt succeeds based on receipt of an affirmative CONNACK packet from the MQTT broker. A ConnectionSuccess event includes a `OnConnectionSuccessReturn`, which includes the MQTT broker's CONNACK packet, as well as a `NegotiatedSettings` structure which contains the final values for all variable MQTT session settings (based on protocol defaults, client wishes, and server response).
* **onConnectionFailure**
    * Invoked when a connection attempt fails at any point between DNS resolution and CONNACK receipt. A ConnectionFailure event includes a `OnConnectionFailureReturn`, which includes an error code and may also include a CONNACK if one was sent. If the remote endpoint sent a CONNACK with a failing reason code, the CONNACK packet will be included in the OnConnectionFailureReturn.
* **onDisconnection**
    * Invoked when the client's network connection is shut down, either by a local action, event, or a remote close or reset. Only emitted after a ConnectionSuccess event: a network connection that is shut down during the connecting process manifests as a ConnectionFailure event. A Disconnection event includes a `OnDisconnectionReturn` which will always include an error code, and if the Disconnect event is due to the receipt of a server-sent DISCONNECT packet, the packet will be included with the event data.
* **onStopped**
    * Invoked once the client has shutdown any associated network connection and entered an idle state where it will no longer attempt to reconnect. Only emitted after an invocation of `stop()` on the client. A stopped client may always be started again. A Stopped event will return a `OnStoppedReturn`, which currently is empty but may include additional data in the future.

If the MQTT5 client is going to subscribe and receive packets from the MQTT broker, it is important to also setup the [PublishEvents](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.PublishEvents.html) callback. This callback is invoked whenever the server sends a message to the client because the server received a message on a topic the client is subscribed to. For example, if you subscribe to `test/topic` and a packet is published to `test/topic`, then the `onMessageReceived` function in the PublishEvents callback will be invoked with a `PublishReturn` that includes the packet that was published to `test/topic`. With this callback, you can process messages made to subscribed topics. To setup the [PublishEvents](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.PublishEvents.html) callback, see the following code:

~~~ java
class MyPublishEvents implements Mqtt5ClientOptions.PublishEvents {
    @Override
    public void onMessageReceived(Mqtt5Client client, PublishReturn result) {
        System.out.println("Message received!");
    }
}

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(clientEndpoint, "<certificate file path>", "<private key file path>");

MyPublishEvents publishEvents = new MyPublishEvents();
builder.withPublishEvents(publishEvents);
~~~

[PublishEvents](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.PublishEvents.html) include the following:

* **onMessageReceived**
  * Invoked when a publish is received on a subscribed topic. A MessageReceived event includes a `PublishReturn`, which will include the `PublishPacket` that was sent from the MQTT broker.

_________

Once fully setup and configured, the MQTT5 client builder can create a MQTT5 client using the following code:

~~~ java
Mqtt5Client client = builder.build();
if (client == null) {
    System.out.println("Client creation failed!");
}
~~~

Note that you can create multiple MQTT5 clients with the same MQTT5 client builder. For example:

~~~ java
Mqtt5Client clientOne = builder.build();
if (clientOne == null) {
    System.out.println("Client one creation failed!");
}

Mqtt5Client clientTwo = builder.build();
if (clientTwo == null) {
    System.out.println("Client two creation failed!");
}
~~~

Once a MQTT5 client is created, it is ready to perform operations, which are described below.

## How to Start and Stop

A MQTT5 client can [start](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#start()) and stop a session as needed. Once started, the MQTT5 client will open the connection and allow packets to be sent and received. Likewise, once stopped, the MQTT5 client will close the connection and terminate the session. A closed client can be started  again by calling [start](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#start()).

To start a MQTT5 client, see the following code:

~~~ java
Mqtt5Client client = builder.build();
client.start();
~~~

_________

The [stop](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#stop(software.amazon.awssdk.crt.mqtt5.packets.DisconnectPacket)) API supports a [DisconnectPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/DisconnectPacket.html) as an optional parameter. If supplied, the [DisconnectPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/DisconnectPacket.html) will be sent to the server prior to closing the socket. To stop a MQTT5 client, see the following code:

~~~ java
DisconnectPacketBuilder disconnectBuilder = new DisconnectPacketBuilder();
disconnectBuilder.withReasonCode(DisconnectPacket.DisconnectReasonCode.NORMAL_DISCONNECTION);
client.stop(disconnectBuilder.build());
~~~

There is no promise returned by a call to [stop](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#stop(software.amazon.awssdk.crt.mqtt5.packets.DisconnectPacket)), but you may listen for the [onStopped](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.LifecycleEvents.html#onStopped(software.amazon.awssdk.crt.mqtt5.Mqtt5Client,software.amazon.awssdk.crt.mqtt5.OnStoppedReturn)) LifecycleEvent on the client.

Note that in the Java SDK, the MQTT5 client will automatically attempt to reconnect should it become disconnected for some reason, like the internet on the device going out for example, that is not a user initiated stop. This re-connection will happen automatically and can be configured in the MQTT5 client builder via the connection settings.

**Important:** When finished with an MQTT5 client, you **must call `close()`** on it or any associated native resource may leak:

~~~ java
DisconnectPacketBuilder disconnectBuilder = new DisconnectPacketBuilder();
disconnectBuilder.withReasonCode(DisconnectPacket.DisconnectReasonCode.NORMAL_DISCONNECTION);
client.stop(disconnectBuilder.build());

// Once fully finished with the Mqtt5Client:
client.close();
~~~

## Client Operations

### Publish

The [publish](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#publish(software.amazon.awssdk.crt.mqtt5.packets.PublishPacket)) operation takes a description of the PUBLISH packet you wish to send and returns a promise containing a [PublishResult](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/PublishResult.html). The returned [PublishResult](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/PublishResult.html) will contain different data depending on the QoS used in the publish.

* For QoS 0: Calling `getValue` will return `null` and the promise will be complete as soon as the packet has been written to the socket.
* For QoS 1: Calling `getValue` will return a [PubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/PubAckPacket.html) and the promise will be complete as soon as the PUBACK is received from the broker.

If the operation fails for any reason before these respective completion events, the promise is rejected with a descriptive error.
You should always check the reason code of a [PubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/PubAckPacket.html) completion to determine if a QoS 1 publish operation actually succeeded.

Once connected, the MQTT5 client can publish to a topic using the following code:

~~~ java
PublishPacketBuilder publishBuilder = new PublishPacketBuilder();
publishBuilder.withTopic("hello/world").withPayload("Hello World".getBytes());
CompletableFuture<PublishResult> published = client.publish(publishBuilder.build());
PublishResult result = published.get(60, TimeUnit.SECONDS);
~~~

The publish packet has many different options which can be configured to allow for different QoS levels, user properties, etc. For example, to make a publish with QoS 1 with a single user property:

~~~ java
PublishPacketBuilder publishBuilder = new PublishPacketBuilder();
publishBuilder.withTopic("hello/world/qos1").withPayload("Hello World".getBytes()).withQOS(QOS.AT_LEAST_ONCE);

ArrayList<UserProperty> userProperties = new ArrayList<UserProperty>();
userProperties.add(new UserProperty("User", "Property"));
publishBuilder.withUserProperties(userProperties);

CompletableFuture<PublishResult> published = client.publish(publishBuilder.build());
PublishResult result = published.get(60, TimeUnit.SECONDS);

~~~

Note that publishes made while a MQTT5 client is disconnected and offline will be put into a queue. Once reconnected, the MQTT5 client will send any publishes made while disconnected and offline automatically.

### Subscribe and Unsubscribe

The [subscribe](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#subscribe(software.amazon.awssdk.crt.mqtt5.packets.SubscribePacket)) operation takes a description of the [SubscribePacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/SubscribePacket.html) you wish to send and returns a promise that resolves successfully with the corresponding [SubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/SubAckPacket.html) returned by the broker; the promise is rejected with an error if anything goes wrong before the [SubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/SubAckPacket.html) is received.
You should always check the reason codes of a [SubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/SubAckPacket.html) completion to determine if the subscribe operation actually succeeded.

Once connected, the MQTT5 client can subscribe to one or more topics using the following code:

~~~ java
SubscribePacketBuilder subBuilder = new SubscribePacketBuilder();
subBuilder.withSubscription("hello/world/qos0", QOS.AT_MOST_ONCE);
subBuilder.withSubscription("hello/world/qos1", QOS.AT_LEAST_ONCE);
client.subscribe(subBuilder.build()).get(60, TimeUnit.SECONDS);
~~~

Once a MQTT5 client is subscribed, if a publish packet is received on a subscribed topic, the MQTT5 client publish callback will be invoked with the received publish packet.

_________

The [unsubscribe](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#unsubscribe(software.amazon.awssdk.crt.mqtt5.packets.UnsubscribePacket)) operation takes a description of the [UnsubscribePacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubscribePacket.html) you wish to send and returns a promise that resolves successfully with the corresponding [UnsubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubAckPacket.html) returned by the broker; the promise is rejected with an error if anything goes wrong before the [UnsubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubAckPacket.html) is received.
You should always check the reason codes of a [UnsubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubAckPacket.html) completion to determine if the unsubscribe operation actually succeeded.

The MQTT5 client can unsubscribe from one or more topics using the following:

~~~ java
UnsubscribePacketBuilder unsubBuilder = new UnsubscribePacketBuilder();
unsubBuilder.withSubscription("hello/world/qos0");
unsubBuilder.withSubscription("hello/world/qos1");
client.unsubscribe(unsubBuilder.build()).get(60, TimeUnit.SECONDS);
~~~

## MQTT5 Best Practices

Below are some best practices for the MQTT5 client that are recommended to follow for the best development experience:

* When creating MQTT5 clients, make sure to use ClientIDs that are unique! If you connect two MQTT5 clients with the same ClientID, they will Disconnect each other! If you do not configure a ClientID, the MQTT5 server will automatically assign one.
* Use the minimum QoS you can get away with for the lowest latency and bandwidth costs. For example, if you are sending data consistently multiple times per second and do not have to have a guarantee the server got each and every publish, using QoS 0 may be ideal compared to QoS 1. Of course, this heavily depends on your use case but generally it is recommended to use the lowest QoS possible.
* If you are getting unexpected disconnects when trying to connect to AWS IoT Core, make sure to check your IoT Core Thing’s policy and permissions to make sure your device is has the permissions it needs to connect!
* Make sure to always call `close()` when finished a MQTT5 client to avoid native resource leaks!
* For [publish](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#publish(software.amazon.awssdk.crt.mqtt5.packets.PublishPacket)), [subscribe](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#subscribe(software.amazon.awssdk.crt.mqtt5.packets.SubscribePacket)), and [unsubscribe](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#unsubscribe(software.amazon.awssdk.crt.mqtt5.packets.UnsubscribePacket)), make sure to check the reason codes in the ACK ([PubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/PubAckPacket.html), [SubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/SubAckPacket.html), and [UnsubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubAckPacket.html) respectively) to see if the operation actually succeeded.
* You MUST NOT perform blocking operations on any callback, or you will cause a deadlock. For example: in the `onMessageReceived` callback, do not send a publish, and then wait for the future to complete within the callback. The Client cannot do work until your callback returns, so the thread will be stuck.
