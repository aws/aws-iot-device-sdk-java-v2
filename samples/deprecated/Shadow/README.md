# Shadow

[**Return to main sample list**](../../README.md)

This sample uses the AWS IoT [Device Shadow](https://docs.aws.amazon.com/iot/latest/developerguide/iot-device-shadows.html) Service to keep a property in sync between device and server. Imagine a light whose color may be changed through an app, or set by a local user.

Once connected, type a value in the terminal and press Enter to update the property's "reported" value. The sample also responds when the "desired" value changes on the server. To observe this, edit the Shadow document in the AWS Console and set a new "desired" value.

On startup, the sample requests the shadow document to learn the property's initial state. The sample also subscribes to "delta" events from the server, which are sent when a property's "desired" value differs from its "reported" value. When the sample learns of a new desired value, that value is changed on the device and an update is sent to the server with the new "reported" value.

Your IoT Core Thing's [Policy](https://docs.aws.amazon.com/iot/latest/developerguide/iot-policies.html) must provide privileges for this sample to connect, subscribe, publish, and receive. Below is a sample policy that can be used on your IoT Core Thing that will allow this sample to run as intended.

<details>
<summary>Sample Policy</summary>
<pre>
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "iot:Publish"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/get",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/update"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Receive"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/get/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/get/rejected",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/update/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/update/rejected",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/update/delta"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Subscribe"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/get/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/get/rejected",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/update/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/update/rejected",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/update/delta"
      ]
    },
    {
      "Effect": "Allow",
      "Action": "iot:Connect",
      "Resource": "arn:aws:iot:<b>region</b>:<b>account</b>:client/test-*"
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

### Run Mqtt5 Shadow Sample
To run the Shadow sample use the following command:

``` sh
mvn compile exec:java -pl samples/Shadow -Dexec.mainClass=shadow.Mqtt5ShadowSample -Dexec.args="--endpoint <endpoint> --cert <path to certificate> --key <path to private key> --thing_name <thing name>"
```

You can also pass a Certificate Authority file (CA) if your certificate and key combination requires it:

``` sh
mvn compile exec:java -pl samples/Shadow -Dexec.mainClass=shadow.Mqtt5ShadowSample -Dexec.args="--endpoint <endpoint> --ca_file <path to root CA> --cert <path to certificate> --key <path to private key> --thing_name <thing name>"
```

### Run Mqtt3 Shadow Sample

To run the Shadow sample use the following command:

``` sh
mvn compile exec:java -pl samples/Shadow -Dexec.mainClass=shadow.ShadowSample -Dexec.args="--endpoint <endpoint> --cert <path to certificate> --key <path to private key> --thing_name <thing name>"
```

You can also pass a Certificate Authority file (CA) if your certificate and key combination requires it:

``` sh
mvn compile exec:java -pl samples/Shadow -Dexec.mainClass=shadow.ShadowSample -Dexec.args="--endpoint <endpoint> --ca_file <path to root CA> --cert <path to certificate> --key <path to private key> --thing_name <thing name>"
```

## Service Client Notes
### Difference between MQTT5 and MQTT311 IotShadowClient
The IotShadowClient with Mqtt5 client is almost identical to Mqtt3 one. We wrapped the Mqtt5Client into MqttClientConnection so that we could keep the same interface for IotShadowClient.
The only difference is that you would need setup up a Mqtt5 Client for the IotShadowClient. For how to setup a Mqtt5 Client, please refer to [MQTT5 UserGuide](../../documents/MQTT5_Userguide.md) and [MQTT5 PubSub Sample](../Mqtt5/PubSub/)

<table>
<tr>
<th>Create a IotShadowClient with Mqtt5</th>
<th>Create a IotShadowClient with Mqtt311</th>
</tr>
<tr>
<td>

```Java
  /**
   * Create the MQTT5 client from the builder
   */
  AwsIotMqtt5ClientBuilder builder = AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(
          <input_endpoint>, <certificate>, <key>);
  ConnectPacket.ConnectPacketBuilder connectProperties = new ConnectPacket.ConnectPacketBuilder();
  /* Client id is mandatory to create a MqttClientConnection */
  connectProperties.withClientId(cmdData.input_clientId);
  builder.withConnectProperties(connectProperties);
  Mqtt5Client client = builder.build();
  builder.close();

  // We wrap the Mqtt5Client into MqttClientConnection so that we can use the same interface for IoTShadowClient.
  MqttClientConnection connection = new MqttClientConnection(client, null);
  // Create the Shadow client
  IotShadowClient Shadow = new IotShadowClient(connection);

  ...
  ...

  /* Make sure to release the resources after use. */
  connection.close();
  client.close();
```

</td>
<td>

```Java
  /**
   * Create the MQTT3 Connection from the builder
   */
  AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(<certificate>, <key>);
  builder.withClientId(cmdData.input_clientId)
         .withEndpoint(cmdData.input_endpoint);
  MqttClientConnection connection = builder.build();

  builder.close();

  // Create the Shadow client
  IotShadowClient Shadow = new IotShadowClient(connection);

  ...
  ...

  /* Make sure to release the resources after use. */
  connection.close();
```

</td>
</tr>
</table>

### mqtt.QualityOfService v.s. mqtt5.QoS
As the service client interface is unchanged for Mqtt3 Connection and Mqtt5 Client,the IotShadowClient will use mqtt.QualityOfService instead of mqtt5.QoS even with a Mqtt5 Client.

### Client Id
As client id is mandatory to create the `MqttClientConnection`, or the constructor would throw an `MqttException`. Please make sure you assign a client id to Mqtt5Client before you create the `MqttClientConnection`.

### Lifecycle Events / Connection Interface
You should NOT mix the connection operations between Mqtt5 Client and the wrapped MqttClientConnection.
A Good Example Would be:
```Java
  Mqtt5Client client = builder.build();
  // We wrap the Mqtt5Client into MqttClientConnection
  MqttClientConnection connection = new MqttClientConnection(client, null);

  // Start the connection using Mqtt5 Interface
  client.start();

  ...
  ...

  // As you start the connection using Mqtt5 Interface, you should stop it
  // with Mqtt5 Interface
  client.stop();

  /* Make sure to release the resources after use. */
  connection.close();
  client.close();

```
or
```Java
  Mqtt5Client client = builder.build();
  // We wrap the Mqtt5Client into MqttClientConnection
  MqttClientConnection connection = new MqttClientConnection(client, null);

  // Connect throw the MqttClientConnection Interface
  connection.connect();

  ...
  ...

  // As you start the connection using MqttClientConnection Interface, you should stop it
  // with MqttClientConnection Interface here
  connection.disconnect();

  /* Make sure to release the resources after use. */
  connection.close();
  client.close();

```

DO NOT DO THIS:
```Java
  Mqtt5Client client = builder.build();
  // We wrap the Mqtt5Client into MqttClientConnection
  MqttClientConnection connection = new MqttClientConnection(client, null);

  // Connect through the Mqtt5 Client Interface
  client.start();

  ...
  ...

  // ERROR!!! The disconnect() here would not work
  connection.disconnect();

  /* Make sure to release the resources after use. */
  connection.close();
  client.close();
```
