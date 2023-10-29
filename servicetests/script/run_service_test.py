# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0.

import argparse
import os
import subprocess
import pathlib
import sys
import json
import boto3

current_folder = os.path.dirname(pathlib.Path(__file__).resolve())
if sys.platform == "win32" or sys.platform == "cygwin":
    current_folder += "\\"
else:
    current_folder += "/"

config_json = None
config_json_arguments_list = []

def setup_json_arguments_list(file, input_uuid=None):
    global config_json
    global config_json_arguments_list

    print("Attempting to get credentials from secrets using Boto3...")
    secrets_client = boto3.client(
        "secretsmanager", region_name=config_json['service_test_region'])
    print("Processing arguments...")

    for argument in config_json['arguments']:
        # Add the name of the argument
        config_json_arguments_list.append(argument['name'])

        # Based on the data present, we need to process and add the data differently
        try:

            # Is there a secret? If so, decode it!
            if 'secret' in argument:
                secret_data = secrets_client.get_secret_value(
                    SecretId=argument['secret'])["SecretString"]

                # Is this supposed to be stored in a file?
                if 'filename' in argument:
                    with open(str(current_folder) + argument['filename'], "w") as file:
                        file.write(secret_data)
                    config_json_arguments_list.append(
                        str(current_folder) + argument['filename'])
                else:
                    config_json_arguments_list.append(secret_data)

            # Raw data? just add it directly!
            elif 'data' in argument:
                tmp_value = argument['data']
                if isinstance(tmp_value, str) and input_uuid is not None:
                    if ("$INPUT_UUID" in tmp_value):
                        tmp_value = tmp_value.replace("$INPUT_UUID", input_uuid)
                if (tmp_value != None and tmp_value != ""):
                    config_json_arguments_list.append(tmp_value)

            # None of the above? Just print an error
            else:
                print("ERROR - unknown or missing argument value!")

        except Exception as e:
            print(f"Something went wrong processing {argument['name']}: {e}!")
            return -1
    return 0


def setup_service_test(file, input_uuid=None):
    global config_json

    file_absolute = pathlib.Path(file).resolve()
    json_file_data = ""
    with open(file_absolute, "r") as json_file:
        json_file_data = json_file.read()

    # Load the JSON data
    config_json = json.loads(json_file_data)

    # Make sure required parameters are all there
    if not 'language' in config_json or not 'service_test_file' in config_json \
       or not 'service_test_region' in config_json or not 'service_test_main_class' in config_json:
        return -1

    # Preprocess service test arguments (get secret data, etc)
    setup_result = setup_json_arguments_list(file, input_uuid)
    if setup_result != 0:
        return setup_result

    print("JSON config file loaded!")
    return 0


def cleanup_service_test():
    global config_json
    global config_json_arguments_list

    for argument in config_json['arguments']:
        config_json_arguments_list.append(argument['name'])

        # Based on the data present, we need to process and add the data differently
        try:
            # Is there a file? If so, clean it!
            if 'filename' in argument:
                if (os.path.isfile(str(current_folder) + argument['filename'])):
                    os.remove(str(current_folder) + argument['filename'])

            # Windows 10 certificate store data?
            if 'windows_cert_certificate' in argument and 'windows_cert_certificate_path' in argument \
                    and 'windows_cert_key' in argument and 'windows_cert_key_path' in argument \
                    and 'windows_cert_pfx_key_path' in argument:

                if (os.path.isfile(str(current_folder) + argument['windows_cert_certificate_path'])):
                    os.remove(str(current_folder) +
                              argument['windows_cert_certificate_path'])
                if (os.path.isfile(str(current_folder) + argument['windows_cert_key_path'])):
                    os.remove(str(current_folder) +
                              argument['windows_cert_key_path'])
                if (os.path.isfile(str(current_folder) + argument['windows_cert_pfx_key_path'])):
                    os.remove(str(current_folder) +
                              argument['windows_cert_pfx_key_path'])

        except Exception as e:
            print(f"Something went wrong cleaning {argument['name']}!")
            return -1


def launch_service_test():
    global config_json
    global config_json_arguments_list

    if (config_json == None):
        print("No configuration JSON file data found!")
        return -1

    exit_code = 0

    print("Launching service test...")

    # Flatten arguments down into a single string
    arguments_as_string = ""
    for i in range(0, len(config_json_arguments_list)):
        arguments_as_string += str(config_json_arguments_list[i])
        if (i+1 < len(config_json_arguments_list)):
            arguments_as_string += " "

    arguments = ["mvn", "compile", "exec:java"]
    arguments.append("-pl")
    arguments.append(config_json['service_test_file'])
    arguments.append("-Dexec.mainClass=" +
                     config_json['service_test_main_class'])
    arguments.append("-Daws.crt.ci=True")

    # We have to do this as a string, unfortunately, due to how -Dexec.args= works...
    argument_string = subprocess.list2cmdline(
        arguments) + " -Dexec.args=\"" + arguments_as_string + "\""
    print(f"Running cmd: {argument_string}")
    service_test_return = subprocess.run(argument_string, shell=True)
    exit_code = service_test_return.returncode

    cleanup_service_test()
    return exit_code


def setup_service_test_and_launch(file, input_uuid=None):
    setup_result = setup_service_test(file, input_uuid)
    if setup_result != 0:
        return setup_result

    print("About to launch service test...")
    return launch_service_test()


def main():
    argument_parser = argparse.ArgumentParser(
        description="Run service test in CI")
    argument_parser.add_argument(
        "--file", required=True, help="Configuration file to pull CI data from")
    argument_parser.add_argument("--input_uuid", required=False,
                                 help="UUID data to replace '$INPUT_UUID' with. Only works in Data field")
    parsed_commands = argument_parser.parse_args()

    file = parsed_commands.file
    input_uuid = parsed_commands.input_uuid

    print(f"Starting to launch service test: config {file}; input UUID: {input_uuid}")
    test_result = setup_service_test_and_launch(file, input_uuid)
    sys.exit(test_result)


if __name__ == "__main__":
    main()
