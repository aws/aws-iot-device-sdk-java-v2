# AWS IoT Device SDK for Java v2

This document provides information about the AWS IoT device SDK for Java V2. This SDK is built on the [AWS Common Runtime](https://docs.aws.amazon.com/sdkref/latest/guide/common-runtime.html)

*__Jump To:__*

* [Installation](#installation)
* [Android](./documents/ANDROID.md)
* [Samples](samples)
* [Mac-Only TLS Behavior](#mac-only-tls-behavior)
* [Getting Help](#getting-help)
* [FAQ](./documents/FAQ.md)
* [API Docs](https://aws.github.io/aws-iot-device-sdk-java-v2/)
* [MQTT5 User Guide](./documents/MQTT5_Userguide.md)
* [Migration Guide from the AWS IoT SDK for Java v1](./documents/MIGRATION_GUIDE.md)

## Installation

### Minimum Requirements

* Java 8+ ([Download and Install Java](https://www.java.com/en/download/help/download_options.html))
* Java JDK 8+ ([Download and Install JDK](https://docs.oracle.com/en/java/javase/18/install/overview-jdk-installation.html#GUID-8677A77F-231A-40F7-98B9-1FD0B48C346A))
  * [Set JAVA_HOME](./documents/PREREQUISITES.md#set-java_home)

[Step-by-step instructions](./documents/PREREQUISITES.md)

### Requirements to build the AWS CRT locally
* C++ 11 or higher
   * Clang 3.9+ or GCC 4.4+ or MSVC 2015+
* CMake 3.1+

[Step-by-step instructions](./documents/PREREQUISITES.md)

### Consuming IoT Device SDK from Maven in your application

Consuming this SDK via Maven is the preferred method of consuming it and using it within your application. To consume the Java V2 SDK in your application, add the following to your `pom.xml` dependencies:

``` xml
<dependency>
  <groupId>software.amazon.awssdk.iotdevicesdk</groupId>
  <artifactId>aws-iot-device-sdk</artifactId>
  <version>1.25.1</version>
</dependency>
```

Replace `1.25.1` in `<version>1.25.1</version>` with the latest release version for the SDK.
Look up the latest SDK version here: https://github.com/aws/aws-iot-device-sdk-java-v2/releases

### Build IoT Device SDK from source

[Install Maven and Set PATH](https://maven.apache.org/install.html)

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

If you wish to use the latest CRT release, rather than the latest tested with the IoT SDK, you can run the following before running `mvn clean install`:

~~~ sh
# Update the version of the CRT being used
mvn versions:use-latest-versions -Dincludes="software.amazon.awssdk.crt*"
~~~

## Samples

[Samples README](samples)

### Mac-Only TLS Behavior

Please note that on Mac, once a private key is used with a certificate, that certificate-key pair is imported into the Mac Keychain.  All subsequent uses of that certificate will use the stored private key and ignore anything passed in programmatically.  Beginning in v1.7.3, when a stored private key from the Keychain is used, the following will be logged at the "info" log level:

```
static: certificate has an existing certificate-key pair that was previously imported into the Keychain.  Using key from Keychain instead of the one provided.
```

## Getting Help

The best way to interact with our team is through GitHub. You can open a [discussion](https://github.com/aws/aws-iot-device-sdk-java-v2/discussions) for guidance questions or an [issue](https://github.com/aws/aws-iot-device-sdk-java-v2/issues/new/choose) for bug reports, or feature requests. You may also find help on community resources such as [StackOverFlow](https://stackoverflow.com/questions/tagged/aws-iot) with the tag [#aws-iot](https://stackoverflow.com/questions/tagged/aws-iot) or if you have a support plan with [AWS Support](https://aws.amazon.com/premiumsupport/), you can also create a new support case.

Please make sure to check out our resources too before opening an issue:

* [FAQ](./documents/FAQ.md)
* [IoT Guide](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html) ([source](https://github.com/awsdocs/aws-iot-docs))
* [MQTT5 User Guide](./documents/MQTT5_Userguide.md)
* Check for similar [Issues](https://github.com/aws/aws-iot-device-sdk-java-v2/issues)
* [AWS IoT Core Documentation](https://docs.aws.amazon.com/iot/)
* [Dev Blog](https://aws.amazon.com/blogs/?awsf.blog-master-iot=category-internet-of-things%23amazon-freertos%7Ccategory-internet-of-things%23aws-greengrass%7Ccategory-internet-of-things%23aws-iot-analytics%7Ccategory-internet-of-things%23aws-iot-button%7Ccategory-internet-of-things%23aws-iot-device-defender%7Ccategory-internet-of-things%23aws-iot-device-management%7Ccategory-internet-of-things%23aws-iot-platform)
* Integration with AWS IoT Services such as
[Device Shadow](https://docs.aws.amazon.com/iot/latest/developerguide/iot-device-shadows.html)
[Jobs](https://docs.aws.amazon.com/iot/latest/developerguide/iot-jobs.html)
[Fleet Provisioning](https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html)
is provided by code that been generated from a model of the service.
* [Contributions Guidelines](./documents/CONTRIBUTING.md)
* [DEVELOPING](./documents/DEVELOPING.md)

## License

This library is licensed under the [Apache 2.0 License](./documents/LICENSE).

Latest released version: v1.25.1
