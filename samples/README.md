# Sample Applications for the AWS IoT Device SDK for Java v2

## MQTT5 Samples
#### MQTT5 is the recommended MQTT Client. It has many benefits over MQTT311 outlined in the [MQTT5 User Guide](../documents/MQTT5_Userguide.md)
* [MQTT5 PubSub](./Mqtt5/PubSub/README.md)
* [MQTT5 Shared Subscription](./Mqtt5/SharedSubscription/README.md)
## MQTT311 Samples
* [BasicPubSub](./BasicPubSub/README.md)
* [Basic Connect](./BasicConnect/README.md)
* [Websocket Connect](./WebsocketConnect/README.md)
* [Pkcs11 Connect](./Pkcs11Connect/README.md)
* [Pkcs12 Connect](./Pkcs12Connect/README.md)
* [WindowsCert Connect](./WindowsCertConnect/README.md)
* [X509 Connect](./X509CredentialsProviderConnect/README.md)
* [CustomAuthorizer Connect](./CustomAuthorizerConnect/README.md)
* [JavaKeystore Connect](./JavaKeystoreConnect/README.md)
* [Cognito Connect](./CognitoConnect/README.md)
* [CustomKeyOperation Connect](./CustomKeyOpsConnect/README.md)
* [Shadow](./Shadow/README.md)
* [Jobs](./Jobs/README.md)
* [fleet provisioning](./FleetProvisioning/README.md)
* [Android Sample](./Android/README.md)
## Other Samples
* [Greengrass Discovery](./Greengrass/README.md)
* [Greengrass IPC](./GreengrassIPC/README.md)

### Note

Note that **all samples will show their options by passing in `--help`**. For example:

```sh
mvn compile exec:java -pl samples/BasicPubSub -Dexec.mainClass=pubsub.PubSub -Dexec.args='--help'
```

Additionally, you can enable logging in all samples. To enable logging in the samples, you will need to set the following system properties when running the samples:

```sh
-Daws.crt.debugnative=true
-Daws.crt.log.destination=File
-Daws.crt.log.level=Trace
-Daws.crt.log.filename=<path and filename>
```

* `aws.crt.debugnative`: Whether to debug native (C/C++) code. Can be either `true` or `false`.
* `aws.crt.log.destination`: Where the logs are outputted to. Can be `File`, `Stdout` or `Stderr`. Defaults to `Stderr`.
* `aws.crt.log.level`: The level of logging shown. Can be `Trace`, `Debug`, `Info`, `Warn`, `Error`, `Fatal`, or `None`. Defaults to `Warn`.
* `aws.crt.log.filename`: The path to save the log file. Only needed if `aws.crt.log.destination` is set to `File`.

For example, to run `BasicPubSub` with logging you could use the following:

```sh
mvn compile exec:java -pl samples/BasicPubSub -Daws.crt.debugnative=true -Daws.crt.log.level=Debug -Daws.crt.log.destionation=Stdout -Dexec.mainClass=pubsub.PubSub -Dexec.args='--endpoint <endpoint> --cert <path to cert> --key <path to key> --ca_file <path to ca file>'
```

### Running Samples with latest SDK release

If you want to run a sample using the latest release of the SDK, instead of compiled from source, you need to use the `latest-release` profile. For example:

```sh
mvn -P latest-release compile exec:java -pl samples/BasicPubSub -Dexec.mainClass=pubsub.PubSub -Dexec.args='--endpoint <endpoint> --cert <path to certificate> --key <path to private key> --ca_file <path to root CA>'
```

This will run the sample using the latest released version of the SDK rather than the version compiled from source. If you are wanting to try the samples without first compiling the SDK, then make sure to add `-P latest-release` and to have Maven download the Java V2 SDK. **This works for all samples in the Java V2 SDK.**
