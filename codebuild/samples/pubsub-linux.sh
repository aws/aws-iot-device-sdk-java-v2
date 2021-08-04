#!/bin/bash

set -e

env

pushd $CODEBUILD_SRC_DIR/samples/BasicPubSub

mvn compile
mvn exec:java -Dexec.mainClass="pubsub.PubSub" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--key,/tmp/privatekey.pem,--cert,/tmp/certificate.pem"

popd
