#!/bin/bash

set -e

env

pushd $CODEBUILD_SRC_DIR/samples/CustomAuthorizerConnect

ENDPOINT=$(aws secretsmanager get-secret-value --secret-id "unit-test/endpoint" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')

mvn compile

echo "Mqtt Connect with Custom Authorizer test"
mvn exec:java -Dexec.mainClass="customauthorizerconnect.CustomAuthorizerConnect" -Daws.crt.ci="True" -Daws.crt.debugnative="True" -Daws.crt.log.level="Trace" -Daws.crt.log.destination="Stdout" -Dexec.arguments="--endpoint,$ENDPOINT,--cert,/tmp/certificate.pem,--key,/tmp/privatekey.pem,--ca_file,/tmp/rootca.pem,--auth_name,TestSDKAuthorizer,--auth_username,V2SDK"

popd
