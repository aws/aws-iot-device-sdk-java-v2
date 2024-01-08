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

## Installation

### Minimum requirements
* Java 11+ ([Download and Install Java](https://www.java.com/en/download/help/download_options.html))
  * [Set JAVA_HOME](./PREREQUISITES.md#set-java_home)
* Gradle 7.4.2 ([Download and Install Gradle](https://gradle.org/install/))
* Android SDK 26 ([Doanload SDK Manager](https://developer.android.com/tools/releases/platform-tools#downloads))
  * [Set ANDROID_HOME](./PREREQUISITES.md#set-android_home)

### Build and install IoT Device SDK from source
Supports API 26 or newer.
NOTE: The shadow sample does not currently complete on android due to its dependence on stdin keyboard input.

``` sh
# Create a workspace directory to hold all the SDK files
mkdir sdk-workspace
cd sdk-workspace
# Clone the SDK repository
# (Use the latest version of the SDK here instead of `v1.19.0`)
git clone --branch v1.19.0 --recurse-submodules https://github.com/aws/aws-iot-device-sdk-java-v2.git
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
    api 'software.amazon.awssdk.iotdevicesdk:aws-iot-device-sdk-android:1.19.0'
}
```
Replace `1.19.0` in `software.amazon.awssdk.iotdevicesdk:aws-iot-device-sdk-android:1.19.0` with the latest release version for the SDK.
Look up the latest SDK version here: https://github.com/aws/aws-iot-device-sdk-java-v2/releases

### Consuming from locally installed
You may also consume IoT Device SDK Android in your application using a locally installed version by adding the
following to your `build.gradle` repositories and depenencies:
``` groovy
repositories {
    mavenLocal()
}

dependencies {
    api 'software.amazon.awssdk.iotdevicesdk:aws-iot-device-sdk-android:1.19.0'
}
```
Replace `1.19.0` in `software.amazon.awssdk.iotdevicesdk:aws-iot-device-sdk-android:1.19.0` with the latest release version for the SDK
or replace with `1.0.0-SNAPSHOT` to use the SDK built and installed from source.
Look up the latest SDK version here: https://github.com/aws/aws-iot-device-sdk-java-v2/releases

## Samples App
[Android IoT Samples App README](../samples/Android/README.md)

