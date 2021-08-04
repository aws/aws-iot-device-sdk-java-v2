#!/bin/bash

set -e

env

pushd $CODEBUILD_SRC_DIR/samples/BasicPubSub

ENDPOINT=$(aws secretsmanager get-secret-value --secret-id "unit-test/endpoint" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')
echo $ENDPOINT
ls /tmp

mvn compile
mvn exec:java -Dexec.mainClass="pubsub.PubSub" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--key,/tmp/privatekey.pem,--cert,/tmp/certificate.pem"

popd
