# MQTT5 PubSub

[**Return to main sample list**](../../README.md)

This sample uses the
[Message Broker](https://docs.aws.amazon.com/iot/latest/developerguide/iot-message-broker.html)
for AWS IoT to send and receive messages through an MQTT connection using MQTT5.

MQTT5 introduces additional features and enhancements that improve the development experience with MQTT. You can read more about MQTT5 in the Java V2 SDK by checking out the [MQTT5 user guide](../../../documents/MQTT5_Userguide.md).

Your IoT Core Thing's [Policy](https://docs.aws.amazon.com/iot/latest/developerguide/iot-policies.html) must provide privileges for this sample to connect, subscribe, publish, and receive. Below is a sample policy that can be used on your IoT Core Thing that will allow this sample to run as intended.

<details>
<summary>(see sample policy)</summary>
<pre>
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "iot:Publish",
        "iot:Receive"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/test/topic"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Subscribe"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/test/topic"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Connect"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:client/test-*"
      ]
    }
  ]
}
</pre>

Replace with the following with the data from your AWS account:
* `<region>`: The AWS IoT Core region where you created your AWS IoT Core thing you wish to use with this sample. For example `us-east-1`.
* `<account>`: Your AWS IoT Core account ID. This is the set of numbers in the top right next to your AWS account name when using the AWS IoT Core website.
* `<thingname>`: The name of your AWS IoT Core thing you want the device connection to be associated with

Note that in a real application, you may want to avoid the use of wildcards in your ClientID or use them selectively. Please follow best practices when working with AWS on production applications using the SDK. Also, for the purposes of this sample, please make sure your policy allows a client ID of `test-*` to connect or use `--client_id <client ID here>` to send the client ID your policy supports.

</details>

## How to run

### Direct MQTT via mTLS

To Run this sample using a direct MQTT connection with a key and certificate, use the following command:

```sh
mvn compile exec:java -pl samples/Mqtt5/PubSub -Dexec.mainClass=mqtt5.pubsub.PubSub -Dexec.args='--endpoint <endpoint> --cert <path to certificate> --key <path to private key>'
```

You can also pass a Certificate Authority file (CA) if your certificate and key combination requires it:

```sh
mvn compile exec:java -pl samples/Mqtt5/PubSub -Dexec.mainClass=mqtt5.pubsub.PubSub -Dexec.args='--endpoint <endpoint> --cert <path to certificate> --key <path to private key> --ca_file <path to root CA>'
```
### Websockets

To Run this sample using Websockets, use the following command:
```sh
mvn compile exec:java -pl samples/Mqtt5/PubSub -Dexec.mainClass=mqtt5.pubsub.PubSub -Dexec.args='--endpoint <endpoint> --signing_region <region>'
```

Note that to run this sample using Websockets, you will need to set your AWS credentials in your environment variables or local files. See the [authorizing direct AWS](https://docs.aws.amazon.com/iot/latest/developerguide/authorizing-direct-aws.html) page for documentation on how to get the AWS credentials, which then you can set to the `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS`, and `AWS_SESSION_TOKEN` environment variables.

## Alternate Connection Configuration Methods supported by AWS IoT Core

All MQTT5 clients should be created using a MQTT5 client builder. A MQTT5 client builder is a factory of sorts for making MQTT5 clients, where you setup the builder and then can create fully configured MQTT5 clients from the settings setup in the builder. The Java SDK provides an easy to use builder designed to make it as easy as possible to get a configuration for common configuration types, like direct MQTT connections and websockets. Each configuration has various levels of flexibility and requirements in the information needed to authenticate a connection with AWS IoT Core. While MQTT5 clients can be created without the use of a MQTT5 client builder, it is strongly recommended to use a MQTT5 client builder when connecting to AWS IoT Core.

### Authentication Methods

* [Direct MQTT with X509-based Mutual TLS Method](#direct-mqtt-with-x509-based-mutual-tls-method)
* [Direct MQTT with Custom Authorizer Method](#direct-mqtt-with-custom-authorizer-method)
* [Direct MQTT with PKCS11 Method](#direct-mqtt-with-pkcs11-method)
* [Direct MQTT with PKCS12 Method](#direct-mqtt-with-pkcs12-method)
* [Direct MQTT with Custom Key Operations Method](#direct-mqtt-with-custom-key-operation-method)
* [Direct MQTT with Windows Certificate Store Method](#direct-mqtt-with-windows-certificate-store-method)
* [Direct MQTT with Java Keystore Method](#direct-mqtt-with-java-keystore-method)
* [Websocket Connection with Sigv4 Authentication Method](#websocket-connection-with-sigv4-authentication-method)
* [Websocket Connection with Cognito Authentication Method](#websocket-connection-with-cognito-authentication-method)
### HTTP Proxy
* [Adding an HTTP Proxy](#adding-an-http-proxy)

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
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newMtlsBuilder(clientEndpoint, certificateData, keyData);
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

If your custom authorizer uses signing, you must specify the three signed token properties as well. It is your responsibility to URI-encode the Username, AuthorizerName, and TokenKeyName parameters.

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
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPkcs11(clientEndpoint, "<PKCS12 file path>", "<PKCS12 password>");
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

See the [authorizing direct AWS](https://docs.aws.amazon.com/iot/latest/developerguide/authorizing-direct-aws.html) page for documentation on how to get the AWS credentials, which then can be set to the `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS`, and `AWS_SESSION_TOKEN` environment variables prior to running the application.

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