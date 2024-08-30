# Android KeyChain PubSub

[**Return to main sample list**](../../README.md)

This sample uses the [Android KeyChain](https://developer.android.com/reference/android/security/KeyChain) and the
[Message Broker](https://docs.aws.amazon.com/iot/latest/developerguide/iot-message-broker.html)
for AWS IoT to subscribe to a topic and then send and receive messages through an MQTT connection using MQTT5.

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

## Prerequisites
The [Android KeyChain](https://developer.android.com/reference/android/security/KeyChain) provides access to private keys and corresponding certificate chains stored on an Android Device. The KeyChain must contain a Private Key and Certificate pair that was either provisioned by AWS IoT Core or a pair in which the certificate was [registered with AWS IoT Core](https://docs.aws.amazon.com/iot/latest/developerguide/register-CA-cert.html).

A manual method of importing a Private Key and Certificate into an Android KeyChain is to package the private key and certificate into a [PKCS#12](https://www.openssl.org/docs/man1.1.1/man1/pkcs12.html) file using [OpenSSL](https://www.openssl.org/)
```
$ openssl pkcs12 -export -out <pkcs12 filename>.p12 -inkey <private key filename> -in <certificate filename>
```
Copy the p12 file onto your Android Device's local storage. You can then use the "Install from storage" option within the Android Device's encryption and credentials settings. Make note of the name you apply during installation as that will be the `Alias` used in the sample.

The application running this sample must have permission to access both the Android device's KeyChain as well as permission to access the PrivateKey associated with the alias.

## How to run

Follow the instructions to build and install the [Android Sample App](../README.md) onto an Android device and then select the KeyChainPubSub sample from the drop-down menu.
