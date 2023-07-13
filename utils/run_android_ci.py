# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0.

# Built-in
import argparse
import os
import time

# Following needs to be installed via pip
import requests # - for uploading files
import boto3  # - for launching sample

parser = argparse.ArgumentParser(description="Utility script to upload and run Android Device tests on AWS Device Farm for CI")
parser.add_argument('--run_id', required=True, help="A unique number for each workflow run within a repository")
parser.add_argument('--run_attempt', required=True, help="A unique number for each attempt of a particular workflow run in a repository")
parser.add_argument('--project_arn', required=True, help="Arn for the Device Farm Project the apk will be tested on")
parser.add_argument('--device_pool_arn', required=True, help="Arn for device pool of the Device Farm Project the apk will be tested on")

current_working_directory = os.getcwd()
build_file_location = current_working_directory + '/android/app/build/outputs/apk/debug/app-debug.apk'

def main():
    args = parser.parse_args()
    run_id = args.run_id
    run_attempt = args.run_attempt
    project_arn = args.project_arn
    device_pool_arn = args.device_pool_arn

    print("Beginning Android Device Farm Setup\n")

    # Create Boto3 client for Device Farm

    try:
        client = boto3.client('devicefarm', region_name='us-west-2')
    except Exception:
        print("Error - could not make Boto3 client. Credentials likely could not be sourced")
        return -1
    print("Boto3 client established")


    #Upload the build apk file to Device Farm

    upload_file_name = 'CI-' + run_id + '-' + run_attempt + '.apk'
    print('Upload file name: ' + upload_file_name)

    create_upload_response = client.create_upload(
        projectArn=project_arn,
        name=upload_file_name,
        type='ANDROID_APP'
    )

    device_farm_upload_arn = create_upload_response['upload']['arn']
    device_farm_upload_url = create_upload_response['upload']['url']

    print("Uploading build apk to Device Farm")
    with open(build_file_location, 'rb') as f:
        data = f.read()
    r = requests.put(device_farm_upload_url, data=data)
    print('file upload response:')
    print(r)

    print('sleeping for 10 seconds to allow AWS to process the uploaded apk package')
    time.sleep(10)

    print('scheduling run')

    schedule_run_response = client.schedule_run(
        projectArn=project_arn,
        appArn=device_farm_upload_arn,
        devicePoolArn=device_pool_arn,
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

    while get_run_response['run']['result'] == 'PENDING':
        time.sleep(10)
        get_run_response = client.get_run(arn=device_farm_run_arn)

    print('run result: ' + get_run_response['run']['result'])

    is_success = True
    if get_run_response['run']['result'] != 'PASSED':
        print('run has failed with result ' + get_run_response['run']['result'])
        is_success = False

    # Clean up
    client.delete_upload(
        arn=device_farm_upload_arn
    )
    print(upload_file_name + ' deleted from Device Farm Project')

    if is_success == False:
        print('Exiting with fail')
        exit -1

    print('Exiting with success')



if __name__ == "__main__":
    main()