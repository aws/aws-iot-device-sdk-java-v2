import boto3
import uuid
import json
import os
import pprint
import subprocess
import re
import random
import sys
from time import sleep

##############################################
# Cleanup Certificates and Things and created certificate and private key file


def delete_thing_with_certi(thingName, certiId, certiArn):
    client.detach_thing_principal(
        thingName=thingName,
        principal=certiArn)
    client.update_certificate(
        certificateId=certiId,
        newStatus='INACTIVE')
    client.delete_certificate(certificateId=certiId, forceDelete=True)
    client.delete_thing(thingName=thingName)
    os.remove(os.environ["DA_CERTI"])
    os.remove(os.environ["DA_KEY"])

# Export the testing log and upload it to S3 bucket


def process_logs(log_group, log_stream, thing_name):
    logs_client = boto3.client('logs')
    response = logs_client.get_log_events(
        logGroupName=log_group,
        logStreamName=log_stream
    )
    log_file = "DA_Log_Java_" + thing_name + ".log"
    f = open(log_file, 'w')
    for event in response["events"]:
        f.write(event['message'])
    f.close()

    try:
        s3_bucket_name = secrets_client.get_secret_value(
            SecretId="ci/DeviceAdvisor/s3bucket")["SecretString"]
        s3.Bucket(s3_bucket_name).upload_file(log_file, log_file)
        print("[Device Advisor] Device Advisor Log file uploaded to " +
              log_file, file=sys.stderr)

    except Exception:
        print(
            "[Device Advisor] Error: could not store log in S3 bucket!", file=sys.stderr)

    os.remove(log_file)

# Sleep for a random time


def sleep_with_backoff(base, max):
    sleep(random.randint(base, max))


##############################################
# Initialize variables
# create aws clients
try:
    client = boto3.client('iot', region_name=os.environ["AWS_DEFAULT_REGION"])
    dataClient = boto3.client(
        'iot-data', region_name=os.environ["AWS_DEFAULT_REGION"])
    deviceAdvisor = boto3.client(
        'iotdeviceadvisor', region_name=os.environ["AWS_DEFAULT_REGION"])
    s3 = boto3.resource('s3', region_name=os.environ["AWS_DEFAULT_REGION"])
    secrets_client = boto3.client(
        "secretsmanager", region_name=os.environ["AWS_DEFAULT_REGION"])
except Exception:
    print("[Device Advisor] Error: could not create boto3 clients.", file=sys.stderr)
    exit(-1)

# const
BACKOFF_BASE = 5
BACKOFF_MAX = 10
# 60 minutes divided by the maximum back-off = longest time a DA run can last with this script.
MAXIMUM_CYCLE_COUNT = (3600 / BACKOFF_MAX)

# Did Device Advisor fail a test? If so, this should be true
did_at_least_one_test_fail = False

# load test config
f = open('deviceadvisor/script/DATestConfig.json')
DATestConfig = json.load(f)
f.close()

# create an temporary certificate/key file path
certificate_path = os.path.join(os.getcwd(), 'certificate.pem.crt')
key_path = os.path.join(os.getcwd(), 'private.pem.key')

# load environment variables requried for testing
shadowProperty = os.environ['DA_SHADOW_PROPERTY']
shadowDefault = os.environ['DA_SHADOW_VALUE_DEFAULT']

##############################################
# make sure sdk get installed
print("[Device Advisor]Info: Start to build sdk...", file=sys.stderr)
subprocess.run("mvn clean install -Dmaven.test.skip=true", shell=True)

# Pretty printer for Device Advisor responds
pp = pprint.PrettyPrinter(stream=sys.stderr)

