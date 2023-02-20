#!/bin/bash

set -e
set -o pipefail

env

pushd $CODEBUILD_SRC_DIR/samples/CognitoConnect

ENDPOINT=$(aws secretsmanager get-secret-value --secret-id "ci/endpoint" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')
COGNITO_IDENTITY=$(aws secretsmanager get-secret-value --secret-id "ci/Cognito/identity_id" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')

mvn compile

echo "Cognito Connect test"
mvn exec:java -Dexec.mainClass="cognitoconnect.CognitoConnect" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--cognito_identity,$COGNITO_IDENTITY,--signing_region,us-east-1"

popd
