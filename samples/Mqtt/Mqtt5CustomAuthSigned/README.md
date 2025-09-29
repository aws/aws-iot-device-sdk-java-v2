# MQTT5 Signed Custom Authorizer PubSub

[**Return to main sample list**](../../README.md)
*__Jump To:__*
* [Introduction](#introduction)
* [Requirements](#requirements)
* [How To Run](#how-to-run)
* [Additional Information](#additional-information)

## Introduction
The Custom Authorizer samples illustrate how to connect to the [AWS IoT Message Broker](https://docs.aws.amazon.com/iot/latest/developerguide/iot-message-broker.html) with the MQTT5 Client by authenticating with a signed or unsigned [Custom Authorizer Lambda Function](https://docs.aws.amazon.com/iot/latest/developerguide/custom-auth-tutorial.html)

You can read more about MQTT5 for the Java IoT Device SDK V2 in the [MQTT5 user guide](../../../documents/MQTT5_Userguide.md).

## Requirements

You will need to setup your Custom Authorizer so the Lambda function returns a policy document. See [this page on the documentation](https://docs.aws.amazon.com/iot/latest/developerguide/config-custom-auth.html) for more details and example return result. You can customize this lambda function as needed for your application to provide your own security measures based on the needs of your application.

The policy [Policy](https://docs.aws.amazon.com/iot/latest/developerguide/iot-policies.html) provided by your Custom Authorizer Lambda must provide iot connect, subscribe, publish, and receive privileges for this sample to run successfully.

Below is a sample policy that provides the necessary privileges.

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
    -pl samples/Mqtt/Mqtt5CustomAuthSigned \
    -Dexec.args=" \
    --endpoint <endpoint> \
    --authorizer_name <The name of the custom authorizer to invoke> \
    --auth_token_key_name <Authorizer token key name> \
    --auth_token_key_value <Authorizer token key value> \
    --auth_signature <Custom authorizer signature> \
    --auth_username <The name to send when connecting through the custom authorizer> \
    --auth_password <The password to send when connecting through a custom authorizer>"
```

If you would like to see what optional arguments are available, use the `--help` argument:
```sh
mvn compile exec:java \
    -pl samples/Mqtt/Mqtt5CustomAuthSigned \
    -Dexec.args=" \
    --help"
```

This will result in the following output:
```
MQTT5 Custom Authorizer Signed Sample

Required:
  --endpoint <ENDPOINT>                         IoT endpoint hostname
  --authorizer_name <AUTHORIZER_NAME>           The name of the custom authorizer to connect to invoke
  --auth_signature <AUTH_SIGNATURE>             Custom authorizer signature
  --auth_token_key_name <AUTH_TOKEN_KEY_NAME>   Authorizer token key name
  --auth_token_key_value <AUTH_TOKEN_KEY_VALUE> Authorizer token key value
  --auth_username <AUTH_USERNAME>               The name to send when connecting through the custom authorizer
  --auth_password <AUTH_PASSWORD>               The password to send when connecting through a custom authorizer

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
