# AWS IoT Device SDK for Java v2

The AWS IoT Device SDK for Java v2 connects your Java applications and devices to the AWS IoT platform. It handles the complexities of secure communication, authentication, and device management so you can focus on your IoT solution. The SDK makes it easy to use AWS IoT services like Device Shadows, Jobs, Fleet Provisioning, and Commands.

**Supported Platforms**: Linux, Windows 11+, macOS 14+, Android API level 24+ (Android 7.0+)

> **Note**: The SDK is known to work on older platform versions, but we only guarantee compatibility for the platforms listed above.

*__Topics:__*
* [Features](#features)
* [Installation](#installation)
  * [Minimum Requirements](#minimum-requirements)
  * [Consuming IoT Device SDK](#consuming-iot-device-sdk-from-maven-in-your-application)
* [Getting Started](#getting-started)
* [Samples](samples)
* [MQTT5 User Guide](./documents/MQTT5_Userguide.md)
* [Getting Help](#getting-help)
* [Resources](#resources)

## Features

The primary purpose of the AWS IoT Device SDK for Java v2 is to simplify the process of connecting devices to AWS IoT Core and interacting with AWS IoT services on various platforms. The SDK provides:

* Integrated service clients for AWS IoT Core services
* Secure device connections to AWS IoT Core using MQTT protocol including MQTT 5.0
* Support for [multiple authentication methods and connection types](./documents/MQTT5_Userguide.md#how-to-setup-mqtt5-builder-based-on-desired-connection-method)
* Android [support](./documents/ANDROID.md)

#### Supported AWS IoT Core services

* The [AWS IoT Device Shadow](https://docs.aws.amazon.com/iot/latest/developerguide/iot-device-shadows.html) service manages device state information in the cloud.
* The [AWS IoT Jobs](https://docs.aws.amazon.com/iot/latest/developerguide/iot-jobs.html) service sends remote operations to connected devices.
* The [AWS IoT fleet provisioning](https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html) service generates and delivers device certificates automatically.
* The [AWS IoT Device Management commands](https://docs.aws.amazon.com/iot/latest/developerguide/iot-remote-command.html) service sends instructions from the cloud to connected devices.

## Installation

The recommended way to use the AWS IoT Device SDK for Java v2 in your project is to consume it from Maven Central.

### Minimum Requirements

To develop applications that use AWS IoT Device SDK for Java v2, you need:
* Java 8+ ([Download and Install Java](https://www.java.com/en/download/help/download_options.html))
* Java JDK 8+ ([Download and Install JDK](https://docs.oracle.com/en/java/javase/18/install/overview-jdk-installation.html))
* A build tool such as [Maven](https://maven.apache.org/install.html)

See [detailed setup instructions](./documents/PREREQUISITES.md) for more information.

### Consuming IoT Device SDK from Maven in your application

Add the following to your `pom.xml` dependencies:

``` xml
<dependency>
  <groupId>software.amazon.awssdk.iotdevicesdk</groupId>
  <artifactId>aws-iot-device-sdk</artifactId>
  <version>1.28.0</version>
</dependency>
```

Replace `1.28.0` in `<version>1.28.0</version>` with the latest release version for the SDK.
Look up the latest SDK version here: https://github.com/aws/aws-iot-device-sdk-java-v2/releases

### Building AWS IoT SDK from source

See the [Development Guide](./documents/DEVELOPING.md) for detailed instructions on building from source and using local builds.

## Getting Started

To get started with the AWS IoT Device SDK for Java v2:

1. Add the SDK to your project - See the [Installation](#installation) section for Maven dependency details

2. Choose your connection method - The SDK supports multiple authentication methods including X.509 certificates, AWS credentials, and custom authentication. [MQTT5 User Guide connection section](./documents/MQTT5_Userguide.md#how-to-setup-mqtt5-builder-based-on-desired-connection-method) and [MQTT5 X509 sample](./samples/Mqtt/Mqtt5X509/README.md) provide more guidance

3. Follow a complete example - Check out the [samples](samples) directory

4. Learn MQTT5 features - For advanced usage and configuration options, see the [MQTT5 User Guide](./documents/MQTT5_Userguide.md)

## Samples

Check out the [samples](samples) directory for working code examples that demonstrate:
- [Basic MQTT connection and messaging](./samples/README.md#mqtt5-client-samples)
- [AWS IoT Device Shadow operations](./samples/ServiceClients/ShadowSandbox/README.md)
- [AWS IoT Jobs](./samples/ServiceClients/JobsSandbox/README.md)
- AWS IoT Fleet provisioning: [basic](./samples/ServiceClients/Provisioning/Basic/README.md) and [with CSR](./samples/ServiceClients/Provisioning/Csr/README.md)
- [AWS IoT Commands](./samples/ServiceClients/CommandsSandbox/README.md)

The samples provide ready-to-run code with detailed setup instructions for each authentication method and use case.

## Getting Help

The best way to interact with our team is through GitHub.
* Open [discussion](https://github.com/aws/aws-iot-device-sdk-java-v2/discussions): Share ideas and solutions with the SDK community
* Search [issues](https://github.com/aws/aws-iot-device-sdk-java-v2/issues): Find created issues for answers based on a topic
* Create an [issue](https://github.com/aws/aws-iot-device-sdk-java-v2/issues/new/choose): New feature request or file a bug

If you have a support plan with [AWS Support](https://aws.amazon.com/premiumsupport/), you can also create a new support case.

#### Mac-Only TLS Behavior

> [!NOTE]
> This SDK does not support TLS 1.3 on macOS. Support for TLS 1.3 on macOS is planned for a future release.

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

Latest released version: v1.28.0
