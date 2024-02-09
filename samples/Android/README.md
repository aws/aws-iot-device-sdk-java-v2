# Sample Application for the AWS IoT Device SDK Android
[**Return to AWS IoT Device SDK for Android README**](../../documents/ANDROID.md)

The Android sample builds an app that can be installed and run on an Android Device. The app builds and allows you
to run the following [samples](#links-to-individual-sample-readme-files) from aws-iot-device-sdk-java-v2:
* [Mqtt5PubSub](../Mqtt5/PubSub/README.md)
* [Mqtt3PubSub](../BasicPubSub/README.md)
* [KeyChainPubSub](./AndroidKeyChainPubSub/README.md)
* [Jobs](../Jobs/README.md)
* [Shadow](../Shadow/README.md)
* [CognitoConnect](../CognitoConnect/README.md)

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

### Required to run Mqtt5PubSub and Mqtt3PubSub samples
* `certificate.pem` - IoT Thing Certificate
* `privatekey.pem` - IoT Thing Private Key

### Required to run KeyChainPubSub
* `keychainAlias.txt` - Alias of PrivateKey to access from KeyChain
  * Permission to access the PrivateKey for given alias must be approved for the app prior to running the app. This can be done by selecting the `KeyChain Alias Permission` from the `Select a Sample` dropdown menu.

###### Optional Files for all PubSub samples
* `topic.txt` - specifies --topic CLI argument
* `message.txt` - specifies --message CLI argument
* `count.txt` - specifies --count CLI argument

### Required to run Jobs and Shadow sample
* `certificate.pem` - IoT Thing Certificate
* `privatekey.pem` - IoT Thing Private Key
* `thingName.txt` - IoT Thing Name used by sample

### Required to run Cognito Client sample:
* `cognitoIdentity.txt` - Cognito identity ID
* `signingRegion.txt` - Signing region

### Optional files for all Samples:
* `rootca.pem` - override the default system trust store
* `clientId.txt` - specifies --clientId CLI argument
* `port.txt` - specifies --port CLI argument
* `verbosity.txt` - specifies --verbosity CLI argument

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

[**Mqtt5PubSub**](../Mqtt5/PubSub/README.md)

[**Mqtt3PubSub**](../BasicPubSub/README.md)

[**KeyChainPubSub**](AndroidKeyChainPubSub/README.md)

[**Jobs**](../Jobs/README.md)

[**Shadow**](../Shadow/README.md)

[**CognitoConnect**](../CognitoConnect/README.md)

##### NOTE: The shadow sample does not currently complete on android due to its dependence on stdin keyboard input.