# Sample Applications for the AWS IoT Device SDK for Java v2
This directory contains sample applications for [aws-iot-device-sdk-java-v2](../README.md)

### Table of Contents
* [Samples](#samples)
    * [MQTT5 Client Samples](#mqtt5-client-samples)
    * [Service Client Samples](#service-client-samples)
    * [Greengrass Samples](#greengrass-samples)
* [Instructions](#instructions)
* [Sample Help](#sample-help)
* [Enable Logging](#enable-logging)

## Samples
### MQTT5 Client Samples
##### MQTT5 is the recommended MQTT Client. Additional infomration and usage instructions can be found in the [MQTT5 User Guide](../documents/Mqtt5_Userguide.md). The samples below will create an MQTT5 client, connect using the selected method, subscribe to a topic, publish to the topic, and then disconnect.
| MQTT5 Client Sample | Description |
|--------|-------------|
| [X509-based mutual TLS](./Mqtt/Mqtt5X509/README.md) | Demonstrates connecting to AWS IoT Core using X.509 certificates and private keys.
| [Websockets with Sigv4 authentication](./Mqtt/Mqtt5AwsWebsocket/README.md) | Shows how to authenticate over websockets using AWS Signature Version 4 credentials. |
| [AWS Signed Custom Authorizer Lambda Function](./Mqtt/Mqtt5CustomAuthSigned/README.md) | Connecting with a signed Lambda-backed custom authorizer.
| [AWS Unsigned Custom Authorizer Lambda Function](./Mqtt/Mqtt5CustomAuthUnsigned/README.md) | Connecting with an unsigned Lambda-backed custom authorizer.
| [PKCS11](./Mqtt/Mqtt5Pkcs11/README.md) | Demonstrates connecting using a hardware security module (HSM) or smartcard with PKCS#11. |
| [Other Connection Methods](../documents/MQTT5_Userguide.md#how-to-create-an-mqtt5-client-based-on-desired-connection-method) | More connection methods are available for review in the MQTT5 Userguide

### Service Client Samples
##### AWS offers a number of IoT related services using MQTT. The samples below demonstrate how to use the service clients provided by the SDK to interact with those services.
| Service Client Sample | Description |
|--------|-------------|
| [Basic Fleet Provisioning](./ServiceClients/Provisioning/Basic/README.md) | Provision a device using the Fleet Provisioning template. |
| [CSR Fleet Provisioning](./ServiceClients/Provisioning/Csr/README.md) | Demonstrates CSR-based device certificate provisioning. |
| [Shadow Sandbox](./ServiceClients/ShadowSandbox/README.md) | Sandbox sample to manage and sync device state using the IoT Device Shadow service. |
| [Jobs Sandbox](./ServiceClients/JobsSandbox/README.md) | Sandbox sample to receive and execute remote operations sent from the Jobs service. |
| [Commands Sandbox](./ServiceClients/CommandsSandbox/README.md) | Sandbox sample to demonstrante Commands service. |

### Greengrass Samples
##### Samples that interact with [AWS Greengrass](https://aws.amazon.com/greengrass/).
| Greengrass Sample | Description |
|--------|-------------|
| [Greengrass Discovery](./Greengrass/Discovery/README.md) | Discover and connect to a local Greengrass core. |
| [Greengrass IPC](./Greengrass/GreengrassIPC/README.md) | Demonstrates Inter-Process Communication (IPC) with Greengrass components. |

### Instructions

First, install `aws-iot-device-sdk-java-v2`. Installation instructions for the SDK are [Provided Here](../README.md#installation).

Each sample's README contains prerequisites, arguments, and detailed instructions. For example, the [MQTT X509  Sample README](./Mqtt/Mqtt5x509/README.md) instructs to run the sample with the following command:

```sh
mvn compile exec:java \
    -pl samples/Mqtt/Mqtt5x509 \
    -Dexec.args=" \
    --endpoint <ENDPOINT> \
    --cert <PATH TO CERTIFICATE FILE> \
    --key <PATH TO PRIVATE KEY FILE>"
```

### Running Samples with latest SDK release

If you want to run a sample using the latest release of the SDK, instead of compiled from source, you need to use the `latest-release` profile. For example:

```sh
mvn -P latest-release compile exec:java \
    -pl samples/Mqtt/Mqtt5X509 \
    -Dexec.args=" \
    --endpoint <ENDPOINT> \
    --cert <PATH TO CERTIFICATE FILE> \
    --key <PATH TO PRIVATE KEY FILE>"
```

This will run the sample using the latest released version of the SDK rather than the version compiled from source. If you are wanting to try the samples without first compiling the SDK, then make sure to add `-P latest-release` and to have Maven download the Java V2 SDK. **This works for all samples in the Java V2 SDK.**

### Sample Help

All samples will show their options and arguments by passing in `--help`. For example:
``` sh
mvn compile exec:java \
    -pl samples/Mqtt/Mqtt5X509 \
    -Dexec.args=" \
    --help"
```

Will result in the following print output:
```
MQTT5 X509 Sample (mTLS)

Required:
  --endpoint <ENDPOINT>     IoT endpoint hostname
  --cert <CERTIFICATE>      Path to certificate file (PEM)
  --key <PRIVATE_KEY>       Path to private key file (PEM)

Optional:
  --client_id <CLIENT_ID>   MQTT client ID (default: generated)
  --topic <TOPIC>           Topic to use (default: test/topic)
  --message <MESSAGE>       Message payload (default: "Hello from mqtt5 sample")
  --count <N>               Messages to publish (0 = infinite, default: 5)
```

The sample will not run without the required arguments.

### Enable Logging

Instructions to enable logging are available in the [FAQ](../documents/FAQ.md) under [How do I enable logging](../documents/FAQ.md#how-do-i-enable-logging).

## ⚠️ Usage disclaimer

These code examples interact with services that may incur charges to your AWS account. For more information, see [AWS Pricing](https://aws.amazon.com/pricing/).

Additionally, example code might theoretically modify or delete existing AWS resources. As a matter of due diligence, do the following:

- Be aware of the resources that these examples create or delete.
- Be aware of the costs that might be charged to your account as a result.
- Back up your important data.
