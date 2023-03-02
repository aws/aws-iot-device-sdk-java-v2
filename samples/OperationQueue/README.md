# Operations Queue

[**Return to main sample list**](../README.md)

This sample uses the
[Message Broker](https://docs.aws.amazon.com/iot/latest/developerguide/iot-message-broker.html)
for AWS IoT to send and receive messages through an MQTT connection. It then subscribes and begins publishing messages to a topic, like in the [PubSub sample](../BasicPubSub/README.md).

However, this sample uses a operation queue to handle the processing of operations, rather than directly using the MQTT311 connection. This gives an extreme level of control over how operations are processed, the order they are processed in, limiting how many operations can exist waiting to be sent, what happens when a new operation is added when the queue is full, and ensuring the MQTT311 connection is never overwhelmed with too many messages at once.

Additionally, using a queue allows you to put limits on how much data you are trying to send through the socket to the AWS IoT Core server. This can help keep your application within the IoT Core sending limits, ensuring all your MQTT311 operations are being processed correctly and the socket is not backed up. It also the peace of mind that your application cannot become "runaway" and start sending an extreme amount of messages at all once, clogging the socket depending on how large the messages are and the frequency.

**Note**: MQTT5 does not have the same issues with backed up sockets due to the use of an internal operation queue, which ensures the socket does not get backed up.

This operation queue can be configured in a number of different ways to best match the needs of your application. Further, the operation queue is designed to be as standalone as possible so it can be used as a starting point for implementing your own operation queue for the MQTT311 connection. The `MqttOperationQueue` class is fully documented with comments explaining the functions used.

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

Note that in a real application, you may want to avoid the use of wildcards in your ClientID or use them selectively. Please follow best practices when working with AWS on production applications using the SDK. Also, for the purposes of this sample, please make sure your policy allows a client ID of `test-*` to connect or use `--client_id <client ID here>` to send the client ID your policy supports.

</details>

## How to run

To Run this sample, use the following command:

```sh
mvn compile exec:java -pl samples/OperationQueue -Dexec.mainClass=pubsub.PubSub -Dexec.args="--endpoint <endpoint> --cert <path to certificate> --key <path to private key>"
```

You can also pass a Certificate Authority file (CA) if your certificate and key combination requires it:

```sh
mvn compile exec:java -pl samples/OperationQueue -Dexec.mainClass=pubsub.PubSub -Dexec.args="--endpoint <endpoint> --cert <path to certificate> --key <path to private key> --ca_file <path to CA file>"
```

Finally, you can control how the operation queue inserts new operations and drops operations when the queue is full via the `--queue_mode` parameter. For example, to have a rolling queue where new operations are added to the front and overflow is removed from the back of the queue:

```sh
mvn compile exec:java -pl samples/OperationQueue -Dexec.mainClass=pubsub.PubSub -Dexec.args="--endpoint <endpoint> --cert <path to certificate> --key <path to private key> --queue_mode 1"
```

See the output of the `--help` argument for more information on the queue operation modes and configuration of this sample.


## Queue Design

TODO

### Operations outside of the queue and retries

TODO
