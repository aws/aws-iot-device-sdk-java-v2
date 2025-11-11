# AWS IoT Device SDK for Android

This document provides information about building and using the AWS IoT device SDK for Java V2 with Android.

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
An Android library of the aws-crt-java package is built from the same library and is
a dependency of the aws-iot-device-sdk-android library.

*__Jump To:__*

* [Installation](#installation)
  * [Minimum requirements](#minimum-requirements)
  * [Build and install IoT Device SDK from source](#build-and-install-iot-device-sdk-from-source)
* [Consuming IoT Device SDK Android](#consuming-from-locally-installed)
  * [Consuming from Maven](#consuming-from-maven)
  * [Consuming from locally installed](#consuming-from-locally-installed)
* [Samples App](#samples-app)
* [Android KeyChain](#android-keychain)
* [PKCS#11](#pkcs11)

## Installation

### Minimum requirements
* Java 17+ ([Download and Install Java](https://www.java.com/en/download/help/download_options.html))
  * [Set JAVA_HOME](./PREREQUISITES.md#set-java_home)
* Gradle 8.5.1+ ([Download and Install Gradle](https://gradle.org/install/))
* Android SDK 24 ([Download SDK Manager](https://developer.android.com/tools/releases/platform-tools#downloads))
  * [Set ANDROID_HOME](./PREREQUISITES.md#set-android_home)

> [!NOTE]
> The SDK supports Android minimum API of 24 but requires [desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring) to support Java 8 language APIs used in by the SDK. If minimum Android API Version is set to 26+ desugaring is not required.

### Build and install IoT Device SDK from source
> [!NOTE]
> The shadow sample does not currently complete on android due to its dependence on stdin keyboard input.

``` sh
# Create a workspace directory to hold all the SDK files
mkdir sdk-workspace
cd sdk-workspace
# Clone the SDK repository
# (Use the latest version of the SDK here instead of `v1.28.0`)
git clone --branch v1.28.0 --recurse-submodules https://github.com/aws/aws-iot-device-sdk-java-v2.git
# Compile and install the SDK for Android
cd aws-iot-device-sdk-java-v2/android
./gradlew build
# Install SDK locally
./gradlew publishToMavenLocal
```

## Consuming IoT Device SDK Android

### Consuming from Maven
(#consuming-from-locally-installed)
Consuming this SDK via Maven is the preferred method of consuming it and using it within your application. To consume
IoT Device SDK Android in your application, add the following to your `build.gradle` repositories and dependencies:

``` groovy
repositories {
    mavenCentral()
}

dependencies {
    api 'software.amazon.awssdk.iotdevicesdk:aws-iot-device-sdk-android:1.28.0'
}
```
Replace `1.28.0` in `software.amazon.awssdk.iotdevicesdk:aws-iot-device-sdk-android:1.28.0` with the latest release version for the SDK.
Look up the latest SDK version here: https://github.com/aws/aws-iot-device-sdk-java-v2/releases

### Consuming from locally installed
You may also consume IoT Device SDK Android in your application using a locally installed version by adding the
following to your `build.gradle` repositories and depenencies:
``` groovy
repositories {
    mavenLocal()
}

dependencies {
    api 'software.amazon.awssdk.iotdevicesdk:aws-iot-device-sdk-android:1.28.0'
}
```
Replace `1.28.0` in `software.amazon.awssdk.iotdevicesdk:aws-iot-device-sdk-android:1.28.0` with the latest release version for the SDK
or replace with `1.0.0-SNAPSHOT` to use the SDK built and installed from source.
Look up the latest SDK version here: https://github.com/aws/aws-iot-device-sdk-java-v2/releases

## Samples App
The Android IoT Samples App builds a number of aws-iot-device-sdk-java-v2 IoT samples into a single APK that can be installed onto an Android device to test different functionality.
[Android IoT Samples App README](../samples/Android/README.md)

## Android KeyChain
Connecting using credentials stored in the Android KeyChain requires the app have permission to both access the KeyChain as well as the alias containing the PrivateKey within. The [Android KeyChain PubSub Sample](../samples/Android/AndroidKeyChainPubSub/README.md) demonstrates how you can use the context and alias with the `AndroidKeyChainHandlerBuilder` and the `AwsIotMqtt5ClientBuilder` to connect to AWS IoT Core with an Mqtt5 Client. The KeyChain PubSub sample is included in the [Android IoT Samples App](../samples/Android/README.md). The `AndroidKeyChainHandlerBuilder` also accepts a `PrivateKey` directly but requires the Certificate be set using either `withCertificateFromPath` or `withCertificateContents` functions.

## PKCS#11
Connecting using PKCS#11 requires a PKCS#11 library which the user must supply. There are requirements the library must meet:
* The PKCS#11 library **must** be compiled for Android and for use on the architecture of the target device.
* The PKCS#11 library **must** have access to the PKCS#11 compatible smart card or Hardware Security Module (HSM) for storage and access to the private key file.
* The path to the library needs to be provided to the [AwsIotMqtt5ClientBuilder](https://github.com/aws/aws-iot-device-sdk-java-v2/blob/92e9ff7dff1cdb191b16c8e52710cc731df04c08/sdk/src/main/java/software/amazon/awssdk/iot/AwsIotMqtt5ClientBuilder.java#L109C24-L109C24) for it to load and use the PKCS#11 library. ([The PKCS#11 Sample](../samples/Pkcs11Connect/README.md) can be used as a reference)
* The PKCS#11 library must not be compressed (When packaging assets into an Android APK, the assets are routinely compressed) and if it is compressed, must be uncompressed before providing the file location.

A java sample using PKCS#11 can be found here: [Java PKCS#11 Sample](../samples/Pkcs11Connect/README.md)
