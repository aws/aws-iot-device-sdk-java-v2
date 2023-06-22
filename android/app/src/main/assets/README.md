# Android App
[**Return to aws-iot-device-sdk-java-v2 README**](../../../../../../aws-iot-device-sdk-java-v2/README.md)

# Files used by IoT Samples App
Files must be placed in the assets directory for the sample app to connect to IoT Core.
## Required by all samples:
* endpoint.txt - IoT ATS Endpoint

## Requiret to run BasicPubSub, Jobs, and Shadow samples
* certificate.pem - IoT Thing Certificate
* privatekey.pem - IoT Thing Private Key
## Required to run Cognito Client sample:
* cognitoIdentity.txt - Cognito identity ID
* signingRegion.txt - Signing region


## Optional files:
* rootca.pem - override the default system trust store
* clientId.txt - specifies --clientId CLI argument
* topic.txt - specifies --topic CLI argument
* message.txt - specifies --message CLI argument
* port.txt - specifies --port CLI argument
* thingName.txt - specifies --thingName CLI argument

# Links to samples
[**BasicPubSub**](../../../../../samples/BasicPubSub/README.md)

[**Jobs**](../../../../../samples/Jobs/README.md)

[**Shadow**](../../../../../samples/Shadow/README.md)

[**CognitoConnect**](../../../../../samples/CognitoConnect/README.md)
##### NOTE: The shadow sample does not currently complete on android due to its dependence on stdin keyboard input.