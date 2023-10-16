# Device Advisor Tests

[Device Advisor Service](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor.html) is a cloud-based,
fully managed test capability for validating IoT devices. Device Advisor provides pre-built tests that help validating
the connectivity with AWS IoT Core.

## Description

A [special script](./script/DATestRun.py) configures and runs all Device Advisor tests. The tests configuration
is provided via a [configuration file](./script/DATestConfig.json).

### Configuration File Structure

Configuration file is in JSON format and it contains a list of test cases.

The following parameters are supported:

- `test_name` - required

  A unique name for the test.

- `test_suite_id` - required

  A unique ID for the test suite you created: it's the last part of Suite definition ARN.

- `test_exe_path` - required

  The Java class that executes the test suite.

- `cmd_args` - optional

  Additional command-line arguments that the executables might require.

- `disabled` - optional

  It's possible to temporarily disable a test suite by setting this parameter to true.

Example:

```
{
  "test_suites" :
  [
    {
      "test_name" : "Named Shadow Publish",
      "test_suite_id" : "abcd1234",
      "test_exe_path" : "ShadowUpdate",
      "cmd_args" : "--named-shadow",
      "disabled" : true
    }
  ]
}
```

It's possible to disable a test case by adding "disabled" parameter to it:

```
{
    "test_name" : "MQTT Connect",
    "test_suite_id" : "abcd1235",
    "test_exe_path" : "MQTTConnect",
    "disabled" : true
},
```

## Device Advisor Test Cases

Below are the test cases that this SDK uses.

### MQTT Connect

Validates that the device under test sends a CONNECT request.

This test executes
[Device send CONNECT to AWS IoT Core (Happy case)](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-mqtt.html#connect).

### MQTT Publish

Validates that the device under test publishes a message with QoS0.

This test executes
[QoS0 (Happy Case)](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-mqtt.html#publish).

### MQTT Subscribe

Validates that the device under test subscribes to MQTT topics.

This test executes
[Can Subscribe (Happy Case)](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-mqtt.html#subscribe).

### Shadow Publish

Validates if a device can publish its state after it connects to AWS IoT Core.

This test executes
[Device publishes state after it connects (Happy case)](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-shadow.html#publish)
for classic shadows.

### Shadow Update

Validates if your device reads all update messages received and synchronizes the device's state to match the desired
state properties.

This test executes
[Device updates reported state to desired state (Happy case)](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-shadow.html#update)
for classic shadows.

### Named Shadow Publish

Validates if a device can publish its state after it connects to AWS IoT Core.

This test executes
[Device publishes state after it connects (Happy case)](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-shadow.html#publish)
for named shadows.

### Named Shadow Update

Validates if your device reads all update messages received and synchronizes the device's state to match the desired
state properties.

This test executes
[Device updates reported state to desired state (Happy case)](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-shadow.html#update)
for named shadows.
