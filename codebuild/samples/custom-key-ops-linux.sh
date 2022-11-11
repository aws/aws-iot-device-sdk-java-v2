#!/bin/bash

set -e
set -o pipefail
env

pushd $CODEBUILD_SRC_DIR/samples/CustomKeyOpsPubSub

ENDPOINT=$(aws secretsmanager get-secret-value --secret-id "ci/endpoint" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')

mvn compile

echo "Custom Key Ops test"
mvn exec:java -Dexec.mainClass="customkeyopspubsub.CustomKeyOpsPubSub" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--key,/tmp/privatekey_p8.pem,--cert,/tmp/certificate.pem"

popd
