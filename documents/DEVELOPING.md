# Development Guide

This guide covers building and developing with the AWS IoT Device SDK for Java v2.

**Prerequisites:** Follow the setup instructions in the [main README](../README.md) first.

## Building from Source

### Basic Build

```bash
# Create workspace directory
mkdir sdk-workspace
cd sdk-workspace

# Clone the repository
git clone https://github.com/awslabs/aws-iot-device-sdk-java-v2.git
cd aws-iot-device-sdk-java-v2

# Build and install
mvn clean install
```

### Using Latest CRT Version

To use the latest AWS CRT release instead of the tested version:

```bash
# Update CRT version before building
mvn versions:use-latest-versions -Dincludes="software.amazon.awssdk.crt*"
mvn clean install
```

## Using Local IoT SDK in Your Application

### Maven Dependency Configuration

To test local changes, update your application's `pom.xml`:

```xml
<dependency>
  <groupId>software.amazon.awssdk.iotdevicesdk</groupId>
  <artifactId>aws-iot-device-sdk</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

**Note:** Ensure the version matches the SDK version in [aws-crt-java pom.xml](https://github.com/awslabs/aws-crt-java/blob/main/pom.xml).

### Maven Local Repository

**Default locations:**
- **Linux:** `/home/<username>/.m2`
- **Windows:** `C:\Users\<username>\.m2`
- **macOS:** `/Users/<username>/.m2`

**Custom repository location:**
```bash
mvn -Dmaven.repo.local=/path/to/custom/repo clean install
```

**Global configuration:** Modify `settings.xml` in Maven configuration directory.

**Cleanup:** The `.m2` directory can be safely deleted to clear local builds.