# test result
test_result = {}
for test_suite in DATestConfig['test_suites']:
    test_name = test_suite['test_name']

    disabled = test_suite.get('disabled', False)
    if disabled:
        print("[Device Advisor] Info: "
              f"{test_name} test suite is disabled, skipping", file=sys.stderr)
        continue

    ##############################################
    # create a test thing
    thing_name = "DATest_" + str(uuid.uuid4())
    try:
        thing_group = secrets_client.get_secret_value(
            SecretId="ci/DeviceAdvisor/thing_group")["SecretString"]
        # create_thing_response:
        # {
        # 'thingName': 'string',
        # 'thingArn': 'string',
        # 'thingId': 'string'
        # }
        print("[Device Advisor] Info: Started to create thing "
              f"'{thing_name}'", file=sys.stderr)
        create_thing_response = client.create_thing(
            thingName=thing_name
        )
        os.environ["DA_THING_NAME"] = thing_name

        # Some tests (e.g. Jobs) require the tested things to be a part of the DA group thing.
        client.add_thing_to_thing_group(
            thingGroupName=thing_group,
            thingName=thing_name,
        )

    except Exception as e:
        print(f"[Device Advisor] Error: Failed to create thing '{thing_name}'; "
              f"exception: {e}", file=sys.stderr)
        exit(-1)

    ##############################################
    # create certificate and keys used for testing
    try:
        print("[Device Advisor] Info: Started to create certificate...",
              file=sys.stderr)
        # create_cert_response:
        # {
        # 'certificateArn': 'string',
        # 'certificateId': 'string',
        # 'certificatePem': 'string',
        # 'keyPair':
        #   {
        #     'PublicKey': 'string',
        #     'PrivateKey': 'string'
        #   }
        # }
        create_cert_response = client.create_keys_and_certificate(
            setAsActive=True
        )
        # write certificate to file
        f = open(certificate_path, "w")
        f.write(create_cert_response['certificatePem'])
        f.close()

        # write private key to file
        f = open(key_path, "w")
        f.write(create_cert_response['keyPair']['PrivateKey'])
        f.close()

        # setup environment variable
        os.environ["DA_CERTI"] = certificate_path
        os.environ["DA_KEY"] = key_path

    except Exception:
        try:
            client.delete_thing(thingName=thing_name)
        except Exception:
            print("[Device Advisor] Error: Could not delete thing.",
                  file=sys.stderr)
        print("[Device Advisor] Error: Failed to create certificate.",
              file=sys.stderr)
        exit(-1)

    certificate_arn = create_cert_response['certificateArn']
    certificate_id = create_cert_response['certificateId']

    ##############################################
    # attach policy to certificate
    try:
        policy_name = secrets_client.get_secret_value(
            SecretId="ci/DeviceAdvisor/policy_name")["SecretString"]
        client.attach_policy(
            policyName=policy_name,
            target=certificate_arn
        )
    except Exception as ex:
        print(ex, file=sys.stderr)
        delete_thing_with_certi(thing_name, certificate_id, certificate_arn)
        print("[Device Advisor] Error: Failed to attach policy.", file=sys.stderr)
        exit(-1)

    ##############################################
    # attach certification to thing
    try:
        print(
            "[Device Advisor] Info: Attach certificate to test thing...", file=sys.stderr)
        # attache the certificate to thing
        client.attach_thing_principal(
            thingName=thing_name,
            principal=create_cert_response['certificateArn']
        )

    except Exception:
        delete_thing_with_certi(thing_name, certificate_id, certificate_arn)
        print("[Device Advisor] Error: Failed to attach certificate.",
              file=sys.stderr)
        exit(-1)

    ##############################################
    # Run device advisor

    try:
        ######################################
        # set default shadow, for shadow update, if the
        # shadow does not exists, update will fail
        print("[Device Advisor] Info: About to update shadow.", file=sys.stderr)
        payload_shadow = json.dumps(
            {
                "state": {
                    "desired": {
                        shadowProperty: shadowDefault
                    },
                    "reported": {
                        shadowProperty: shadowDefault
                    }
                }
            })
        shadow_response = dataClient.update_thing_shadow(
            thingName=thing_name,
            payload=payload_shadow)
        get_shadow_response = dataClient.get_thing_shadow(thingName=thing_name)
        # make sure shadow is created before we go to next step
        print("[Device Advisor] Info: About to wait for shadow update.",
              file=sys.stderr)
        while (get_shadow_response is None):
            get_shadow_response = dataClient.get_thing_shadow(
                thingName=thing_name)

        # start device advisor test
        # test_start_response
        # {
        # 'suiteRunId': 'string',
        # 'suiteRunArn': 'string',
        # 'createdAt': datetime(2015, 1, 1)
        # }
        print("[Device Advisor] Info: Start device advisor test: " +
              test_name, file=sys.stderr)
        sleep_with_backoff(BACKOFF_BASE, BACKOFF_MAX)
        test_start_response = deviceAdvisor.start_suite_run(
            suiteDefinitionId=test_suite['test_suite_id'],
            suiteRunConfiguration={
                'primaryDevice': {
                    'thingArn': create_thing_response['thingArn'],
                },
                'parallelRun': True
            })

        # get DA endpoint
        print("[Device Advisor] Info: Getting Device Advisor endpoint.",
              file=sys.stderr)
        endpoint_response = deviceAdvisor.get_endpoint(
            thingArn=create_thing_response['thingArn']
        )
        os.environ['DA_ENDPOINT'] = endpoint_response['endpoint']

        cycle_number = 0
        # This flag is needed to handle the case when some problem occurred on Device Advisor side (e.g. connect fails)
        test_executed = False
        while True:
            cycle_number += 1
            if (cycle_number >= MAXIMUM_CYCLE_COUNT):
                print(f"[Device Advisor] Error: {cycle_number} of cycles lasting "
                      f"{BACKOFF_BASE} to {BACKOFF_MAX} seconds have passed.", file=sys.stderr)
                raise Exception(f"ERROR - {cycle_number} of cycles lasting "
                                f"{BACKOFF_BASE} to {BACKOFF_MAX} seconds have passed.")

            # Add backoff to avoid TooManyRequestsException
            sleep_with_backoff(BACKOFF_BASE, BACKOFF_MAX)
            print(
                "[Device Advisor] Info: About to get Device Advisor suite run.", file=sys.stderr)
            test_result_responds = deviceAdvisor.get_suite_run(
                suiteDefinitionId=test_suite['test_suite_id'],
                suiteRunId=test_start_response['suiteRunId']
            )
            print(
                "[Device Advisor] Debug: deviceAdvisor.get_suite_run respond:", file=sys.stderr)
            pp.pprint(test_result_responds)
            # If the status is PENDING or the responds does not loaded, the test suite is still loading
            if (test_result_responds['status'] == 'PENDING' or
                # test group has not been loaded
                len(test_result_responds['testResult']['groups']) == 0 or
                # test case has not been loaded
                len(test_result_responds['testResult']['groups'][0]['tests']) == 0 or
                    test_result_responds['testResult']['groups'][0]['tests'][0]['status'] == 'PENDING'):
                continue

            # Start to run the test sample after the status turns into RUNNING
            elif (not test_executed and test_result_responds['status'] == 'RUNNING' and
                  test_result_responds['testResult']['groups'][0]['tests'][0]['status'] == 'RUNNING'):
                print(
                    "[Device Advisor] Info: About to get start Device Advisor companion test application.", file=sys.stderr)
                working_dir = os.getcwd()
                exe_path = os.path.join(
                    "deviceadvisor/tests/", test_suite['test_exe_path'])
                os.chdir(exe_path)
                print("[Device Advisor] Debug: CWD: " +
                      os.getcwd(), file=sys.stderr)
                run_cmd = 'mvn clean compile exec:java -Dexec.mainClass=' + \
                    test_suite['test_exe_path'] + '.' + \
                    test_suite['test_exe_path']
                if 'cmd_args' in test_suite:
                    run_cmd = run_cmd + ' -Dexec.args="' + \
                        test_suite['cmd_args'] + '"'
                print("[Device Advisor] Debug: run_cmd:" +
                      run_cmd, file=sys.stderr)
                result = subprocess.run(run_cmd, shell=True, timeout=60*2)
                print("[Device Advisor] Debug: result: ",
                      result, file=sys.stderr)
                if result.returncode == 0:
                    # Once the SDK test completes successfully, we assume that Device Advisor service received
                    # and processed all requests.
                    test_executed = True
                os.chdir(working_dir)
            # If the test finalizing or store the test result
            elif (test_result_responds['status'] != 'RUNNING'):
                test_result[test_name] = test_result_responds['status']
                # If the test failed, upload the logs to S3 before clean up
                if (test_result[test_name] != "PASS"):
                    print(
                        "[Device Advisor] Info: About to upload log to S3.", file=sys.stderr)
                    log_url = test_result_responds['testResult']['groups'][0]['tests'][0]['logUrl']
                    group_string = re.search('group=(.*);', log_url)
                    log_group = group_string.group(1)
                    stream_string = re.search('stream=(.*)', log_url)
                    log_stream = stream_string.group(1)
                    process_logs(log_group, log_stream, thing_name)
                delete_thing_with_certi(
                    thing_name, certificate_id, certificate_arn)
                break

    except Exception as e:
        delete_thing_with_certi(thing_name, certificate_id, certificate_arn)
        print(f"[Device Advisor] Error: Failed to test: {test_name}; exception: {e}",
              file=sys.stderr)
        did_at_least_one_test_fail = True
        sleep_with_backoff(BACKOFF_BASE, BACKOFF_MAX)

##############################################
# print result and cleanup things
print(test_result, file=sys.stderr)
failed = False
for test in test_result:
    if (test_result[test] != "PASS" and test_result[test] != "PASS_WITH_WARNINGS"):
        print("[Device Advisor]Error: Test \"" + test +
              "\" Failed with status:" + test_result[test], file=sys.stderr)
        failed = True
if failed:
    # if the test failed, we dont clean the Thing so that we can track the error
    exit(-1)

if (did_at_least_one_test_fail == True):
    print("[Device Advisor] At least one test failed!", file=sys.stderr)
    exit(-1)

exit(0)
