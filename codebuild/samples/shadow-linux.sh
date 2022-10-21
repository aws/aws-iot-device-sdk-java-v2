#!/bin/bash

set -e

env

pushd $CODEBUILD_SRC_DIR/samples/Shadow

ENDPOINT=$(aws secretsmanager get-secret-value --secret-id "ci/endpoint" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')

mvn compile

echo "Shadow test"
mvn exec:java -Dexec.mainClass="shadow.ShadowSample" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--key,/tmp/privatekey.pem,--cert,/tmp/certificate.pem,--thing_name,CI_CodeBuild_Thing"

popd
