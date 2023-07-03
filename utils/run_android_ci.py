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

if __name__ == "__main__":
    main()