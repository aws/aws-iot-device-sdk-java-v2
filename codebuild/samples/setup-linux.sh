#!/bin/bash

set -e

env

# build java package
cd $CODEBUILD_SRC_DIR

ulimit -c unlimited
mvn compile
mvn install -DskipTests=true

aws secretsmanager get-secret-value --secret-id "unit-test/certificate" --query "SecretString" | cut -f2 -d":" | cut -f2 -d\" > /tmp/certificate.pem
aws secretsmanager get-secret-value --secret-id "unit-test/privatekey" --query "SecretString" | cut -f2 -d":" | cut -f2 -d\" > /tmp/privatekey.pem


