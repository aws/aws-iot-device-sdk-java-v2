# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0.

# Built-in
import argparse
import os
import subprocess
import pathlib
import sys
import json
import time

# Following needs to be installed via pip
import requests # - for uploading files
import boto3  # - for launching sample

current_folder = os.path.dirname(pathlib.Path(__file__).resolve())
current_folder += "/"
current_working_directory = os.getcwd()
build_file_location = current_working_directory + '/android/app/build/outputs/apk/debug/app-debug.apk'

def main():
    print("Testing python script.\n")
    print("current_folder: " + current_folder)
    print("current working dir: " + current_working_directory)

    # Create Boto3 client for Device Farm

    try:
        client = boto3.client('devicefarm', region_name='us-west-2')
    except Exception:
        print("Error - could not make Boto3 client. Credentials likely could not be sourced")
        return -1
    print("Boto3 client established")


    #Upload the build apk file to Device Farm

    upload_file_name = 'CI-githubRunID-GithubRunNumber.apk'

    create_upload_response = client.create_upload(
        projectArn='arn:aws:devicefarm:us-west-2:180635532705:project:ee67d437-f890-4c6b-a2eb-8d5ed201252f',
        name=upload_file_name,
        type='ANDROID_APP'
    )

    device_farm_upload_arn = create_upload_response['upload']['arn']
    device_farm_upload_url = create_upload_response['upload']['url']

    print("Uploading build apk to Device Farm")
    with open(build_file_location, 'rb') as f:
        data = f.read()
    r = requests.put(device_farm_upload_url, data=data)
    print('file upload response')
    print(r)

    print('sleeping for 10 seconds to allow AWS to process the uploaded apk package')
    time.sleep(10)

    print('scheduling run')

    schedule_run_response = client.schedule_run(
        projectArn='arn:aws:devicefarm:us-west-2:180635532705:project:ee67d437-f890-4c6b-a2eb-8d5ed201252f',
        appArn=device_farm_upload_arn,
        devicePoolArn='arn:aws:devicefarm:us-west-2:180635532705:devicepool:ee67d437-f890-4c6b-a2eb-8d5ed201252f/79f3baaa-b80e-4d4d-86a8-075aadbac4d0',
        name=upload_file_name,
        test={
            'type': 'BUILTIN_FUZZ'
        },
        executionConfiguration={
            'jobTimeoutMinutes': 20
        }
    )
    print(schedule_run_response)
    device_farm_run_arn = schedule_run_response['run']['arn']

    get_run_response = client.get_run(arn=device_farm_run_arn)
    print(get_run_response)

    while get_run_response['run']['result'] == 'PENDING':
        print('current run result: ' + get_run_response['run']['result'])
        time.sleep(5)
        get_run_response = client.get_run(arn=device_farm_run_arn)

    print('run result: ' + get_run_response['run']['result'])

    is_success = True

    if get_run_response['run']['result'] != 'PASSED':
        print('run has failed with result ' + get_run_response['run']['result'])
        is_success = False



    if is_success == False:
        exit -1




if __name__ == "__main__":
    main()