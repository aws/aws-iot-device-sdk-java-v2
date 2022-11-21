#!/bin/bash

set -e
set -o pipefail

env

pushd $CODEBUILD_SRC_DIR/samples/CustomAuthorizerConnect

ENDPOINT=$(aws secretsmanager get-secret-value --secret-id "ci/endpoint" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')
AUTH_NAME=$(aws secretsmanager get-secret-value --secret-id "ci/CustomAuthorizer/name" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')
AUTH_PASSWORD=$(aws secretsmanager get-secret-value --secret-id "ci/CustomAuthorizer/password" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')

mvn compile

echo "Mqtt Connect with Custom Authorizer test"
mvn exec:java -Dexec.mainClass="customauthorizerconnect.CustomAuthorizerConnect" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--custom_auth_authorizer_name,$AUTH_NAME,--custom_auth_password,$AUTH_PASSWORD"

popd
