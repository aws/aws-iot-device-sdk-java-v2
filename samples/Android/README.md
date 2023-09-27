# Sample Application for the AWS IoT Device SDK Android
[**Return to AWS IoT Device SDK for Android README**](../../documents/ANDROID.md)

The Android sample builds an app that can be installed and run on an Android Device. The app builds and allows you
to run the following [samples](#links-to-sample-readme) from aws-iot-device-sdk-java-v2:
* BasicPubSub
* Mqtt5PubSub
* Jobs
* Shadow
* CognitoConnect

*__Jump To:__*

* [Prerequisites](#prerequisites)
  * [Files required to run samples](#files-required-to-run-samples)
* [Build and install sample app](#build-and-install-sample-app)
* [Links to sample README](#links-to-sample-readme)


# Prerequisites
The individual samples within the app require specific files to operate. The files must be placed in the
`app/src/main/assets` directory prior to building for the sample app to connect to IoT Core and complete
succesfully. The names of the files must be exactly as provided. Explanations for what each file and
associated argument are doing can be found in the individual [Sample README](#links-to-sample-readme)
files linked below.

## Files required to run samples

### Files required by all samples:
* `endpoint.txt` - IoT ATS Endpoint

### Required to run BasicPubSub, Mqtt5PubSub, Jobs, and Shadow samples
* `certificate.pem` - IoT Thing Certificate
* `privatekey.pem` - IoT Thing Private Key

### Required to run Cognito Client sample:
* `cognitoIdentity.txt` - Cognito identity ID
* `signingRegion.txt` - Signing region

### Optional files:
* `rootca.pem` - override the default system trust store
* `clientId.txt` - specifies --clientId CLI argument
* `topic.txt` - specifies --topic CLI argument
* `message.txt` - specifies --message CLI argument
* `port.txt` - specifies --port CLI argument
* `thingName.txt` - specifies --thingName CLI argument

# Build and install sample app

``` sh
cd samples/Android
# Create a workspace directory to hold all the SDK files
mkdir sdk-workspace
cd sdk-workspace
# Clone the SDK repository
# (Use the latest version of the SDK here instead of "v1.17.0)
git clone --branch v1.17.0 --recurse-submodules https://github.com/aws/aws-iot-device-sdk-java-v2.git
# Compile and install the SDK for Android
cd aws-iot-device-sdk-java-v2/android
./gradlew build
# Install SDK locally
./gradlew publishToMavenLocal
```

# Links to sample README
The following links will provide more details on the individual samples available in the
Android sample app.

[**BasicPubSub**](../BasicPubSub/README.md)

[**Mqtt5PubSub**](../Mqtt5/PubSub/README.md)

[**Jobs**](../Jobs/README.md)

[**Shadow**](../Shadow/README.md)

[**CognitoConnect**](../CognitoConnect/README.md)

##### NOTE: The shadow sample does not currently complete on android due to its dependence on stdin keyboard input.