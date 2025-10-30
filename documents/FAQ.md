# Frequently Asked Questions

*__Jump To:__*
* [Where should I start](#where-should-i-start)
* [How do I enable logging](#how-do-i-enable-logging)
* [How do I get more information from an error code?](#how-do-i-get-more-information-from-an-error-code)
* [I keep getting AWS_ERROR_MQTT_UNEXPECTED_HANGUP](#i-keep-getting-aws_error_mqtt_unexpected_hangup)
* [I am experiencing deadlocks](#i-am-experiencing-deadlocks)
* [How to debug in VSCode?](#how-to-debug-in-vscode)
* [What certificates do I need?](#what-certificates-do-i-need)
* [I am getting AWS_IO_TLS_ERROR_DEFAULT_TRUST_STORE_NOT_FOUND](#root-ca-file)
* [How do I build and use the Android SDK?](#how-do-i-build-and-use-the-android-sdk)
* [Where can I find MQTT 311 Samples?](#where-can-i-find-mqtt-311-samples)
* [How can I improve the library size?](#how-can-i-improve-the-library-size)
* [I still have more questions about this sdk?](#i-still-have-more-questions-about-this-sdk)

### Where should I start?

If you are just getting started make sure you [install this sdk](https://github.com/aws/aws-iot-device-sdk-java-v2#installation) and then build and run the [Mqtt5 X509 Sample](https://github.com/aws/aws-iot-device-sdk-java-v2/tree/main/samples/Mqtt/Mqtt5X509)

### How do I enable logging?

To enable logging in the samples, you will need to set the following system properties when running the samples:

```sh
-Daws.crt.debugnative=true
-Daws.crt.log.destination=File
-Daws.crt.log.level=Trace
-Daws.crt.log.filename=<path and filename>
```

* `aws.crt.debugnative`: Whether to debug native (C/C++) code. Can be either `true` or `false`.
* `aws.crt.log.destination`: Where the logs are output to. Can be `File`, `Stdout`, or `Stderr`. Defaults to `Stderr`.
* `aws.crt.log.level`: The level of logging shown. Can be `Trace`, `Debug`, `Info`, `Warn`, `Error`, `Fatal`, or `None`. Defaults to `Warn`.
* `aws.crt.log.filename`: The path to save the log file. Only needed if `aws.crt.log.destination` is set to `File`.

For example, to run `Mqtt X509` with logging you could use the following:

```sh
mvn compile exec:java -pl samples/Mqtt/Mqtt5X509 -Daws.crt.debugnative=true -Daws.crt.log.level=Debug -Daws.crt.log.destination=Stdout -Dexec.args='--endpoint <endpoint> --cert <path to cert> --key <path to key>'
```

You can also enable [CloudWatch logging](https://docs.aws.amazon.com/iot/latest/developerguide/cloud-watch-logs.html) for IoT which will provide you with additional information that is not available on the client side sdk.

### How do I get more information from an error code?
When error codes are returned from the aws-crt-java they can be translated into human readable errors using the following:

```
import software.amazon.awssdk.crt.CRT;

// Print out the error code name
System.out.println(CRT.awsErrorName(errorCode));

// Print out a description of the error code
System.out.println(CRT.awsErrorString(errorCode));
```

### I keep getting AWS_ERROR_MQTT_UNEXPECTED_HANGUP

This could be many different things, but it is most likely a policy issue. Start by using a super permissive IAM policy called AWSIOTFullAccess which looks like this:

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

### How to debug in VSCode?

Here is an example `launch.json` file to run the X509 sample
 ``` json
 {
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "x509",
            "request": "launch",
            "mainClass": "mqtt5x509.Mqtt5X509",
            "projectName": "Mqtt5X509",
            "args": "--endpoint <account-number>-ats.iot.<region>.amazonaws.com --cert <path to cert> --key <path to key> --client-id test-client",
            "vmArgs": "-Daws.crt.debugnative=true -Daws.crt.log.destination=Stdout",
            "console": "externalTerminal"
        }
    ]
}
```

### What certificates do I need?

* You can download pre-generated certificates from the AWS console (this is the simplest and is recommended for testing)
* You can also generate your own certificates to fit your specific use case. You can find documentation for that [here](https://docs.aws.amazon.com/iot/latest/developerguide/x509-client-certs.html) and [here](https://iot-device-management.workshop.aws/en/provisioning-options.html)
* Certificates that you will need to run the samples
    * Device certificate
        * Intermediate device certificate that is used to generate the key below
        * When using samples it can look like this: `--cert abcde12345-certificate.pem.crt`
    * Key files
        * You should have generated/downloaded private and public keys that will be used to verify that communications are coming from you
        * When using samples you only need the private key and it will look like this: `--key abcde12345-private.pem.key`

### I am getting AWS_IO_TLS_ERROR_DEFAULT_TRUST_STORE_NOT_FOUND<a name="root-ca-file"></a>

This error usually occurs when the SDK cannot find or access the system's default trust store for TLS certificate validation. You can resolve this by downloading and specifying the Root CA certificate explicitly.

**Root CA Certificate**
* Download the root CA certificate file that corresponds to the type of data endpoint and cipher suite you're using (you most likely want Amazon Root CA 1 if you are using the AWS IoT service)
* This certificate is generated and provided by Amazon. You can download it [here](https://www.amazontrust.com/repository/) or download it when getting the other certificates from the AWS Console

**Set Root CA for the client builder**
```java
// When building your MQTT5 client, specify the CA file
// Mqtt5ClientBuilder builder = <setup your client builder based on your auth type>
builder.withCertificateAuthorityFromPath(null, "<path to AmazonRootCA1.pem>");
```


### How do I build and use the Android SDK?
Instructions for building, installing, and use of the Android SDK can be found [here](../documents/ANDROID.md)

### Where can I find MQTT 311 Samples?
The MQTT 311 Samples can be found in the v1.27.2 samples folder [here](https://github.com/aws/aws-iot-device-sdk-java-v2/tree/v1.27.2/samples)

### How can I improve the library size?

The SDK depends on aws-crt-java, which includes native binaries for multiple platforms (~50MB total). Here are two approaches to reduce size:

#### Option 1: Use Platform-Specific Dependencies

Use classifiers to include only your target platform's binaries:

```xml
<dependency>
    <groupId>software.amazon.awssdk.crt</groupId>
    <artifactId>aws-crt</artifactId>
    <version>0.39.0</version>
    <classifier>linux-x86_64</classifier> <!-- Only Linux 64-bit -->
</dependency>
```

See [all available classifiers](https://github.com/awslabs/aws-crt-java/tree/main?tab=readme-ov-file#available-classifiers).

#### Option 2: Build from Source

For maximum control, build both CRT and SDK locally:

1. [Build aws-crt-java from source](https://github.com/awslabs/aws-crt-java/tree/main?tab=readme-ov-file#platform)
2. Update `sdk/pom.xml` to use local aws-crt build:
   ```xml
   <dependency>
       <groupId>software.amazon.awssdk.crt</groupId>
       <artifactId>aws-crt</artifactId>
       <version>1.0.0-SNAPSHOT</version>
   </dependency>
   ```
3. [Build the SDK from source](./DEVELOPING.md#building-from-source)


### I still have more questions about this sdk?

* [Here](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html) are the AWS IoT Core docs for more details about IoT Core
* [Here](https://docs.aws.amazon.com/greengrass/v2/developerguide/what-is-iot-greengrass.html) are the AWS IoT Greengrass v2 docs for more details about greengrass
* [Discussion](https://github.com/aws/aws-iot-device-sdk-java-v2/discussions) questions are also a great way to ask other questions about this sdk.
* [Open an issue](https://github.com/aws/aws-iot-device-sdk-java-v2/issues) if you find a bug or have a feature request
