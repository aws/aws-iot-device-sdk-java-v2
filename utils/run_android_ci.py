# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0.

# Built-in
import argparse
import sys
import os
import time
import datetime

# Following needs to be installed via pip
import requests # - for uploading files
import boto3  # - for launching sample

parser = argparse.ArgumentParser(description="Utility script to upload and run Android Device tests on AWS Device Farm for CI")
parser.add_argument('--region', required=True, help="The AWS region device farm project is located")
parser.add_argument('--run_id', required=True, help="A unique number for each workflow run within a repository")
parser.add_argument('--run_attempt', required=True, help="A unique number for each attempt of a particular workflow run in a repository")
parser.add_argument('--project_arn', required=True, help="Arn for the Device Farm Project the apk will be tested on")
parser.add_argument('--device_pool_arn', required=True, help="Arn for device pool of the Device Farm Project the apk will be tested on")
parser.add_argument('--device_pool', required=True, help="Which device pool is being used for this test")

current_working_directory = os.getcwd()
build_file_location = current_working_directory + '/sdk/tests/android/testapp/build/outputs/apk/debug/testapp-debug.apk'
test_file_location = current_working_directory + '/sdk/tests/android/testapp/build/outputs/apk/androidTest/debug/testapp-debug-androidTest.apk'

def main():
    args = parser.parse_args()
    region = args.region
    run_id = args.run_id
    run_attempt = args.run_attempt
    project_arn = args.project_arn
    device_pool_arn = args.device_pool_arn
    device_pool = args.device_pool

    print("Beginning Android Device Farm Setup\n")

    # Create Boto3 client for Device Farm
    try:
        client = boto3.client('devicefarm', region_name=region)
    except Exception:
        print("Error - could not make Boto3 client. Credentials likely could not be sourced")
        sys.exit(-1)
    print("Boto3 client established")


    # Upload the build apk file to Device Farm
    upload_file_name = 'CI-' + run_id + '-' + run_attempt + '-' + device_pool + '.apk'
    print('Upload file name: ' + upload_file_name)

    # Setup upload to Device Farm project
    create_upload_response = client.create_upload(
        projectArn=project_arn,
        name=upload_file_name,
        type='ANDROID_APP'
    )
    device_farm_upload_arn = create_upload_response['upload']['arn']
    device_farm_upload_url = create_upload_response['upload']['url']

    # Upload project apk
    with open(build_file_location, 'rb') as f:
        data = f.read()
    r = requests.put(device_farm_upload_url, data=data)
    print('File upload status code: ' + str(r.status_code) + ' reason: ' + r.reason)

    device_farm_upload_status = client.get_upload(arn=device_farm_upload_arn)
    while device_farm_upload_status['upload']['status'] != 'SUCCEEDED':
        if device_farm_upload_status['upload']['status'] == 'FAILED':
            print('Upload failed to process')
            sys.exit(-1)
        time.sleep(1)
        device_farm_upload_status = client.get_upload(arn=device_farm_upload_arn)

    # Upload the instrumentation test package to Device Farm
    upload_test_file_name = 'CI-' + run_id + '-' + run_attempt + '-' + device_pool + 'tests.apk'
    create_upload_response = client.create_upload(
        projectArn=project_arn,
        name=upload_test_file_name,
        type='INSTRUMENTATION_TEST_PACKAGE'
    )
    device_farm_instrumentation_upload_arn = create_upload_response['upload']['arn']
    device_farm_instrumentation_upload_url = create_upload_response['upload']['url']

    with open(test_file_location, 'rb') as f:
        data_instrumentation = f.read()
    r_instrumentation = requests.put(device_farm_instrumentation_upload_url, data=data_instrumentation)
    print('File upload status code: ' + str(r_instrumentation.status_code) + ' reason: ' + r_instrumentation.reason)

    device_farm_upload_status = client.get_upload(arn=device_farm_instrumentation_upload_arn)
    while device_farm_upload_status['upload']['status'] != 'SUCCEEDED':
        if device_farm_upload_status['upload']['status'] == 'FAILED':
            print('Upload failed to process')
            sys.exit(-1)
        time.sleep(1)
        device_farm_upload_status = client.get_upload(arn=device_farm_instrumentation_upload_arn)

    print('scheduling run')

    schedule_run_response = client.schedule_run(
        projectArn=project_arn,
        appArn=device_farm_upload_arn,
        devicePoolArn=device_pool_arn,
        name=upload_file_name,
        test={
            'type': 'INSTRUMENTATION',
            'testPackageArn': device_farm_instrumentation_upload_arn
        },
        executionConfiguration={
            'jobTimeoutMinutes': 20
        }
    )

    device_farm_run_arn = schedule_run_response['run']['arn']

    run_start_time = schedule_run_response['run']['started']
    run_start_date_time = run_start_time.strftime("%m/%d/%Y, %H:%M:%S")
    print('run scheduled at ' + run_start_date_time)

    get_run_response = client.get_run(arn=device_farm_run_arn)
    while get_run_response['run']['result'] == 'PENDING':
        time.sleep(10)
        get_run_response = client.get_run(arn=device_farm_run_arn)

    run_end_time = datetime.datetime.now()
    run_end_date_time = run_end_time.strftime("%m/%d/%Y, %H:%M:%S")
    print('Run ended at ' + run_end_date_time + ' with result: ' + get_run_response['run']['result'])

    is_success = True
    if get_run_response['run']['result'] != 'PASSED':
        print('run has failed with result ' + get_run_response['run']['result'])
        is_success = False

    # Clean up
    print('Deleting ' + upload_file_name + ' Device Farm project')
    client.delete_upload(
        arn=device_farm_upload_arn
    )
    client.delete_upload(
        arn=device_farm_instrumentation_upload_arn
    )

    if is_success == False:
        print('Exiting with fail')
        sys.exit(-1)

    print('Exiting with success')

if __name__ == "__main__":
    main()