# MQTT5 AWS Websocket PubSub

[**Return to main sample list**](../../README.md)

*__Jump To:__*
* [Introduction](#introduction)
* [Requirements](#requirements)
* [How To Run](#how-to-run)
* [Additional Information](#additional-information)

## Introduction
This sample uses the
[Message Broker](https://docs.aws.amazon.com/iot/latest/developerguide/iot-message-broker.html)
for AWS IoT to send and receive messages through an MQTT connection using MQTT5 and a websocket as transport. Using websockets as transport requires the initial handshake request to be signed with the AWS Sigv4 signing algorithm. [`DefaultChainCredentialsProvider`](https://github.com/awslabs/aws-crt-java/blob/main/src/main/java/software/amazon/awssdk/crt/auth/credentials/DefaultChainCredentialsProvider.java) is used to source credentials via the default credentials provider chain to sign the websocket handshake.

You can read more about MQTT5 for the Java IoT Device SDK V2 in the [MQTT5 user guide](../../../documents/MQTT5_Userguide.md).

## Requirements

This sample assumes you have the required AWS IoT resources available. Information about AWS IoT can be found [HERE](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html) and instructions on creating AWS IoT resources (AWS IoT Policy, Device Certificate, Private Key) can be found [HERE](https://docs.aws.amazon.com/iot/latest/developerguide/create-iot-resources.html).

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
        "arn:aws:iot:<b>region</b>:<b>account</b>:client/mqtt5-sample-*"
      ]
    }
  ]
}
</pre>

Replace with the following with the data from your AWS account:
* `<region>`: The AWS IoT Core region where you created your AWS IoT Core thing you wish to use with this sample. For example `us-east-1`.
* `<account>`: Your AWS IoT Core account ID. This is the set of numbers in the top right next to your AWS account name when using the AWS IoT Core website.

Note that in a real application, you may want to avoid the use of wildcards in your ClientID or use them selectively. Please follow best practices when working with AWS on production applications using the SDK. Also, for the purposes of this sample, please make sure your policy allows a client ID of `mqtt5-sample-*` to connect or use `--client_id <client ID here>` to send the client ID your policy supports.

</details>

## How to run

To run this sample from the `aws-iot-device-sdk-java-v2` folder use the following command:

```sh
mvn compile exec:java \
    -pl samples/Mqtt/Mqtt5AwsWebsocket \
    -Dexec.args="\
    --endpoint <endpoint> \
    --signing_region <Signing region for websocket connection>"
```

If you would like to see what optional arguments are available, use the `--help` argument:
```sh
mvn compile exec:java \
    -pl samples/Mqtt/Mqtt5AwsWebsocket \
    -Dexec.args="\
    --help"
```

This will result in the following output:
```
MQTT5 AWS Websocket Sample

Required:
  --endpoint <ENDPOINT>             IoT endpoint hostname
  --signing_region <SIGNING_REGION> Signing region for websocket connection

Optional:
  --client_id <CLIENT_ID>   MQTT client ID (default: generated)
  --topic <TOPIC>           Topic to use (default: test/topic)
  --message <MESSAGE>       Message payload (default: "Hello from mqtt5 sample")
  --count <N>               Messages to publish (0 = infinite, default: 5)
```

The sample will not run without the required arguments.

## Additional Information
Additional help with the MQTT5 Client can be found in the [MQTT5 Userguide](../../../documents/MQTT5_Userguide.md). This guide will provide more details on MQTT5 [operations](../../../documents/MQTT5_Userguide.md#client-operations), [lifecycle events](../../../documents/MQTT5_Userguide.md#lifecycle-management), [connection methods](../../../documents/MQTT5_Userguide.md#how-to-setup-mqtt5-builder-based-on-desired-connection-method), and other useful information.

## ⚠️ Usage disclaimer

These code examples interact with services that may incur charges to your AWS account. For more information, see [AWS Pricing](https://aws.amazon.com/pricing/).

Additionally, example code might theoretically modify or delete existing AWS resources. As a matter of due diligence, do the following:

- Be aware of the resources that these examples create or delete.
- Be aware of the costs that might be charged to your account as a result.
- Back up your important data.