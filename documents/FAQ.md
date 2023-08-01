# Frequently Asked Questions

### Where should I start?

If you are just getting started make sure you [install this sdk](https://github.com/aws/aws-iot-device-sdk-java-v2#installation) and then build and run the [basic PubSub](https://github.com/aws/aws-iot-device-sdk-java-v2/tree/main/samples#pubsub)

### How do I enable logging?

To enable logging in the samples, you will need to set the following system properties when running the samples:

```sh
-Daws.crt.debugnative=true
-Daws.crt.log.destination=File
-Daws.crt.log.level=Trace
-Daws.crt.log.filename=<path and filename>
```

* `aws.crt.debugnative`: Whether to debug native (C/C++) code. Can be either `true` or `false`.
* `aws.crt.log.destination`: Where the logs are outputted to. Can be `File`, `Stdout` or `Stderr`. Defaults to `Stderr`.
* `aws.crt.log.level`: The level of logging shown. Can be `Trace`, `Debug`, `Info`, `Warn`, `Error`, `Fatal`, or `None`. Defaults to `Warn`.
* `aws.crt.log.filename`: The path to save the log file. Only needed if `aws.crt.log.destination` is set to `File`.

For example, to run `BasicPubSub` with logging you could use the following:

```sh
mvn compile exec:java -pl samples/BasicPubSub -Daws.crt.debugnative=true -Daws.crt.log.level=Debug -Daws.crt.log.destionation=Stdout -Dexec.mainClass=pubsub.PubSub -Dexec.args='--endpoint <endpoint> --cert <path to cert> --key <path to key> --ca_file <path to ca file>'
```

You can also enable [CloudWatch logging](https://docs.aws.amazon.com/iot/latest/developerguide/cloud-watch-logs.html) for IoT which will provide you with additional information that is not available on the client side sdk.

### I keep getting AWS_ERROR_MQTT_UNEXPECTED_HANGUP

This could be many different things but it most likely is a policy issue. Start with using a super permissive IAM policy called AWSIOTFullAccess which looks like this:

``` json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "iot:*"
            ],
            "Resource": "*"
        }
    ]
}
```

After getting it working make sure to only allow the actions and resources that you need. More info about IoT IAM policies can be found [here](https://docs.aws.amazon.com/iot/latest/developerguide/security_iam_service-with-iam.html).

### I am experiencing deadlocks

You MUST NOT perform blocking operations on any callback, or you will cause a deadlock. For example: in the on_publish_received callback, do not send a publish, and then wait for the future to complete within the callback. The Client cannot do work until your callback returns, so the thread will be stuck.

### Mac-Only TLS Behavior

Please note that on Mac, once a private key is used with a certificate, that certificate-key pair is imported into the Mac Keychain.  All subsequent uses of that certificate will use the stored private key and ignore anything passed in programmatically.  Beginning in v1.7.3, when a stored private key from the Keychain is used, the following will be logged at the "info" log level:

```
static: certificate has an existing certificate-key pair that was previously imported into the Keychain.  Using key from Keychain instead of the one provided.
```

### How do debug in VSCode?

Here is an example launch.json file to run the pubsub sample
 ``` json
 {
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "PubSub",
            "request": "launch",
            "mainClass": "pubsub.PubSub",
            "projectName": "BasicPubSub",
            "args": "--endpoint <account-number>-ats.iot.<region>.amazonaws.com --ca_file <path to root-CA> --cert <path to cert> --key <path to key> --client-id test-client",
            "console": "externalTerminal"
        }
    ]
}
```

### What certificates do I need?

* You can download pre-generated certificates from the AWS console (this is the simplest and is recommended for testing)
* You can also generate your own certificates to fit your specific use case. You can find documentation for that [here](https://docs.aws.amazon.com/iot/latest/developerguide/x509-client-certs.html) and [here](https://iot-device-management.workshop.aws/en/provisioning-options.html)
* Certificates that you will need to run the samples
    * Root CA Certificates
        * Download the root CA certificate file that corresponds to the type of data endpoint and cipher suite you're using (You most likely want Amazon Root CA 1)
        * Generated and provided by Amazon. You can download it [here](https://www.amazontrust.com/repository/) or download it when getting the other certificates from the AWS console
        * When using samples it can look like this: `--ca_file root-CA.crt`
    * Device certificate
        * Intermediate device certificate that is used to generate the key below
        * When using samples it can look like this: `--cert abcde12345-certificate.pem.crt`
    * Key files
        * You should have generated/downloaded private and public keys that will be used to verify that communications are coming from you
        * When using samples you only need the private key and it will look like this: `--key abcde12345-private.pem.key`

### I still have more questions about the this sdk?

* [Here](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html) are the AWS IoT Core docs for more details about IoT Core
* [Here](https://docs.aws.amazon.com/greengrass/v2/developerguide/what-is-iot-greengrass.html) are the AWS IoT Greengrass v2 docs for more details about greengrass
* [Discussion](https://github.com/aws/aws-iot-device-sdk-java-v2/discussions) questions are also a great way to ask other questions about this sdk.
* [Open an issue](https://github.com/aws/aws-iot-device-sdk-java-v2/issues) if you find a bug or have a feature request
