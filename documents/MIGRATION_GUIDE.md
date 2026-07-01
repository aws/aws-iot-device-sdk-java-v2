# Migrate from v1 to v2 of the AWS IoT SDK for Java

The AWS IoT Device SDK for Java v2 is a major rewrite of the AWS IoT Device SDK for Java v1 code base built on top of Java 8+. It includes many updates,
such as improved consistency, ease of use, more detailed information about client status, and an offline operation queue
control. This guide describes the major features that are new in the v2 SDK, and provides guidance on how to migrate
your code to v2 from v1 of the AWS IoT SDK for Java.

> [!NOTE]  
> If you can't find the information you need in this guide, visit the [How to get help](#how-to-get-help) section for more help and guidance.

* [What's new in AWS IoT Device SDK for Java v2](#whats-new-in-aws-iot-device-sdk-for-java-v2)
* [How to get started with AWS IoT Device SDK for Java v2](#how-to-get-started-with-aws-iot-device-sdk-for-java-v2)
    * [Package name change](#package-name-change)
    * [MQTT protocol](#mqtt-protocol)
    * [Client builder](#client-builder)
    * [Client start](#client-start)
    * [Connection types and features](#connection-types-and-features)
    * [Lifecycle events](#lifecycle-events)
    * [Publish](#publish)
    * [Subscribe](#subscribe)
    * [Unsubscribe](#unsubscribe)
    * [Client stop](#client-stop)
    * [Client shutdown](#client-shutdown)
    * [Reconnects](#reconnects)
    * [Offline operations queue](#offline-operations-queue)
    * [Operation timeouts](#operation-timeouts)
    * [Logging](#logging)
    * [Client for AWS IoT Device Shadow](#client-for-aws-iot-device-shadow)
    * [Client for AWS IoT Jobs](#client-for-aws-iot-jobs)
    * [Client for AWS IoT fleet provisioning](#client-for-aws-iot-fleet-provisioning)
    * [Example](#example)
* [How to get help](#how-to-get-help)
* [Appendix](#appendix)
    * [MQTT5 features](#mqtt5-features)

## What's new in AWS IoT Device SDK for Java v2

* The v2 SDK clients are truly async. Operations return `CompletableFuture` objects. Blocking calls can be emulated by waiting
for the returned `CompletableFuture` object to be resolved.
* The v2 SDK provides an MQTT5 Client which implements the MQTT5 protocol, the next step in evolution of the MQTT protocol.
* The v2 SDK supports AWS IoT services such as Jobs and fleet provisioning.

## How to get started with AWS IoT Device SDK for Java v2

Public API for almost all actions and operations has changed significantly. There're differences between the v1 SDK and
the v2 SDK. This section describes the changes you need to apply to your project with the v1 SDK to start using the v2 SDK.
For more information about MQTT5, visit [MQTT5 User Guide](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#getting-started-with-mqtt5).

### Package name change

Package names for the v2 SDK now begin with `software.amazon.awssdk` instead of `com.amazonaws` which was used by the v1 SDK.

These same names differentiate Maven artifacts from v1 to v2. Maven artifacts for the v2 SDK use the `software.amazon.awssdk`
groupId, whereas the v1 SDK uses the `com.amazonaws` groupId.

#### Example of adding the v1 SDK to Maven project

```
<dependency>
  <groupId>com.amazonaws</groupId>
  <artifactId>aws-iot-device-sdk-java</artifactId>
  <version>1.3.9</version>
</dependency>
```

#### Example of adding the v2 SDK to Maven project

```
<dependency>
  <groupId>software.amazon.awssdk.iotdevicesdk</groupId>
  <artifactId>aws-iot-device-sdk</artifactId>
  <version>1.19.0</version>
</dependency>
```

### MQTT protocol

The v1 SDK uses an MQTT version 3.1.1 client under the hood.

The v2 SDK provides MQTT version 3.1.1 and MQTT version 5.0 client implementations. This guide focuses on MQTT5 because
this version is a significant improvement over MQTT3. For more information, see the [MQTT5 features](#mqtt5-features) section.

### Client builder

To access AWS IoT services, you must initialize an MQTT client.

In the v1 SDK, the [AWSIotMqttClient](http://aws-iot-device-sdk-java-docs.s3-website-us-east-1.amazonaws.com/com/amazonaws/services/iot/client/AWSIotMqttClient.html)
class represents an MQTT client. You instantiate the client directly by passing all the required parameters to the class
constructor. It's possible to change the client settings after its creation using the `set*` methods like `setKeepAliveInterval`
or `setMaxConnectionRetries`.

In the v2 SDK, the [Mqtt5Client](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html)
class represents an MQTT client, specifically for MQTT5 protocol. The v2 SDK provides an [MQTT5 client builder](https://aws.github.io/aws-iot-device-sdk-java-v2/software/amazon/awssdk/iot/AwsIotMqtt5ClientBuilder.html)
designed to easily create common configuration types such as direct MQTT or WebSocket connections. After an MQTT5 client
is built and finalized, the settings of the resulting MQTT5 client cannot be modified.

#### Example of creating a client in the v1 SDK

```java
String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
String clientId = "<unique client id>";
String certificateFile = "<certificate file>";  // X.509 based certificate file
String privateKeyFile = "<private key file>";   // PEM encoded private key file

KeyStorePasswordPair pair =
    SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
AWSIotMqttClient client =
    new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
```

#### Example of creating a client in the v2 SDK

The v2 SDK supports different connection types. Given the same input parameters as in the v1 example above, the most
recommended method to create an MQTT5 client will be [newDirectMqttBuilderWithMtlsFromPath](https://aws.github.io/aws-iot-device-sdk-java-v2/software/amazon/awssdk/iot/AwsIotMqtt5ClientBuilder.html#newDirectMqttBuilderWithMtlsFromPath(java.lang.String,java.lang.String,java.lang.String)).

```java
String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
String clientId = "<unique client id>";
String certificateFile = "<certificate file>";  // X.509 based certificate file
String privateKeyFile = "<private key file>";   // PEM encoded private key file

AwsIotMqtt5ClientBuilder builder =
  AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(clientEndpoint, certificateFile, privateKeyFile);

ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
connectProperties.withClientId(clientId);
builder.withConnectProperties(connectProperties);

Mqtt5Client client = builder.build();
```

For more information, refer to the [Connection types and features](#connection-types-and-features) section for other connection types supported by the v2 SDK.


### Client start

To connect to the server in the v1 SDK, you call the `connect` method on an `AWSIotMqttClient` instance.

The v2 SDK changed API terminology. You `start` the MQTT5 client rather than `connect` as in the v1 SDK. This removes
the semantic confusion between client-level controls and internal recurrent networking events related to connection.

#### Example of connecting to a server in the v1 SDK

```java
AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, keyStore, keyPassword);
client.connect();
```

#### Example of connecting to a server in the v2 SDK

```java
Mqtt5Client client = clientBuilder.build();
client.start();
```


### Connection types and features

The v1 SDK supports two types of connections to the AWS IoT service: MQTT with X.509 certificate and MQTT over
Secure WebSocket with SigV4 authentication.

The v2 SDK adds a collection of connection types and cryptography formats (e.g. [PKCS #11](https://en.wikipedia.org/wiki/PKCS_11)
and [Custom Authorizer](https://docs.aws.amazon.com/iot/latest/developerguide/custom-authentication.html)), credential
providers (e.g. [Windows Certificate Store](https://learn.microsoft.com/en-us/windows-hardware/drivers/install/certificate-stores)),
and other connection-related features.

For more information, refer to the [How to set up MQTT5 builder based on desired connection method](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#how-to-setup-mqtt5-builder-based-on-desired-connection-method)
section of the MQTT5 user guide for detailed information and code snippets on each connection type and connection feature.

| Connection type/feature                                  | v1 SDK                                              | v2 SDK                            | User guide |
|----------------------------------------------------------|-----------------------------------------------------|-----------------------------------|:----------:|
| MQTT over Secure WebSocket with AWS SigV4 authentication | $${\Large\color{green}&#10004;}$$                   | $${\Large\color{green}&#10004;}$$ | [link](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#websocket-connection-with-sigv4-authentication-method) |
| MQTT with Java KeyStore Method                           | $${\Large\color{green}&#10004;}$$                   | $${\Large\color{green}&#10004;}$$ | [link](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#direct-mqtt-with-java-keystore-method) |
| Websocket Connection with Cognito Authentication Method  | $${\Large\color{green}&#10004;}$$                   | $${\Large\color{green}&#10004;}$$ | [link](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#websocket-connection-with-cognito-authentication-method) |
| MQTT with X.509 certificate based mutual authentication  | $${\Large\color{orange}&#10004;}$$<sup>\*</sup>     | $${\Large\color{green}&#10004;}$$ | [link](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#direct-mqtt-with-x509-based-mutual-tls-method) |
| MQTT with PKCS12 Method                                  | $${\Large\color{orange}&#10004;}$$<sup>\*</sup>     | $${\Large\color{green}&#10004;}$$ | [link](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#direct-mqtt-with-pkcs12-method) |
| MQTT with Custom Key Operation Method                    | $${\Large\color{orange}&#10004;}$$<sup>\*</sup>     | $${\Large\color{green}&#10004;}$$ | [link](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#direct-mqtt-with-custom-key-operation-method) |
| MQTT with Custom Authorizer Method                       | $${\Large\color{orange}&#10004;}$$<sup>\*\*</sup>   | $${\Large\color{green}&#10004;}$$ | [link](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#direct-mqtt-with-custom-authorizer-method) |
| MQTT with Windows Certificate Store Method               | $${\Large\color{red}&#10008;}$$                     | $${\Large\color{green}&#10004;}$$ | [link](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#direct-mqtt-with-windows-certificate-store-method) |
| MQTT with PKCS11 Method                                  | $${\Large\color{red}&#10008;}$$                     | $${\Large\color{green}&#10004;}$$ | [link](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#direct-mqtt-with-pkcs11-method) |
| HTTP Proxy                                               | $${\Large\color{orange}&#10004;}$$<sup>\*\*\*</sup> | $${\Large\color{green}&#10004;}$$ | [link](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#http-proxy) |

${\Large\color{orange}&#10004;}$<sup>\*</sup> - To get this connection type work in the v1 SDK, you need to create KeyStore.\
${\Large\color{orange}&#10004;}$<sup>\*\*</sup> - To get this connection type work in the v1 SDK, you need to implement the
[Custom Authentication workflow](https://docs.aws.amazon.com/iot/latest/developerguide/custom-authorizer.html).\
${\Large\color{orange}&#10004;}$<sup>\*\*\*</sup> - The v1 SDK does not allow specifying HTTP proxy, but it is possible to configure
systemwide proxy.

#### Example of creating connection using KeyStore in the v1 SDK

```java
String keyStoreFile = "<my.keystore>";
String keyStorePassword = "<keystore-password>";
String keyPassword = "<key-password>";

KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
keyStore.load(new FileInputStream(keyStoreFile), keyStorePassword.toCharArray());

String clientEndpoint = "<prefix>.iot.<region>.amazonaws.com";
String clientId = "<unique client id>";

AWSIotMqttClient client =
        new AWSIotMqttClient(clientEndpoint, clientId, keyStore, keyPassword);

// Connect to server.
client.connect();
```

#### Example of creating connection using KeyStore in the v2 SDK

[newDirectMqttBuilderWithJavaKeystore](https://aws.github.io/aws-iot-device-sdk-java-v2/software/amazon/awssdk/iot/AwsIotMqtt5ClientBuilder.html#newDirectMqttBuilderWithJavaKeystore(java.lang.String,java.security.KeyStore,java.lang.String,java.lang.String))
requires a  `certificateAlias` parameter to ensure that the correct certificate is used. In the v1 SDK, only the first
certificate in the KeyStore file will be used (see [SSLContext documentation](https://docs.oracle.com/javase/6/docs/api/javax/net/ssl/SSLContext.html#init(javax.net.ssl.KeyManager[],%20javax.net.ssl.TrustManager[],%20java.security.SecureRandom))),
which can be confusing.

```java
String keyStoreFile = "<my.keystore>";
String keyStorePassword = "<keystore-password>";
String certificateAlias = "<certificate-alias>";
String keyPassword = "<key-password>";

KeyStore keyStore = KeyStore.getDefaultType();
keyStore.load(new FileInputStream(keyStoreFile), keyStorePassword.toCharArray());

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
String clientId = "<unique client id>";

AwsIotMqtt5ClientBuilder builder =
        AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithJavaKeystore(
            clientEndpoint,
            keyStore,
            certificateAlias,
            keyPassword);

ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
connectProperties.withClientId(clientId);
builder.withConnectProperties(connectProperties);

Mqtt5Client client = builder.build();

// Connect to server.
client.start();
```

### Lifecycle events

Both v1 and v2 SDKs provide lifecycle events for the MQTT clients.

The v1 SDK provides three lifecycle events: *on connection success*, *on connection failure*, and *on connection closed*. You can
supply a custom callback function via subclassing `AWSIotMqttClient`. It is recommended to use lifecycle events callbacks
to help determine the state of the MQTT client during the operation.

The v2 SDK adds two new lifecycle events and provides five lifecycle events in total: *on connection success*, *on connection failure*,
*on disconnect* (the same as *on connection closed* in the v1 SDK), *on stopped*, and *on attempting connect*. Enabling lifecycle
events is mandatory in the v2 SDK.

For more information, refer to the [MQTT5 user guide](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/documents/MQTT5_Userguide.md#how-to-create-a-mqtt5-client).

#### Example of setting lifecycle events in the v1 SDK

```java
class MyClient extends AWSIotMqttClient {
    @Override
    public void onConnectionSuccess() {
    }

    @Override
    public void onConnectionFailure() {
    }

    @Override
    public void onConnectionClosed() {
    }
}

MyClient client = new MyClient(/*...*/);
```

#### Example of setting lifecycle events in the v2 SDK

```java
class MyLifecycleEvents implements Mqtt5ClientOptions.LifecycleEvents {
    @Override
    public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn onAttemptingConnectReturn) {
    }

    @Override
    public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn onConnectionSuccessReturn) {
    }
    
    @Override
    public void onConnectionFailure(Mqtt5Client client, OnConnectionFailureReturn onConnectionFailureReturn) {
    }

    @Override
    public void onDisconnection(Mqtt5Client client, OnDisconnectionReturn onDisconnectionReturn) {
    }

    @Override
    public void onStopped(Mqtt5Client client, OnStoppedReturn onStoppedReturn) {
    }
}

String clientEndpoint = "<prefix>-ats.iot.<region>.amazonaws.com";
AwsIotMqtt5ClientBuilder builder =
  AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(clientEndpoint, "<certificate file path>", "<private key file path>");

MyLifecycleEvents lifecycleEvents = new MyLifecycleEvents();
builder.withLifeCycleEvents(lifecycleEvents);

Mqtt5Client client = builder.build();
```

### Publish

The v1 SDK provides two API calls for publishing: blocking and non-blocking. For the non-blocking version, the result of the
publish operation is reported via a set of callbacks. If you try to publish to a topic that is not allowed by a policy,
AWS IoT Core service will close the connection.

The v2 SDK provides only asynchronous non-blocking API. [PublishPacketBuilder](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/PublishPacket.PublishPacketBuilder.html)
creates a [PublishPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/PublishPacket.html)
object that contains a description of the PUBLISH packet. The [publish](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#publish(software.amazon.awssdk.crt.mqtt5.packets.PublishPacket))
operation takes a `PublishPacket` instance and returns a promise that contains a [PublishResult](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/PublishResult.html).
The returned `PublishResult` will contain different data depending on the `QoS` used in the publish.

> [!NOTE]
> If you try to publish with the v2 MQTT5 client to a topic that is not allowed by a policy, you do not get the connection
> closed but instead receive a PUBACK with a reason code.

* For QoS 0 (AT_MOST_ONCE): Calling `getValue` will return `null` and the promise will be complete as soon as the packet
has been written to the socket.
* For QoS 1 (AT_LEAST_ONCE): Calling `getValue` will return a [PubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/PubAckPacket.html)
and the promise will be complete as soon as the PUBACK is received from the broker.

If the operation fails for any reason before these respective completion events, the promise is rejected with a descriptive
error. You should always check the reason code of a [PubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/PubAckPacket.html)
completion to determine if a QoS 1 publish operation actually succeeded.

#### Example of publishing in the v1 SDK

```java
// Blocking API.
client.publish("my/topic", AWSIotQos.QOS0, "hello");
```

```java
// Non-blocking API.
public class MyMessage extends AWSIotMessage {
    @Override
    public void onSuccess() {}
    @Override
    public void onFailure() {}
    @Override
    public void onTimeout() {}
}

MyMessage message = new MyMessage("my/topic", AWSIotQos.QOS0, "hello");
long timeout = 3000;  // milliseconds
client.publish(message, timeout);
```

#### Example of publishing in the v2 SDK

```java
PublishPacketBuilder publishBuilder =
        new PublishPacketBuilder("my/topic", QOS.AT_MOST_ONCE, "hello".getBytes());
CompletableFuture<PublishResult> published = client.publish(publishBuilder.build());
PublishResult result = published.get(60, TimeUnit.SECONDS);
```

### Subscribe

The v1 SDK provides blocking and non-blocking APIs for subscribing. To subscribe to a topic in the v1 SDK, you should
provide an instance of [AWSIotTopic](http://aws-iot-device-sdk-java-docs.s3-website-us-east-1.amazonaws.com/com/amazonaws/services/iot/client/AWSIotTopic.html)
to the [subscribe](http://aws-iot-device-sdk-java-docs.s3-website-us-east-1.amazonaws.com/com/amazonaws/services/iot/client/core/AbstractAwsIotClient.html#subscribe-com.amazonaws.services.iot.client.AWSIotTopic-)
operation. AWSIotTopic object (or, usually, an object of a children class) implements `onMessageReceived` method which
will be called on receiving a new message. If you try to subscribe to a topic that is not allowed by a policy, AWS IoT
Core service will close the connection.

The v2 SDK provides only asynchronous non-blocking API. First, you need to create a [SubscribePacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/SubscribePacket.html)
object with the help of [SubscribePacketBuilder](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/SubscribePacket.SubscribePacketBuilder.html).
If you specify multiple topics in the `SubscribePacketBuilder` object, the v2 SDK will subscribe to all of these topics using
one request. The [subscribe](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#subscribe(software.amazon.awssdk.crt.mqtt5.packets.SubscribePacket))
operation takes a description of the `SubscribePacket` you wish to send and returns a promise that resolves successfully
with the corresponding [SubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/SubAckPacket.html)
returned by the broker. The promise is rejected with an error if anything goes wrong before the `SubAckPacket` is received.
You should always check the reason codes of a `SubAckPacket` completion to determine if the subscribe operation actually succeeded.

> [!NOTE]
> If you try to subscribe with the v2 MQTT5 client to a topic that is not allowed by a policy, you do not get the connection
> closed but instead receive a SUBACK with a reason code.

In the v2 SDK, if the MQTT5 client is going to subscribe and receive packets from the MQTT broker, it is important to also set up
the [PublishEvents](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.PublishEvents.html)
callback. This callback is invoked whenever the client receives a message from the server on a topic the client is subscribed
to. With this callback, you can process messages made to subscribed topics.

#### Example of subscribing in the v1 SDK

```java
public class MyTopic extends AWSIotTopic {    
    public MyTopic(String topic, AWSIotQos qos) {
        super(topic, qos);
    }

    @Override
    public void onMessage(AWSIotMessage message) {
        // Called when a message is received.
    }
}

// Subscribe to topic.
MyTopic myOwnTopic = new MyTopic("my/own/topic", AWSIotQos.QOS1);
client.subscribe(myOwnTopic);
```

#### Example of subscribing in the v2 SDK

```java
static class SamplePublishEvents implements PublishEvents {
    @Override
    public void onMessageReceived(Mqtt5Client client, PublishReturn publishReturn) {
        // Called when a message is received by one of the active subscriptions.
    }
}

// Register subscription callback.
// A single callback processes messages received for all subscriptions,
// so it's set for the client.
SamplePublishEvents publishEvents = new SamplePublishEvents();
clientBuilder.withPublishEvents(publishEvents);
Mqtt5Client client = clientBuilder.build();

// Subscribe to topic.
SubscribePacketBuilder subscribeBuilder =
        new SubscribePacketBuilder("my/own/topic", QOS.AT_LEAST_ONCE);
CompletableFuture<Integer> subscribed = client.subscribe(subscribeBuilder.build());
```

### Unsubscribe

The v1 SDK provides blocking and non-blocking APIs for unsubscribing. To unsubscribe from a topic in the v1 SDK, you should
provide an instance of [AWSIotTopic](http://aws-iot-device-sdk-java-docs.s3-website-us-east-1.amazonaws.com/com/amazonaws/services/iot/client/AWSIotTopic.html)
to the [unsubscribe](http://aws-iot-device-sdk-java-docs.s3-website-us-east-1.amazonaws.com/com/amazonaws/services/iot/client/core/AbstractAwsIotClient.html#unsubscribe-com.amazonaws.services.iot.client.AWSIotTopic-)
operation. `AWSIotTopic` object (or, usually, an object of a children class) implements `onSuccess` and `onFailure` methods.
One of these methods will be called after the operation succeeds or fails.

The v2 SDK provides only asynchronous non-blocking API. First, you need to create an [UnsubscribePacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubscribePacket.html)
object with the help of [UnsubscribePacketBuilder](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubscribePacket.UnsubscribePacketBuilder.html).
The [unsubscribe](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#unsubscribe(software.amazon.awssdk.crt.mqtt5.packets.UnsubscribePacket))
operation takes a description of the [UnsubscribePacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubscribePacket.html)
you wish to send and returns a promise that resolves successfully with the corresponding [UnsubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubAckPacket.html)
returned by the broker. The promise is rejected with an error if anything goes wrong before the [UnsubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubAckPacket.html)
is received. You should always check the reason codes of a [UnsubAckPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubAckPacket.html)
completion to determine if the unsubscribe operation actually succeeded.

Similar to subscribing, you can unsubscribe from multiple topics in one request by just calling [withSubscription](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubscribePacket.UnsubscribePacketBuilder.html#withSubscription(java.lang.String))
for each topic you wish to unsubscribe from.

#### Example of unsubscribing in the v1 SDK

```java
// Blocking API.
client.unsubscribe("my/topic");
client.unsubscribe("another/topic");
```

```java
// Non-blocking API.
public class MyTopic extends AWSIotTopic {    
    public MyTopic(String topic, AWSIotQos qos) {
        super(topic, qos);
    }

    @Override
    public void onSuccess() {
        // Called when unsubscribing succeeds.
    }
    
        @Override
    public void onFailure() {
        // Called when unsubscribing fails.
    }
}

// Unsubscribe from topic.
MyTopic myOwnTopic = new MyTopic("my/topic", AWSIotQos.QOS1);
client.unsubscribe(myOwnTopic);
```

#### Example of unsubscribing in the v2 SDK

```java
UnsubscribePacketBuilder unsubBuilder = new UnsubscribePacketBuilder("my/topic");
client.unsubscribe(unsubBuilder.build()).get(60, TimeUnit.SECONDS);
```

### Client stop

In the v1 SDK, the `disconnect` method in the `AWSIotMqttClient` class disconnects the client. Once disconnected, the client
can connect again by calling `connect`.

In the v2 SDK, an MQTT5 client can stop a session by calling the [stop](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#stop(software.amazon.awssdk.crt.mqtt5.packets.DisconnectPacket))
method. You can provide an optional [DisconnectPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/DisconnectPacket.html)
parameter. A closed client can be started again by calling [start](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#start()).

#### Example of disconnecting a client in the v1 SDK

```java
client.disconnect();
```

#### Example of disconnecting a client in the v2 SDK

```java
DisconnectPacketBuilder disconnectBuilder = new DisconnectPacketBuilder();
disconnectBuilder.withReasonCode(DisconnectPacket.DisconnectReasonCode.NORMAL_DISCONNECTION);
client.stop(disconnectBuilder.build());
```

### Client shutdown

The v1 SDK automatically cleans resources allocated by an `AWSIotMqttClient` object on shutdown.

In the v2 SDK, when an MQTT5 client is no longer required, your program **must** close it explicitly via a `close` call.

The v2 SDK `Mqtt5Client` class implements the [AutoCloseable](https://docs.oracle.com/javase/8/docs/api/java/lang/AutoCloseable.html)
interface, so it is recommended to create `Mqtt5Client` objects in the [try-with-resources](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html)
blocks.

#### Example of closing a client in the v2 SDK

```java
Mqtt5CLient client = builder.build()

// Once fully finished with the Mqtt5Client:
client.close();
```

```java
// client.close() will be called at the end of the try/catch block.
try (Mqtt5CLient client = builder.build()) {
    // ...
} catch (Exception e) {
}

```

### Reconnects

The v1 SDK has a maximum number of retry attempts for auto-reconnect. If you exceed the maximum number of retries, the v1 SDK will throw
a permanent error and you will not be able to use the same client instance again.

The v2 SDK attempts to reconnect automatically until connection succeeds or `client.stop()` is called. The reconnection parameters,
such as min/max delays and [jitter modes](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/io/ExponentialBackoffRetryOptions.JitterMode.html),
are configurable through [`AwsIotMqtt5ClientBuilder`](https://aws.github.io/aws-iot-device-sdk-java-v2/software/amazon/awssdk/iot/AwsIotMqtt5ClientBuilder.html).

#### Example of tweaking reconnection settings in the v1 SDK

```java
client.setBaseRetryDelay(1000L);
client.setMaxRetryDelay(10000L);
```

#### Example of tweaking reconnection settings in the v2 SDK

```java
clientBuilder.withMinReconnectDelayMs(1000L);
clientBuilder.withMaxReconnectDelayMs(10000L);
clientBuilder.withRetryJitterMode(JitterMode.Full);
clientBuilder.withMinConnectedTimeToResetReconnectDelayMs(5000L);
Mqtt5Client client = clientBuidler.build();
```

### Offline operations queue

In the v1 SDK, if you have too many in-flight QoS 1 messages, you will encounter the `too many publishes in Progress` error
on publishing messages. This is caused by the so-called [in-flight publish limit](https://github.com/aws/aws-iot-device-sdk-java/blob/master/README.md#increase-in-flight-publish-limit-too-many-publishes-in-progress-error).
By default, the v1 SDK supports a maximum of 10 in-flight operations.

The v2 SDK does not limit the number of in-flight messages. Additionally, the v2 SDK provides a way to configure which kind
of packets will be placed into the offline queue when the client is in the disconnected state. The following code snippet
demonstrates how to enable storing all packets except QOS0 publish packets in the offline queue on disconnect:

#### Example of configuring the offline queue in the v2 SDK

```java
AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(/*...*/);
builder.withOfflineQueueBehavior(ClientOfflineQueueBehavior.FAIL_QOS0_PUBLISH_ON_DISCONNECT);
Mqtt5Client client = builder.build();
```

> [!NOTE]  
> AWS IoT Core [limits the number of allowed operations per second](https://docs.aws.amazon.com/general/latest/gr/iot-core.html#message-broker-limits).
The [`getOperationStatistics`](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#getOperationStatistics())
method returns the current state of an `Mqtt5Client` object's queue of operations, which may help with tracking the number
of in-flight messages.

#### Example of getting client operational statistics in the v2 SDK

```java
Mqtt5ClientOperationStatistics stats = client.getOperationStatistics();
System.out.println("Client operations queue statistics:\n"
    + "\tgetUnackedOperationCount: " + stats.getUnackedOperationCount() + "\n"
     + "\tgetUnackedOperationSize: " + stats.getUnackedOperationSize()  + "\n"
     + "\tgetIncompleteOperationCount: " + stats.getIncompleteOperationCount() + "\n"
     + "\tgetIncompleteOperationSize: " + stats.getIncompleteOperationSize()
```

For more information, see [withOfflineQueueBehavior documentation](https://aws.github.io/aws-iot-device-sdk-java-v2/software/amazon/awssdk/iot/AwsIotMqtt5ClientBuilder.html#withOfflineQueueBehavior(software.amazon.awssdk.crt.mqtt5.Mqtt5ClientOptions.ClientOfflineQueueBehavior)).

For the list of the supported offline queue behaviors and their descriptions, see [ClientOfflineQueueBehavior documentation](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.ClientOfflineQueueBehavior.html).

### Operation timeouts

In the v1 SDK, all operations (*publish*, *subscribe*, *unsubscribe*) will not timeout unless you define a timeout for them.
If no timeout is defined, there is a possibility that an operation will wait forever for the server to respond and block
the calling thread indefinitely.

In the v2 SDK, operations timeout is set for the MQTT5 client with the builder method [withAckTimeoutSeconds](https://aws.github.io/aws-iot-device-sdk-java-v2/software/amazon/awssdk/iot/AwsIotMqtt5ClientBuilder.html#withAckTimeoutSeconds(java.lang.Long)).
The default value is no timeout. As in the v1 SDK, failing to set a timeout can cause an operation to stuck forever, but it
won't block the client.

The [`getOperationStatistics`](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5Client.html#getOperationStatistics())
method returns the current state of an `Mqtt5Client` object's queue of operations, which may help with tracking operations.

#### Example of timeouts in the v1 SDK

```java
long connectTimeoutMs = 10000L;
client.connect(connectTimeoutMs);

long publishTimeoutMs = 2000L;
client.publish("my/topic", "hello", publishTimeoutMs);
```

#### Example of timeouts in the v2 SDK

```java
builder.withAckTimeoutSeconds(10);
Mqtt5Client client = builder.build();
```

### Logging

The v1 SDK uses `java.util.logging` for logging. To change the logging behavior (for example, to change the logging level or
logging destination), you can specify a property file using the JVM property `java.util.logging.config.file`.

The v2 SDK uses a custom logger allowing to control the logging process simultaneously for all layers of the SDK.

#### Example of enabling logging in the v1 SDK

To change the console logging level, the property file *logging.properties* should contain the following line:

```java
java.util.logging.ConsoleHandler.level=INFO
```

#### Example of enabling logging in the v2 SDK

You can enable logging by passing the following properties:

```
-Daws.crt.log.destination=File
-Daws.crt.log.level=Debug
-Daws.crt.log.filename=<path and filename>
```

* `aws.crt.log.destination`: Where the logs are output. Can be `File`, `Stdout` or `Stderr`. Defaults to `Stderr`.
* `aws.crt.log.level`: The level of logging shown. Can be `Trace`, `Debug`, `Info`, `Warn`, `Error`, `Fatal`, or `None`. Defaults to `Warn`.
* `aws.crt.log.filename`: The path to save the log file. Only required if `aws.crt.log.destination` is set to `File`.

### Client for AWS IoT Device Shadow

The v1 SDK is built with [AWS IoT device shadow support](http://docs.aws.amazon.com/iot/latest/developerguide/iot-thing-shadows.html), which
provides access to thing shadows (sometimes referred to as device shadows). It also supports a simplified shadow access model,
which allows developers to exchange data with their shadows by just using `getter` and `setter` methods without having to serialize
or deserialize any JSON documents.

The v2 SDK also supports device shadow service, but with completely different APIs.
First, you subscribe to special topics to get data and feedback from a service. The service client provides API for that.
For example, `SubscribeToGetShadowAccepted` subscribes to a topic to which AWS IoT Core will publish a shadow document. The server will notify you if it cannot send you a requested document via `SubscribeToGetShadowRejected`.\
After subscribing to all the required topics, the service client can start interacting with the server, for example, update
the status or request for data. These actions are also performed via client API calls. For example, `PublishGetShadow`
sends a request to AWS IoT Core to get a shadow document. The requested shadow document will be received in a callback
specified in the `SubscribeToGetShadowAccepted` call.

AWS IoT Core [documentation for Device Shadow](https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-mqtt.html)
service provides detailed descriptions for the topics used to interact with the service.

#### Example of creating a Device Shadow service client in the v1 SDK

```java
// Blocking and non-blocking API.
String thingName = "<thing name>";
AWSIotDevice device = new AWSIotDevice(thingName);
client.attach(device);
```

```java
// Simplified Shadow Access Model.
public class MyDevice extends AWSIotDevice {
    public MyDevice(String thingName) {
        super(thingName);
    }

    @AWSIotDeviceProperty
    private String someValue;

    public String getSomeValue() {
        // Read from the physical device.
    }

    public void setSomeValue(String newValue) {
        // Write to the physical device.
    }
}

MyDevice device = new MyDevice(thingName);
```

#### Example of creating a Device Shadow service client in the v2 SDK

A thing name in v2 SDK shadow client is specified for the operations with shadow documents.

```java
MqttClientConnection connection = new MqttClientConnection(mqtt5Client, null);
shadowClient = new IotShadowClient(connection);
mqtt5Client.start();
```

#### Example of getting a shadow document in the v1 SDK

```java
// Blocking API.
String state = device.get();
```

```java
// Non-blocking API.
public class MyShadowMessage extends AWSIotMessage {
    public MyShadowMessage() {
        super(null, null);
    }

    @Override
    public void onSuccess() {
        // called when the shadow method succeeded
        // state (JSON document) received is available in the payload field
    }

    @Override
    public void onFailure() {
        // called when the shadow method failed
    }

    @Override
    public void onTimeout() {
        // called when the shadow method timed out
    }
}

MyShadowMessage message = new MyShadowMessage();
long timeout = 3000;                    // milliseconds
device.get(message, timeout);
```

```java
// Simplified Shadow Access Model.
String state = device.getSomeValue();
```

#### Example of getting a shadow document in the v2 SDK

```java
static void onGetShadowAccepted(GetShadowResponse response) {
    // Called when a get request succeeded.
    // The `response` object contains the shadow document.
}

static void onGetShadowRejected(ErrorResponse response) {
    // Called when a get request failed.
}

GetShadowSubscriptionRequest requestGetShadow = new GetShadowSubscriptionRequest();
requestGetShadow.thingName = "<thing name>";

// Subscribe to the topic providing shadow documents.
CompletableFuture<Integer> accepted = shadowClient.SubscribeToGetShadowAccepted(
        requestGetShadow,
        QualityOfService.AT_LEAST_ONCE,
        onGetShadowAccepted);
// Subscribe to the topic reporting errors.
CompletableFuture<Integer> rejected = shadowClient.SubscribeToGetShadowRejected(
        requestGetShadow,
        QualityOfService.AT_LEAST_ONCE,
        onGetShadowRejected);

accepted.get();
rejected.get();

// Send request for a shadow document.
// On success, the document will be received on `onGetShadowAccepted` callback.
// On failure, the `onGetShadowRejected` callback will be called.
GetShadowRequest getShadowRequest = new GetShadowRequest();
getShadowRequest.thingName = "<thing name>";
CompletableFuture<Integer> published = shadowClient.PublishGetShadow(
        getShadowRequest,
        QualityOfService.AT_LEAST_ONCE);
published.get();
```

#### Example of updating a shadow document in the v1 SDK

```java
// Blocking and non-blocking API.
State state = "{\"state\":{\"reported\":{\"sensor\":3.0}}}";
device.update(state);
```

```java
// Simplified Shadow Access Model.
device.setSomeValue("{\"state\":{\"reported\":{\"sensor\":3.0}}}");
```

#### Example of updating a shadow document in the v2 SDK

```java
static void onUpdateShadowAccepted(UpdateShadowResponse response) {
    // Called when an update request succeeded.
}

static void onUpdateShadowRejected(ErrorResponse response) {
    // Called when an update request failed.
}

UpdateShadowSubscriptionRequest requestUpdateShadow = new UpdateShadowSubscriptionRequest();
requestUpdateShadow.thingName = "<thing name>";

// Subscribe to update responses.
CompletableFuture<Integer> accepted = shadowClient.SubscribeToUpdateShadowAccepted(
        requestUpdateShadow,
        QualityOfService.AT_LEAST_ONCE,
        onUpdateShadowAccepted);

// Subscribe to the topic reporting errors.
CompletableFuture<Integer> rejected = shadowClient.SubscribeToUpdateShadowRejected(
        requestUpdateShadow,
        QualityOfService.AT_LEAST_ONCE,
        onUpdateShadowRejected);
accepted.get();
rejected.get();

// Update shadow document
UpdateShadowRequest request = new UpdateShadowRequest();
request.thingName = "<thing name>";
request.state = new ShadowState();
request.state.reported = new HashMap<String, Object>() {
    {
        put("sensor", 3.0);
    }
}
shadowClient.PublishUpdateShadow(request, QualityOfService.AT_LEAST_ONCE);
```

For more information, see API documentation for the v2 SDK [Device Shadow](https://aws.github.io/aws-iot-device-sdk-java-v2/software/amazon/awssdk/iot/iotshadow/IotShadowClient.html).

For code examples, see the v2 SDK [Device Shadow](https://github.com/aws/aws-iot-device-sdk-java-v2/tree/main/samples/Shadow).

### Client for AWS IoT Jobs

The v2 SDK expands support of AWS IoT Core services implementing a service client for the [Jobs](https://docs.aws.amazon.com/iot/latest/developerguide/iot-jobs.html)
service. The Jobs service helps with defining a set of remote operations that can be sent to and run on one or more devices connected
to AWS IoT.

The Jobs service client provides API similar to API provided by [Client for AWS IoT Device Shadow](#client-for-device-shadow-service).
First, you subscribe to special topics to get data and feedback from a service. The service client provides API for that.
After subscribing to all the required topics, the service client can start interacting with the server, for example, update
the status or request for data. These actions are also performed via client API calls.

For detailed descriptions for the topics used to interact with the Jobs service, see AWS IoT Core documentation for the [Jobs](https://docs.aws.amazon.com/iot/latest/developerguide/jobs-mqtt-api.html) service.

For more information about the service clients, see API documentation for the v2 SDK [Jobs](https://aws.github.io/aws-iot-device-sdk-java-v2/software/amazon/awssdk/iot/iotjobs/IotJobsClient.html).

For code example, see the v2 SDK [Jobs](https://github.com/aws/aws-iot-device-sdk-java-v2/tree/main/samples/Jobs) samples.

### Client for AWS IoT fleet provisioning

[Fleet Provisioning](https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html)
(also known as Identity Service) is another AWS IoT service that the v2 SDK provides access to. By using AWS IoT fleet provisioning, AWS IoT can generate and securely deliver device
certificates and private keys to your devices when they connect to AWS IoT for the first time.

The fleet provisioning service client provides an API similar to the APIs provided by [Client for AWS IoT Device Shadow](#client-for-device-shadow-service).
First, you subscribe to special topics to get data and feedback from a service. The service client provides API for that.
After subscribing to all the required topics, the service client can start interacting with the server, for example, update
the status or request for data. These actions are also performed via client API calls.

For detailed descriptions for the topics used to interact with the Fleet Provisioning service, see AWS IoT Core documentation for [Fleet Provisioning](https://docs.aws.amazon.com/iot/latest/developerguide/fleet-provision-api.html).

For more information about the Fleet Provisioning service client, see API documentation for the v2 SDK [Fleet Provisioning](https://aws.github.io/aws-iot-device-sdk-java-v2/software/amazon/awssdk/iot/iotidentity/IotIdentityClient.html).

For code examples, see the v2 SDK [Fleet Provisioning](https://github.com/aws/aws-iot-device-sdk-java-v2/tree/main/samples/FleetProvisioning)
samples.

### Example

It's always helpful to look at a working example to see how new functionality works, to be able to tweak different options,
to compare with existing code. For that reason, we implemented a [Publish/Subscribe example](https://github.com/aws/aws-iot-device-sdk-java-v2/tree/main/samples/Mqtt/Mqtt5X509)
([source code](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/main/samples/Mqtt/Mqtt5X509/src/main/java/mqtt5x509/Mqtt5X509.java))
in the V2 SDK similar to a sample provided by V1 SDK (see a corresponding [readme section](https://github.com/aws/aws-iot-device-sdk-java/tree/master?tab=readme-ov-file#sample-applications)
and [source code](https://github.com/aws/aws-iot-device-sdk-java/blob/master/aws-iot-device-sdk-java-samples/src/main/java/com/amazonaws/services/iot/client/sample/pubSub/PublishSubscribeSample.java)).

## How to get help

Questions? You can look for an answer in the [discussions](https://github.com/aws/aws-iot-device-sdk-java-v2/discussions?discussions_q=label%3Amigration)
page. Or, you can always open a [new discussion](https://github.com/aws/aws-iot-device-sdk-java-v2/discussions/new?category=q-a&labels=migration),
and we will be happy to help you.

## Appendix

### MQTT5 features

**Clean Start and Session Expiry**\
You can use Clean Start and Session Expiry to handle your persistent sessions with more flexibility.
For more information, see the [Mqtt5ClientOptions.ClientSessionBehavior](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/Mqtt5ClientOptions.ClientSessionBehavior.html)
enum and the [NegotiatedSettings.getSessionExpiryInterval](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/NegotiatedSettings.html#getSessionExpiryInterval())
method.

**Reason Code on all ACKs**\
You can debug or process error messages more easily using the reason codes. Reason codes are returned by the message broker
based on the type of interaction with the broker (Subscribe, Publish, Acknowledge).
For more information, see [PubAckReasonCode](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/PubAckPacket.PubAckReasonCode.html),
[SubAckReasonCode](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/SubAckPacket.SubAckReasonCode.html),
[UnsubAckReasonCode](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/UnsubAckPacket.UnsubAckReasonCode.html),
[ConnectReasonCode](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/ConnAckPacket.ConnectReasonCode.html),
[DisconnectReasonCode](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/DisconnectPacket.DisconnectReasonCode.html).

**Topic Aliases**\
You can substitute a topic name with a topic alias, which is a two-byte integer.
Use [withTopicAlias](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/PublishPacket.PublishPacketBuilder.html#withTopicAlias(long))
method when creating a PUBLISH packet.

**Message Expiry**\
You can add message expiry values to published messages. Use [withMessageExpiryIntervalSeconds](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/PublishPacket.PublishPacketBuilder.html#withMessageExpiryIntervalSeconds(java.lang.Long))
method in the `PublishPacketBuilder` class.

**Server disconnect**\
When a disconnection happens, the server can proactively send the client a DISCONNECT to notify connection closure with
a reason code for disconnection.\
For more information, see the [DisconnectPacket](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/DisconnectPacket.html)
class.

**Request/Response**\
Publishers can request a response be sent by the receiver to a publisher-specified topic upon reception. Use [withResponseTopic](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/PublishPacket.PublishPacketBuilder.html#withResponseTopic(java.lang.String)) method in the `PublishPacketBuilder` class.

**Maximum Packet Size**\
Client and Server can independently specify the maximum packet size that they support. For more information, see the [connectPacketBuilder.withMaximumPacketSizeBytes](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/ConnectPacket.ConnectPacketBuilder.html#withMaximumPacketSizeBytes(java.lang.Long)), the 
[NegotiatedSettings.getMaximumPacketSizeToServer](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/NegotiatedSettings.html#getMaximumPacketSizeToServer()),
and the [ConnAckPacket.getMaximumPacketSize](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/ConnAckPacket.html#getMaximumPacketSize()) methods.

**Payload format and content type**\
You can specify the payload format (binary, text) and content type when a message is published. These are forwarded to the
receiver of the message. Use the [withContentType](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/packets/PublishPacket.PublishPacketBuilder.html#withContentType(java.lang.String))
method in the `PublishPacketBuilder` class.

**Shared Subscriptions**\
Shared Subscriptions allow multiple clients to share a subscription to a topic and only one client will receive messages
published to that topic using a random distribution.

> [!NOTE]  
> AWS IoT Core supports Shared Subscriptions for both MQTT3 and MQTT5. For more information, see [Shared Subscriptions](https://docs.aws.amazon.com/iot/latest/developerguide/mqtt.html#mqtt5-shared-subscription) from the AWS IoT Core developer guide.
