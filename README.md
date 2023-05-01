# AWS IoT Device SDK for Java v2

This document provides information about the AWS IoT device SDK for Java V2.

If you have any issues or feature requests, please file an issue or pull request.

API documentation: https://aws.github.io/aws-iot-device-sdk-java-v2/

This SDK is built on the AWS Common Runtime, a collection of libraries
([aws-c-common](https://github.com/awslabs/aws-c-common),
[aws-c-io](https://github.com/awslabs/aws-c-io),
[aws-c-mqtt](https://github.com/awslabs/aws-c-mqtt),
[aws-c-http](https://github.com/awslabs/aws-c-http),
[aws-c-cal](https://github.com/awslabs/aws-c-cal),
[aws-c-auth](https://github.com/awslabs/aws-c-auth),
[s2n](https://github.com/awslabs/s2n)...) written in C to be
cross-platform, high-performance, secure, and reliable. The libraries are bound
to Java by the [aws-crt-java](https://github.com/awslabs/aws-crt-java) package.

*__Jump To:__*

* [Installation](#Installation)
* [Samples](samples)
* [Getting Help](#Getting-Help)
* [FAQ](./documents/FAQ.md)
* [Giving Feedback and Contributions](#Giving-Feedback-and-Contributions)
* [MQTT5 User Guide](./documents/MQTT5_Userguide.md)

## What's New

The SDK now supports MQTT5. See the [MQTT5 User Guide](./documents/MQTT5_Userguide.md) or the [API Documentation](https://awslabs.github.io/aws-crt-java/software/amazon/awssdk/crt/mqtt5/package-summary.html) for more information. There is also a [MQTT5 sample here](https://github.com/aws/aws-iot-device-sdk-java-v2/tree/main/samples#mqtt5-pubsub).

## Installation

### Minimum Requirements

* Java 8+ ([Download and Install Java](https://www.java.com/en/download/help/download_options.html))
* Java JDK 8+ ([Download and Install JDK](https://docs.oracle.com/en/java/javase/18/install/overview-jdk-installation.html#GUID-8677A77F-231A-40F7-98B9-1FD0B48C346A))
  * [Set JAVA_HOME](./PREREQUISITES.md/##SetJAVA_HOME)

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
  <version>1.11.6</version>
</dependency>
```

Replace `1.11.6` in `<version>1.11.6</version>` with the latest release version for the SDK.
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

### Build IoT Device SDK and CRT from source

``` sh
# Create a workspace directory to hold all the SDK files
mkdir sdk-workspace
cd sdk-workspace
# Clone the CRT repository
#     (Use the latest version of the CRT here instead of "v0.21.11")
git clone --branch v0.21.13 --recurse-submodules https://github.com/awslabs/aws-crt-java.git
cd aws-crt-java
# Compile and install the CRT
mvn install -Dmaven.test.skip=true
# Clone the SDK repository
git clone https://github.com/awslabs/aws-iot-device-sdk-java-v2.git
cd ../aws-iot-device-sdk-java-v2
# Compile and install
mvn clean install
```

#### Android

Supports API 26 or newer.
NOTE: The shadow sample does not currently complete on android due to its dependence on stdin keyboard input.

``` sh
# Create a workspace directory to hold all the SDK files
mkdir sdk-workspace
cd sdk-workspace
# Clone the CRT repository
#     (Use the latest version of the CRT here instead of "v0.21.11")
git clone --branch v0.21.13 --recurse-submodules https://github.com/awslabs/aws-crt-java.git
# Compile and install the CRT for Android
cd aws-crt-java/android
./gradlew connectedCheck # optional, will run the unit tests on any connected devices/emulators
./gradlew publishToMavenLocal
# Clone the SDK repository
cd ../..
git clone https://github.com/awslabs/aws-iot-device-sdk-java-v2.git
# Compile and install
cd aws-iot-device-sdk-java-v2/android
./gradlew publishToMavenLocal
./gradlew installDebug # optional, will install the IoTSamples app to any connected devices/emulators
```

Add the following to your project's build.gradle:

``` groovy
repositories {
    mavenCentral()
    maven {
        url System.getenv('HOME') + "/.m2/repository"
    }
}

dependencies {
    implementation 'software.amazon.awssdk.crt:android:0.21.13'
}
```

Replace `0.21.13` in `software.amazon.awssdk.crt:android:0.21.13` with the latest version of the CRT.
Look up the latest CRT version here: https://github.com/awslabs/aws-crt-java/releases

#### Caution
You will need to override and provide a ROOT_CERTIFICATE manually from one of the following [certificates](https://www.amazontrust.com/repository/). For overriding default trust store you can use following [method](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/ed802dce740895bcd3b0b91de30ec49407e34a87/sdk/src/main/java/software/amazon/awssdk/iot/AwsIotMqttConnectionBuilder.java#L151-L160). It's a [known problem](https://github.com/aws/aws-iot-device-sdk-java-v2/issues/157).

## Samples

[Samples README](samples)

## Getting Help

The best way to interact with our team is through GitHub. You can [open an issue](https://github.com/aws/aws-iot-device-sdk-java-v2/issues) and choose from one of our templates for guidance, bug reports, or feature requests. You may also find help on community resources such as [StackOverFlow](https://stackoverflow.com/questions/tagged/aws-iot) with the tag #aws-iot or If you have a support plan with [AWS Support](https://aws.amazon.com/premiumsupport/), you can also create a new support case.

Please make sure to check out our resources too before opening an issue:

* Our [FAQ](./documents/FAQ.md)
* Our [Developer Guide](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html) ([source](https://github.com/awsdocs/aws-iot-docs))
* [MQTT5 User Guide](./documents/MQTT5_Userguide.md)
* Check for similar [Issues](https://github.com/aws/aws-iot-device-sdk-java-v2/issues)
* [AWS IoT Core Documentation](https://docs.aws.amazon.com/iot/)
* [Dev Blog](https://aws.amazon.com/blogs/?awsf.blog-master-iot=category-internet-of-things%23amazon-freertos%7Ccategory-internet-of-things%23aws-greengrass%7Ccategory-internet-of-things%23aws-iot-analytics%7Ccategory-internet-of-things%23aws-iot-button%7Ccategory-internet-of-things%23aws-iot-device-defender%7Ccategory-internet-of-things%23aws-iot-device-management%7Ccategory-internet-of-things%23aws-iot-platform)
* Integration with AWS IoT Services such as
[Device Shadow](https://docs.aws.amazon.com/iot/latest/developerguide/iot-device-shadows.html)
[Jobs](https://docs.aws.amazon.com/iot/latest/developerguide/iot-jobs.html)
[Fleet Provisioning](https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html)
is provided by code that been generated from a model of the service.

## Giving Feedback and Contributions

We need your help in making this SDK great. Please participate in the community and contribute to this effort by submitting issues, participating in discussion forums and submitting pull requests through the following channels.

* [Contributions Guidelines](./documents/CONTRIBUTING.md)
* Articulate your feature request or upvote existing ones on our [Issues](https://github.com/aws/aws-iot-device-sdk-java-v2/issues?q=is%3Aissue+is%3Aopen+label%3Afeature-request) page.
* Create discussion questions [here](https://github.com/aws/aws-iot-device-sdk-java-v2/discussions)
* Find a bug open an [issue](https://github.com/aws/aws-iot-device-sdk-java-v2/issues)

## License

This library is licensed under the Apache 2.0 License.

Latest released version: v1.12.0
