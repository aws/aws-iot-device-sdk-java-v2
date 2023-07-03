# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0.

# Built-in
import argparse
import os
import subprocess
import pathlib
import sys
import json
# Needs to be installed via pip
import boto3  # - for launching sample

current_folder = os.path.dirname(pathlib.Path(__file__).resolve())
current_folder += "/"

def main():
    print("Testing python script.\n")
    print("current_folder:")
    print(current_folder)

    try:
        client = boto3.client('devicefarm')
    except Exception:
        print("Error - could not make Boto3 client. Credentials likely could not be sourced")
        return -1

    create_upload_response = client.create_upload(
        name='PythonTest.apk',
        type='BUILTIN_FUZZ',
        projectArn='arn:aws:devicefarm:us-west-2:180635532705:project:ee67d437-f890-4c6b-a2eb-8d5ed201252f'
    )

    print(create_upload_response)


if __name__ == "__main__":
    main()