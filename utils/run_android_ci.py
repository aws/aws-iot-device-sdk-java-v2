# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0.

# Built-in
import argparse
import os
import subprocess
import pathlib
import sys
import json

# Following needs to be installed via pip
import requests # - for uploading files
import boto3  # - for launching sample

current_folder = os.path.dirname(pathlib.Path(__file__).resolve())
current_folder += "/"
current_working_directory = os.getcwd()

def main():
    print("Testing python script.\n")
    print("current_folder: " + current_folder)
    print("current working dir: " + current_working_directory)

    try:
        client = boto3.client('devicefarm', region_name='us-west-2')
    except Exception:
        print("Error - could not make Boto3 client. Credentials likely could not be sourced")
        return -1

    print("Boto3 client established")

    create_upload_response = client.create_upload(
        projectArn='arn:aws:devicefarm:us-west-2:180635532705:project:ee67d437-f890-4c6b-a2eb-8d5ed201252f',
        name='PythonTest.apk',
        type='ANDROID_APP'
    )

    print("\n")
    print(create_upload_response)
    print("\n")
    device_farm_upload_arn = create_upload_response['upload']['arn']
    device_farm_upload_url = create_upload_response['upload']['url']
    print("arn: " + device_farm_upload_arn)
    print("url: " + device_farm_upload_url)

    upload_file_name = 'CI-githubRunID-GithubRunNumber.apk'

    print("Uploading device file to device farm")
    files = {upload_file_name: open(current_folder + 'publish-release.sh', 'rb')}
    r = requests.post(device_farm_upload_url, files=files)

    print('file uploaded attempted')
    print(r)




if __name__ == "__main__":
    main()