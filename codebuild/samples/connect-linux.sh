#!/bin/bash

set -e

env

pushd $CODEBUILD_SRC_DIR/samples/BasicConnect

ENDPOINT=$(aws secretsmanager get-secret-value --secret-id "unit-test/endpoint" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')

mvn compile

echo "Basic Mqtt (Direct) Connect test"
mvn -P debug exec:java -Dexec.mainClass="basicconnect.BasicConnect" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--key,/tmp/privatekey.pem,--cert,/tmp/certificate.pem"

popd

pushd $CODEBUILD_SRC_DIR/samples/WebsocketConnect

mvn compile

echo "Websocket Connect test"
mvn -P debug exec:java -Dexec.mainClass="websocketconnect.WebsocketConnect" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--signing_region,us-east-1,--port,443"

popd
