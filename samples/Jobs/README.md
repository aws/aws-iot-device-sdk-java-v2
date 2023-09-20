# Jobs

[**Return to main sample list**](../README.md)

This sample uses the AWS IoT [Jobs](https://docs.aws.amazon.com/iot/latest/developerguide/iot-jobs.html) Service to describe jobs to execute. [Jobs](https://docs.aws.amazon.com/iot/latest/developerguide/iot-jobs.html) is a service that allows you to define and respond to remote operation requests defined through the AWS IoT Core website or via any other device (or CLI command) that can access the [Jobs](https://docs.aws.amazon.com/iot/latest/developerguide/iot-jobs.html) service.

Note: This sample requires you to create jobs for your device to execute. See
[instructions here](https://docs.aws.amazon.com/iot/latest/developerguide/create-manage-jobs.html) for how to make jobs.

On startup, the sample describes the jobs that are pending execution and pretends to process them, marking each job as complete as it does so.

Your IoT Core Thing's [Policy](https://docs.aws.amazon.com/iot/latest/developerguide/iot-policies.html) must provide privileges for this sample to connect, subscribe, publish, and receive. Below is a sample policy that can be used on your IoT Core Thing that will allow this sample to run as intended.

<details>
<summary>Sample Policy</summary>
<pre>
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "iot:Publish",
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/start-next",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/*/update",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/*/get",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/get"
      ]
    },
    {
      "Effect": "Allow",
      "Action": "iot:Receive",
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/notify-next",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/start-next/*",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/*/update/*",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/get/*",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/*/get/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": "iot:Subscribe",
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/jobs/notify-next",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/jobs/start-next/*",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/jobs/*/update/*",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/jobs/get/*",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/jobs/*/get/*"
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

### Run Mqtt5 Jobs Sample
Use the following command to run the Jobs sample:

``` sh
mvn compile exec:java -pl samples/Jobs -Dexec.mainClass=jobs.Mqtt5JobsSample -Dexec.args="--endpoint <endpoint> --cert <path to certificate> --key <path to private key> --thing_name <thing name>"
```

You can also pass a Certificate Authority file (CA) if your certificate and key combination requires it:

``` sh
mvn compile exec:java -pl samples/Jobs -Dexec.mainClass=jobs.Mqtt5JobsSample -Dexec.args="--endpoint <endpoint> --ca_file <path to root CA> --cert <path to certificate> --key <path to private key> --thing_name <thing name>"
```

### Run Mqtt3 Jobs Sample
Use the following command to run the Jobs sample:

``` sh
mvn compile exec:java -pl samples/Jobs -Dexec.mainClass=jobs.JobsSample -Dexec.args="--endpoint <endpoint> --cert <path to certificate> --key <path to private key> --thing_name <thing name>"
```

You can also pass a Certificate Authority file (CA) if your certificate and key combination requires it:

``` sh
mvn compile exec:java -pl samples/Jobs -Dexec.mainClass=jobs.JobsSample -Dexec.args="--endpoint <endpoint> --ca_file <path to root CA> --cert <path to certificate> --key <path to private key> --thing_name <thing name>"
```



## Service Client Notes
### Difference relative to MQTT311 IotJobsClient
The IotJobsClient with mqtt5 client is almost identical to mqtt3 one. We wrapped the Mqtt5Client into MqttClientConnection so that we could keep the same interface for IotJobsClient.
The only difference is that you would need setup up a Mqtt5 Client for the IotJobsClient. For how to setup a Mqtt5 Client, please refer to [MQTT5 UserGuide](../../documents/MQTT5_Userguide.md) and [MQTT5 PubSub Sample](../Mqtt5/PubSub/)

<table>
<tr>
<th>Create a IotJobsClient with Mqtt5</th>
<th>Create a IotJobsClient with Mqtt311</th>
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

  // We wrap the Mqtt5Client into MqttClientConnection so that we keep the same interface for IoTJobsClient.
  MqttClientConnection connection = new MqttClientConnection(client, null);
  // Create the Jobs client
  IotJobsClient jobs = new IotJobsClient(connection);

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

  // Create the Jobs client
  IotJobsClient jobs = new IotJobsClient(connection);

  ...
  ...

  /* Make sure to release the resources after use. */
  connection.close();
```

</td>
</tr>
</table>

### mqtt.QualityOfService v.s. mqtt5.QoS
As the service client interface is unchanged for Mqtt3 Connection and Mqtt5 Client,the IotJobsClient will use mqtt.QualityOfService instead of mqtt5.QoS even with a Mqtt5 Client.

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