# Sample Application for the AWS IoT Device SDK Android
[**Return to AWS IoT Device SDK for Android README**](../../documents/ANDROID.md)

The Android sample builds an app that can be installed and run on an Android Device. The app builds and allows you
to run the following [samples](#links-to-individual-sample-readme-files) from aws-iot-device-sdk-java-v2:
* BasicPubSub
* Mqtt5PubSub
* Jobs
* Shadow
* CognitoConnect

*__Jump To:__*

* [Prerequisites](#prerequisites)
  * [Files required to run samples](#files-required-to-run-samples)
* [Build and install sample app](#build-and-install-sample-app)
* [Links to sample README](#links-to-individual-sample-readme-files)


# Prerequisites
The individual samples within the app require specific files to operate. The files must be placed in the
`app/src/main/assets` directory prior to building for the sample app to connect to IoT Core and complete
succesfully. The names of the files must be exactly as provided. Explanations for what each file and
associated argument are doing can be found in the individual [Sample README](#links-to-individual-sample-readme-files)
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
# Change to the app directory
cd samples/Android/app
# Use gradlew from the android folder to build the sample app
../../../android/gradlew build

# Install it to a connected Device
../../../android/gradlew installDebug
```

# Links to individual sample README files
The following links will provide more details on the individual samples available in the
Android sample app.

[**BasicPubSub**](../BasicPubSub/README.md)

[**Mqtt5PubSub**](../Mqtt5/PubSub/README.md)

[**Jobs**](../Jobs/README.md)

[**Shadow**](../Shadow/README.md)

[**CognitoConnect**](../CognitoConnect/README.md)

##### NOTE: The shadow sample does not currently complete on android due to its dependence on stdin keyboard input.