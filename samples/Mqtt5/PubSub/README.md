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

Note that to run this sample using Websockets, you will need to set your AWS credentials in your environment variables or local files. See the [authorizing direct AWS](https://docs.aws.amazon.com/iot/latest/developerguide/authorizing-direct-aws.html) page for documentation on how to get the AWS credentials, which then you can set to the `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, and `AWS_SESSION_TOKEN` environment variables.
