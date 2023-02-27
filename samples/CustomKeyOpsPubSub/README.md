# Custom Key Operations PubSub

[**Return to main sample list**](../README.md)

This sample is similar to the [Basic PubSub](../BasicPubSub/README.md) sample but shows how to perform custom private key operations during the Mutual TLS (mTLS) handshake. This is necessary if you require an external library to handle private key operations such as signing and decrypting. By utilizing custom private key operations, you can use any external library for the Mutual TLS private key operations when connecting to AWS IoT Core.

**WARNING: Unix (Linux) only**

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

To run the Custom Key Ops PubSub sample use the following command:

``` sh
> mvn exec:java -pl samples/CustomKeyOpsPubSub -Dexec.mainClass=customkeyopspubsub.CustomKeyOpsPubSub -Dexec.args="--endpoint <endpoint> --cert <path to certificate> --key <path to pkcs8 key>"
```

You can also pass a Certificate Authority file (CA) if your certificate and key combination requires it:

``` sh
> mvn exec:java -pl samples/CustomKeyOpsPubSub -Dexec.mainClass=customkeyopspubsub.CustomKeyOpsPubSub -Dexec.args="--endpoint <endpoint> --cert <path to certificate> --key <path to pkcs8 key> --ca_file <path to root CA>"
```

### How to convert AWS IoT Core key to PCKS#8 key

Note that, for this sample, the `--key` passed via args must be a PKCS#8 file instead of the typical PKCS#1 file that AWS IoT Core vends by default. To convert your key file from PKCS#1 (starts with `-----BEGIN RSA PRIVATE KEY-----`) into a PKCS#8 file (starts with `-----BEGIN PRIVATE KEY-----`), run the following cmd:

```sh
openssl pkcs8 -topk8 -in <my-private.pem.key> -out <my-private-p8.pem.key> -nocrypt
```

Then you can use this new PKCS#8 key with the sample.
