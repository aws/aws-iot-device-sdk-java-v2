#!/bin/bash

set -e
set -o pipefail

pushd $CODEBUILD_SRC_DIR/samples/Pkcs11Connect

# ENDPOINT=$(aws secretsmanager get-secret-value --secret-id "unit-test/endpoint" --query "SecretString" | cut -f2 -d":" | sed -e 's/[\\\"\}]//g')

# # from hereon commands are echoed. don't leak secrets
# set -x

# softhsm2-util --version

# # SoftHSM2's default tokendir path might be invalid on this machine
# # so set up a conf file that specifies a known good tokendir path
# mkdir -p /tmp/tokens
# export SOFTHSM2_CONF=/tmp/softhsm2.conf
# echo "directories.tokendir = /tmp/tokens" > /tmp/softhsm2.conf

# # create token
# softhsm2-util --init-token --free --label my-token --pin 0000 --so-pin 0000

# # add private key to token (must be in PKCS#8 format)
# softhsm2-util --import /tmp/privatekey_p8.pem --token my-token --label my-key --id BEEFCAFE --pin 0000

# # Compile and run sample
# mvn compile
# echo "PKCS11 Connect test"
# mvn exec:java -Dexec.mainClass="pkcs11connect.Pkcs11Connect" -Daws.crt.ci="True" -Dexec.arguments="--endpoint,$ENDPOINT,--cert,/tmp/certificate.pem,--pkcs11_lib,/usr/lib/softhsm/libsofthsm2.so,--pin,0000,--token_label,my-token,--key_label,my-key"

popd
