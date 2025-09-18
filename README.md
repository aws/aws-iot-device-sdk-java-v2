# AWS IoT Device SDK for Java v2

The AWS IoT Device SDK for Java v2 connects your Java applications and devices to the AWS IoT platform. Built on the AWS Common Runtime, it handles the complexities of secure communication, authentication, and device management so you can focus on your IoT solution.

**Supported Platforms**: Linux, Windows, macOS, [Android](./documents/ANDROID.md)

*__Topics:__*
* [Features](#features)
* [Using SDK](#using-sdk)
* [Getting Started](#getting-started)
* [Samples](samples)
* [MQTT5 User Guide](./documents/MQTT5_Userguide.md)
* [Getting Help](#getting-help)
* [Resources](#resources)

## Features

The primary purpose of the AWS IoT Device SDK for Java v2 is to simplify the process of connecting devices to AWS IoT Core and interacting with AWS IoT services on various platforms. The SDK provides:

This SDK is built on the [AWS Common Runtime](https://docs.aws.amazon.com/sdkref/latest/guide/common-runtime.html).

* Secure device connections to AWS IoT Core using MQTT protocol (MQTT 3.1.1 and MQTT 5.0)
* Support for [multiple authentication methods and connection types](./documents/MQTT5_Userguide.md#how-to-setup-mqtt5-builder-based-on-desired-connection-method)
* [Android support](./documents/ANDROID.md)
* Built on the [AWS Common Runtime](https://docs.aws.amazon.com/sdkref/latest/guide/common-runtime.html) for high performance and minimal footprint
* First-class support for AWS IoT Core services.

#### Supported AWS IoT Core services

* The [AWS IoT Device Shadow](https://docs.aws.amazon.com/iot/latest/developerguide/iot-device-shadows.html) service adds shadows to AWS IoT thing objects.
* The [AWS IoT Jobs](https://docs.aws.amazon.com/iot/latest/developerguide/iot-jobs.html) allows to define a set of remote operations that can be sent to and run on one or more devices connected to AWS IoT.
* The [AWS IoT fleet provisioning](https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html) can generate and securely deliver device certificates and private keys to IoT devices when they connect to AWS IoT for the first time.
* The [AWS IoT Device Management commands](https://docs.aws.amazon.com/iot/latest/developerguide/iot-remote-command.html) allows to send an instruction from the cloud to a device that's connected to AWS IoT.

## Using SDK

The recommended way to use the AWS IoT Device SDK for Java v2 in your project is to consume it from Maven Central.

### Minimum Requirements

* Java 8+ ([Download and Install Java](https://www.java.com/en/download/help/download_options.html))
* Java JDK 8+ ([Download and Install JDK](https://docs.oracle.com/en/java/javase/18/install/overview-jdk-installation.html))
  * [Set JAVA_HOME](./documents/PREREQUISITES.md#set-java_home)

See [step-by-step instructions](./documents/PREREQUISITES.md) for more detailed instructions.

### Consuming IoT Device SDK from Maven in your application

Add the following to your `pom.xml` dependencies:

``` xml
<dependency>
  <groupId>software.amazon.awssdk.iotdevicesdk</groupId>
  <artifactId>aws-iot-device-sdk</artifactId>
  <version>1.27.4</version>
</dependency>
```

Replace `1.27.4` in `<version>1.27.4</version>` with the latest release version for the SDK.
Look up the latest SDK version here: https://github.com/aws/aws-iot-device-sdk-java-v2/releases

### Building AWS IoT SDK from source

To build this SDK from source, you need to [install and configure Maven](https://maven.apache.org/install.html).

See [step-by-step instructions](./documents/PREREQUISITES.md) for more details on configuring required tools.

``` sh
# Create a workspace directory to hold all the SDK files
mkdir sdk-workspace
cd sdk-workspace
# Clone the repository
git clone https://github.com/awslabs/aws-iot-device-sdk-java-v2.git
cd aws-iot-device-sdk-java-v2
# Compile and install
mvn clean install
```

If you wish to use the latest CRT release, rather than the latest tested with the IoT SDK, you can run the following command before running `mvn clean install`:

``` sh
# Update the version of the CRT being used
mvn versions:use-latest-versions -Dincludes="software.amazon.awssdk.crt*"
```

#### Building AWS CRT from source

If you also need to build AWS CRT Java from source, visit [AWS CRT Java](https://github.com/awslabs/aws-crt-java?tab=readme-ov-file#platform) project for instructions.

## Getting Started

To get started with the AWS IoT Device SDK for Java v2:

1. Add the SDK to your project - See the [Using SDK](#using-sdk) section for Maven dependency details

2. Choose your connection method - The SDK supports multiple authentication methods including X.509 certificates, AWS credentials, and custom authentication. [MQTT5 User Guide connection section](./documents/MQTT5_Userguide.md#how-to-setup-mqtt5-builder-based-on-desired-connection-method) and [MQTT5 PubSub sample](./samples/Mqtt5/PubSub/README.md) provide more guidance

3. Follow a complete example - Check out the [samples](samples) directory

4. Learn MQTT5 features - For advanced usage and configuration options, see the [MQTT5 User Guide](./documents/MQTT5_Userguide.md)

## Samples

Check out the [samples](samples) directory for working code examples that demonstrate:
- Basic MQTT connection and messaging
- AWS IoT Device Shadow operations
- AWS IoT Jobs
- AWS IoT Fleet provisioning
- AWS IoT Commands

The samples provide ready-to-run code with detailed setup instructions for each authentication method and use case.

## Getting Help

The best way to interact with our team is through GitHub.
* Open [discussion](https://github.com/aws/aws-iot-device-sdk-java-v2/discussions): Share ideas and solutions with the SDK community
* Search [issues](https://github.com/aws/aws-iot-device-sdk-java-v2/issues): Find created issues for answers based on a topic
* Create an [issue](https://github.com/aws/aws-iot-device-sdk-java-v2/issues/new/choose): New feature request or file a bug

If you have a support plan with [AWS Support](https://aws.amazon.com/premiumsupport/), you can also create a new support case.

#### Mac-Only TLS Behavior

Please note that on Mac, once a private key is used with a certificate, that certificate-key pair is imported into the Mac Keychain.  All subsequent uses of that certificate will use the stored private key and ignore anything passed in programmatically.  Beginning in v1.7.3, when a stored private key from the Keychain is used, the following will be logged at the "info" log level:

```
static: certificate has an existing certificate-key pair that was previously imported into the Keychain.
 Using key from Keychain instead of the one provided.
```

## Resources

Check out our resources for additional guidance too before opening an issue:

* [FAQ](./documents/FAQ.md)
* [AWS IoT Core Developer Guide](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html)
* [MQTT5 User Guide](./documents/MQTT5_Userguide.md)
* [API Docs](https://aws.github.io/aws-iot-device-sdk-java-v2/)
* [AWS IoT Core Documentation](https://docs.aws.amazon.com/iot/)
* [Dev Blog](https://aws.amazon.com/blogs/iot/category/internet-of-things/)
* [Migration Guide from the AWS IoT SDK for Java v1](./documents/MIGRATION_GUIDE.md)
* [Contributions Guidelines](./documents/CONTRIBUTING.md)
* [DEVELOPING](./documents/DEVELOPING.md)

## License

This library is licensed under the [Apache 2.0 License](./documents/LICENSE).

Latest released version: v1.27.4
