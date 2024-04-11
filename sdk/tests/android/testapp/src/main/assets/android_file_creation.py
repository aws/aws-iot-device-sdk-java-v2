import os
import boto3
from botocore.exceptions import ClientError

# This file is used for running unit tests on android devices in AWS Device Farm.
# Variables and files for testing in Github CI are set to environment variables which are not accessible on
# Android devices. They must be packaged into the app itself. This is done by converting the necessary
# files and variables into txt files and storing them as assets prior to building the test app.

cwd = os.getcwd()

def saveStringToFile(fileData, fileName):
    secret_file = open(cwd + "/" + fileName, "w")
    secret_file.write(fileData)
    secret_file.close()
    print(fileName + " file created")

def getSecretAndSaveToFile(client, secretName, fileName):
    try:
        secret_value_response = client.get_secret_value(SecretId=secretName)
    except ClientError as e:
        print("Error encountered")
        if e.response['Error']['Code'] == 'ResourceNotFoundException':
            print("The requested secret " + secretName + " was not found")
        elif e.response['Error']['Code'] == 'InvalidRequestException':
            print("The request was invalid due to:", e)
        elif e.response['Error']['Code'] == 'InvalidParameterException':
            print("The request had invalid params:", e)
        elif e.response['Error']['Code'] == 'DecryptionFailure':
            print("The requested secret can't be decrypted using the provided KMS key:", e)
        elif e.response['Error']['Code'] == 'InternalServiceError':
            print("An error occurred on service side:", e)
        else:
            print(e)
            raise e
    else:
        if 'SecretString' in secret_value_response:
            saveStringToFile(secret_value_response['SecretString'], fileName)
        else:
            print("SecretString not found in response")

def main():
    print("Setting up Android test assets")

    # Most testing varibales and files are pulled from Secrets Manager
    session = boto3.session.Session()
    try:
        client = session.client(
            service_name='secretsmanager',
            region_name='us-east-1'
        )
    except Exception:
        print("Error - could not make Boto3 secrets manager client.")
    print("Boto3 client created")

    getSecretAndSaveToFile(client, "ci/endpoint", "endpoint.txt")
    getSecretAndSaveToFile(client, "ci/PubSub/cert", "pubSubCertificate.pem")
    getSecretAndSaveToFile(client, "ci/PubSub/key", "pubSubPrivatekey.pem")
    getSecretAndSaveToFile(client, "ci/Cognito/identity_id", "cognitoIdentity.txt")
    getSecretAndSaveToFile(client, "ci/Jobs/cert", "jobsCertificate.pem")
    getSecretAndSaveToFile(client, "ci/Jobs/key", "jobsPrivatekey.pem")
    getSecretAndSaveToFile(client, "ci/Shadow/cert", "shadowCertificate.pem")
    getSecretAndSaveToFile(client, "ci/Shadow/key", "shadowPrivatekey.pem")
    getSecretAndSaveToFile(client, "ci/mqtt5/us/mqtt5_thing/cert", "mqtt5PubSubCertificate.pem")
    getSecretAndSaveToFile(client, "ci/mqtt5/us/mqtt5_thing/key", "mqtt5PubSubPrivatekey.pem")
    getSecretAndSaveToFile(client, "ci/PubSub/cert", "customKeyOpsCert.pem")
    getSecretAndSaveToFile(client, "ci/PubSub/keyp8", "customKeyOpsKey.pem")

    print("Android test asset creation complete")


if __name__ == "__main__":
    main()