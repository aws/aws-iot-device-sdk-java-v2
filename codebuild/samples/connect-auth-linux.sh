#!/bin/bash

set -e

env

pushd $CODEBUILD_SRC_DIR/samples/CustomAuthorizerConnect

ENDPOINT=$(aws secretsmanager get-secret-value --secret-id "unit-test/endpoint" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')

mvn compile

echo "Mqtt (Direct) Connect with Custom Authorizer test"
mvn exec:java -Dexec.mainClass="customauthorizerconnect.CustomAuthorizerConnect" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--key,/tmp/privatekey.pem,--cert,/tmp/certificate.pem,--auth_name,TestSDKAuthorizer"

popd
