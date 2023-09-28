git submodule update --init
cd ./android

GPG_KEY=$(cat /tmp/aws-sdk-common-runtime.key.asc)
# Publish to nexus
./gradlew -PsigningKey=$"$GPG_KEY" -PsigningPassword=$GPG_PASSPHRASE -PsonatypeUsername='aws-sdk-common-runtime' -PsonatypePassword=$ST_PASSWORD publishToAwsNexus closeAwsNexusStagingRepository | tee /tmp/android_deploy.log
# Get the staging repository id and save it
cat /tmp/android_deploy.log | grep "Created staging repository" | cut -d\' -f2 | tee /tmp/android_repositoryId.txt