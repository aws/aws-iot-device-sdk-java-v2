import argparse
import boto3
import uuid
import os
import sys
import run_service_test


def delete_thing(thing_name, region):
    try:
        iot_client = boto3.client('iot', region_name=region)
    except Exception:
        print("Error - could not make Boto3 client. Credentials likely could not be sourced")
        return -1

    thing_principals = None
    try:
        thing_principals = iot_client.list_thing_principals(thingName=thing_name)
    except Exception:
        print ("Could not get thing principals!")
        return -1

    try:
        if thing_principals != None:
            if thing_principals["principals"] != None:
                if len(thing_principals["principals"]) > 0:
                    for principal in thing_principals["principals"]:
                        certificate_id = principal.split("/")[1]
                        iot_client.detach_thing_principal(thingName=thing_name, principal=principal)
                        iot_client.update_certificate(certificateId=certificate_id, newStatus ='INACTIVE')
                        iot_client.delete_certificate(certificateId=certificate_id, forceDelete=True)
    except Exception as exception:
        print (exception)
        print ("Could not delete certificate!")
        return -1

    try:
        iot_client.delete_thing(thingName=thing_name)
    except Exception as exception:
        print (exception)
        print ("Could not delete IoT thing!")
        return -1

    print ("IoT thing deleted successfully")
    return 0


def main():
    argument_parser = argparse.ArgumentParser(
        description="Run service test in CI")
    argument_parser.add_argument(
        "--input-uuid", required=False, help="UUID for thing name")
    argument_parser.add_argument(
        "--thing-name-prefix", required=False, default="", help="Prefix for a thing name")
    argument_parser.add_argument(
        "--region", required=False, default="us-east-1", help="The name of the region to use")
    argument_parser.add_argument("--input_uuid", required=False,
                                 help="UUID data to replace '$INPUT_UUID' with. Only works in Data field")
    parsed_commands = argument_parser.parse_args()

    current_path = os.path.dirname(os.path.realpath(__file__))
    cfg_file = os.path.join(current_path, "fleet_provisioning_cfg.json")
    input_uuid = parsed_commands.input_uuid if parsed_commands.input_uuid else str(uuid.uuid4())
    # Perform fleet provisioning. If it's successful, a newly created thing should appear.
    result = run_service_test.setup_service_test_and_launch(cfg_file, input_uuid)
    if result != 0:
        sys.exit(result)

    thing_name = parsed_commands.thing_name_prefix + input_uuid
    # Delete a thing created by fleet provisioning.
    result = delete_thing(thing_name, parsed_commands.region)
    sys.exit(result)


if __name__ == "__main__":
    main()
