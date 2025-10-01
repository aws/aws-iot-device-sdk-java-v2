# Sample Application for the AWS IoT Device SDK Android
[**Return to AWS IoT Device SDK for Android README**](../../documents/ANDROID.md)

The Android sample builds an app that can be installed and run on an Android Device. The app builds and allows you
to run the following [samples](#links-to-individual-sample-readme-files) from aws-iot-device-sdk-java-v2:
* [Mqtt5PubSub](../Mqtt/Mqtt5X509/README.md)
* [KeyChainPubSub](./AndroidKeyChainPubSub/README.md)

*__Jump To:__*

* [Prerequisites](#prerequisites)
  * [Files required to run samples](#files-required-to-run-samples)
* [Build and install sample app](#build-and-install-sample-app)
* [Links to sample README](#links-to-individual-sample-readme-files)


# Prerequisites
The individual samples within the app require specific files to operate. The files **MUST** be placed in the
**`app/src/main/assets`** directory prior to building for the sample app to connect to IoT Core and complete
successfully. The names of the files must be exactly as provided. Explanations for what each file and
associated argument are doing can be found in the individual [Sample README](#links-to-individual-sample-readme-files)
files linked below.

## Files required to run samples

### Files required by all samples:
* `endpoint.txt` - IoT ATS Endpoint

### Required to run Mqtt5X509 sample
* `certificate.pem` - IoT Thing Certificate
* `privatekey.pem` - IoT Thing Private Key

### Required to run KeyChainPubSub
* `keychainAlias.txt` - Alias of PrivateKey to access from KeyChain
  * The sample app must have permission to access KeyChain. The PrivateKey for given alias must also be granted prior to running the KeyChainPubSub sample. This can be done by selecting the `KeyChain Alias Permission` from the `Select a Sample` dropdown menu and selecting the PrivateKey associated with the alias.

###### Optional Files for all PubSub samples
* `topic.txt` - specifies --topic CLI argument
* `message.txt` - specifies --message CLI argument
* `count.txt` - specifies --count CLI argument

### Optional files for all Samples:
* `clientId.txt` - specifies --clientId CLI argument

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

[**Mqtt5PubSub**](../Mqtt/Mqtt5X509/README.md)

[**KeyChainPubSub**](./AndroidKeyChainPubSub/README.md)


# Trouble Shooting
### Error: The file name must end with .xml
This error typically occurs when non-XML files are placed in the `app/src/main/res/` directory. Android enforces strict rules on what can be included in the `res/` folder. If you're working with test or data files (e.g., .txt in our sample), you **MUST** place them in the `app/src/main/assets/` directory instead.

## ⚠️ Usage disclaimer

These code examples interact with services that may incur charges to your AWS account. For more information, see [AWS Pricing](https://aws.amazon.com/pricing/).

Additionally, example code might theoretically modify or delete existing AWS resources. As a matter of due diligence, do the following:

- Be aware of the resources that these examples create or delete.
- Be aware of the costs that might be charged to your account as a result.
- Back up your important data.
