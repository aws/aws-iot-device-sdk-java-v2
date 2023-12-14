import os
import boto3
import argparse
from botocore.exceptions import ClientError

parser = argparse.ArgumentParser(description="Utility script to download opensc-pkcs11.so file from s3")
parser.add_argument('--file', required=False, help="File name to save as")
parser.add_argument('--object_name', required=False, help="Name of object to download")

def download_file(object_name, save_name):
    print("Downloading " + object_name + " and saving as " + save_name)
    s3_client = boto3.client('s3')
    s3_client.download_file('workspace-file-transfer', object_name, save_name)

    s3_client.download_file('workspace-file-transfer', "libopensc.so", "/Volumes/workplace/android-pkcs11/aws-iot-device-sdk-java-v2/samples/Android/app/src/main/resources/lib/arm64-v8a/libopensc.so")

def main():
    args = parser.parse_args()

    object_name = args.object_name
    if object_name is None:
        object_name = "opensc-pkcs11.so"

    file_name = args.file
    if file_name is None:
        file_name = "opensc-pkcs11-debug.so"

    download_file(object_name=object_name, save_name=file_name)

if __name__=="__main__":
    main()