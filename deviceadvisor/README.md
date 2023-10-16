# Device Advisor Tests

[Device Advisor Service](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor.html) is a cloud-based,
fully managed test capability for validating IoT devices. Device Advisor provides pre-built tests that help validating
the connectivity with AWS IoT Core.

## Device Advisor Test Cases

Below are the test cases that this SDK uses.

### MQTT Connect

Validates that the device under test sends a CONNECT request.

This test executes the "Device send CONNECT to AWS IoT Core (Happy case)" case described
[here](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-mqtt.html#connect).

### MQTT Publish

Validates that the device under test publishes a message with QoS0.

This test executes the "QoS0 (Happy Case)" case described
[here](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-mqtt.html#publish).

### MQTT Subscribe

Validates that the device under test subscribes to MQTT topics.

This test executes the "Can Subscribe (Happy Case)" case described
[here](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-mqtt.html#subscribe).

### Shadow Publish

Validates if a device can publish its state after it connects to AWS IoT Core.

This test executes the "Device publishes state after it connects (Happy case)" case for classic shadows described
[here](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-shadow.html#publish).

### Shadow Update

This test executes the "Device updates reported state to desired state (Happy case)" case for classic shadows described
[here](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-shadow.html#update).

### Named Shadow Publish

Validates if a device can publish its state after it connects to AWS IoT Core.

This test executes the "Device publishes state after it connects (Happy case)" case for classic shadows described
[here](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-shadow.html#publish).

### Named Shadow Update

This test executes the "Device updates reported state to desired state (Happy case)" case for named shadows described
[here](https://docs.aws.amazon.com/iot/latest/developerguide/device-advisor-tests-shadow.html#update).
