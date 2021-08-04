#!/bin/bash

set -e

env

pushd $CODEBUILD_SRC_DIR/samples/BasicPubSub

ENDPOINT=$(aws secretsmanager get-secret-value --secret-id "unit-test/endpoint" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')
echo $ENDPOINT
ls /tmp

mvn compile

echo "\nMqtt Direct test"
mvn exec:java -Dexec.mainClass="pubsub.PubSub" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--key,/tmp/privatekey.pem,--cert,/tmp/certificate.pem"

echo "\nWebsocket test"
mvn exec:java -Dexec.mainClass="pubsub.PubSub" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--websockets,--region,us-east-1"

popd
