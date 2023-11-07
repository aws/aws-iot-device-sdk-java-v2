# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0.

import argparse
import uuid
import os
import sys
import run_in_ci
import ci_iot_thing


def main():
    argument_parser = argparse.ArgumentParser(
        description="Run Fleet Provisioning test in CI")
    argument_parser.add_argument(
        "--input-uuid", required=False, help="UUID for thing name. UUID will be generated if this option is omit")
    argument_parser.add_argument(
        "--thing-name-prefix", required=False, default="",
        help="Prefix for a thing name, should be the same that Fleet Provisioning template uses")
    argument_parser.add_argument(
        "--region", required=False, default="us-east-1", help="The name of the region to use")
    argument_parser.add_argument(
        "--mqtt-version", required=True, choices=[3, 5], type=int, help="MQTT protocol version to use")
    parsed_commands = argument_parser.parse_args()

    current_path = os.path.dirname(os.path.realpath(__file__))
    cfg_file_pfx = "mqtt3_" if parsed_commands.mqtt_version == 3 else "mqtt5_"
    cfg_file = os.path.join(current_path, cfg_file_pfx + "fleet_provisioning_cfg.json")
    input_uuid = parsed_commands.input_uuid if parsed_commands.input_uuid else str(uuid.uuid4())

    # Perform fleet provisioning. If it's successful, a newly created thing should appear.
    test_result = run_in_ci.setup_and_launch(cfg_file, input_uuid)

    # Delete a thing created by fleet provisioning.
    # NOTE We want to try to delete thing even if test was unsuccessful.
    thing_name = parsed_commands.thing_name_prefix + input_uuid
    delete_result = ci_iot_thing.delete_iot_thing(thing_name, parsed_commands.region)

    if test_result != 0 or delete_result != 0:
        sys.exit(-1)

if __name__ == "__main__":
    main()
