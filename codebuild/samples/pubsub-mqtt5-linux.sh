#!/bin/bash

set -e
set -o pipefail

env

pushd $CODEBUILD_SRC_DIR/samples/Mqtt5/PubSub

ENDPOINT=$(aws secretsmanager get-secret-value --secret-id "ci/endpoint" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')

mvn compile

echo "MQTT5 PubSub test"
mvn exec:java -Dexec.mainClass="mqtt5.pubsub.PubSub" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--key,/tmp/privatekey.pem,--cert,/tmp/certificate.pem"

popd
