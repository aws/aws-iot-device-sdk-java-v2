# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0.

import sys

import boto3


def delete_iot_thing(thing_name, region):
    try:
        iot_client = boto3.client('iot', region_name=region)
    except Exception as e:
        print(f"ERROR: Could not make Boto3 client. Credentials likely could not be sourced", file=sys.stderr)
        raise

    try:
        thing_principals = iot_client.list_thing_principals(thingName=thing_name)
        print(f"principals: {thing_principals}", file=sys.stderr)
        for principal in thing_principals["principals"]:
            certificate_id = principal.split("/")[1]
            iot_client.detach_thing_principal(thingName=thing_name, principal=principal)
            iot_client.update_certificate(certificateId=certificate_id, newStatus='INACTIVE')
            iot_client.delete_certificate(certificateId=certificate_id, forceDelete=True)
    except Exception:
        print("ERROR: Could not delete certificate for IoT thing {thing_name}, probably thing does not exist",
              file=sys.stderr)
        raise

    try:
        iot_client.delete_thing(thingName=thing_name)
    except Exception:
        raise

    print("IoT thing deleted successfully", file=sys.stderr)

    return 0
