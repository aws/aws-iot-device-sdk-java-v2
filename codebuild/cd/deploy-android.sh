#!/bin/bash

set -ex
set -o pipefail # Ensure if any part of a pipeline fails, it propogates the error through the pipeline

git submodule update --init
cd ./android

# Check if promote release mode is enabled
PROMOTE_RELEASE="${PROMOTE_RELEASE:-false}"

GPG_KEY=$(cat /tmp/aws-sdk-common-runtime.key.asc)
# Publish and release
# As May30th, 2025, the Sonatype OSSRH has been deprecated and replaced with Central Publisher and the new API does't support `findSonatypeStagingRepository`.
# the release will need to be invoked within the same call.
# https://github.com/gradle-nexus/publish-plugin/issues/379

if [ "$PROMOTE_RELEASE" = "true" ]; then
    # close and release the staging repository to promote release
    ./gradlew -PsigningKey=$"$GPG_KEY" -PsigningPassword=$MAVEN_GPG_PASSPHRASE -PsonatypeUsername=$ST_USERNAME -PsonatypePassword=$ST_PASSWORD publishToSonatype closeAndReleaseSonatypeStagingRepository
else
    # close the staging repository without promoting release. NOTES: you need to manually clean up the staging repository in Maven Central.
    ./gradlew -PnewVersion=$DEPLOY_VERSION -PsigningKey=$"$GPG_KEY" -PsigningPassword=$MAVEN_GPG_PASSPHRASE -PsonatypeUsername=$ST_USERNAME -PsonatypePassword=$ST_PASSWORD publishToSonatype closeSonatypeStagingRepository
fi
