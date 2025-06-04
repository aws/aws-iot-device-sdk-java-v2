# Commands Sandbox

[**Return to main sample list**](../README.md)

This is an interactive sample that allows you to use the AWS IoT [Commands](https://docs.aws.amazon.com/iot/latest/developerguide/iot-remote-command.html)
service to receive and process remote instructions.

In a real use case, control plane commands (the actions performed by aws-sdk-java-v2) would be issued by another applications
under control of the customer, while data plane operations (the actions performed by the IoT SDK Java v2) would be issued
by software running on the IoT device itself.

Using the IoT Commands service and this sample requires an understanding of two closely-related but different service terms:
* **AWS IoT Command** - metadata describing a task that the user would like one or more devices to run.
* **AWS IoT Command Execution** - metadata describing the state of a single device's attempt to execute an AWS IoT Command.

In particular, you can define an **AWS IoT Command** and then send it multiple times to the same device. The device will
try to execute each received **AWS IoT Command Execution**.

AWS IoT command service uses different MQTT topics for different payload formats. This allows a device to choose which
AWS IoT command payload formats to receive. AWS IoT Commands service distinguishes the following payload formats:
- JSON
- CBOR
- generic (i.e. everything else)

If your device wants to receive both JSON and CBOR payloads, it will need to subscribe to two topics using two separate
API calls. On the other hand, if your device needs to receive, for example, "plain/text" and "my-custom-format" payloads,
it has to subscribe to the generic MQTT topic and distinguish received IoT commands by the payload-type field.

> [!NOTE]
> In this sample, the term command is used with multiple different meanings. To keep them straight, we qualify the word
> with a prefix:
> - AWS IoT command - a description of a task defined in the AWS IoT Commands service.
> - AWS IoT command execution - an event with instructions sent from the IoT Core to a device.
> - sample command - an action that this sample application can perform, such as `open-thing-stream`.
>
> To avoid confusion, the `command` word will always be used with a context.

### Interaction with sample application

Once connected, the sample supports the following commands:

Control Plane
* `create-command` - 
* `delete-command` - 
* `send-command-to-thing` - 
* `send-command-to-client` - 
* `get-command-execution`- 

Data Plane
* `open-thing-stream <payload-format>` - subscribe to a stream of AWS IoT command executions with a specified payload format
  targeting the IoT Thing set on the application startup
* `open-client-stream <payload-format>` - subscribe to a stream of AWS IoT command executions with a specified payload format
  targeting the MQTT client ID set on the application startup
* `update-command-execution <execution-id> \<status> \[\<reason-code>] \[\<reason-description>]` - update status for specified
  execution ID;
    * status can be one of the following: IN_PROGRESS, SUCCEEDED, REJECTED, FAILED, TIMED_OUT
    * reason-code and reason-description may be optionally provided for the REJECTED, FAILED, or TIMED_OUT statuses

Miscellaneous
* `list-streams` - list all open streaming operations
* `close-stream <stream-id>` - close a specified stream; <stream-id> is internal ID that can be found with 'list-streams'
* `quit` - quit the sample application

### Prerequisites
Your IoT Core Thing's [Policy](https://docs.aws.amazon.com/iot/latest/developerguide/iot-policies.html) must provide privileges
for this sample to connect, subscribe, publish, and receive in order to perform its data plane operations. Below is a sample
policy that can be used on your IoT Core Thing that will allow this sample to run as intended.

<details>
<summary>Sample Policy</summary>
<pre>
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "iot:Publish",
      "Resource": [
        "arn:aws:iot:&lt;region&gt;:&lt;account&gt;:topic/$aws/commands/&lt;device_type&gt;/&lt;device_id&gt;/executions/*/response/json"
      ]
    },
    {
      "Effect": "Allow",
      "Action": "iot:Receive",
      "Resource": [
        "arn:aws:iot:&lt;region&gt;:&lt;account&gt;:topic/$aws/commands/&lt;device_type&gt;/&lt;device_id&gt;/executions/*/request/*",
        "arn:aws:iot:&lt;region&gt;:&lt;account&gt;:topic/$aws/commands/&lt;device_type&gt;/&lt;device_id&gt;/executions/*/response/accepted/json",
        "arn:aws:iot:&lt;region&gt;:&lt;account&gt;:topic/$aws/commands/&lt;device_type&gt;/&lt;device_id&gt;/executions/*/response/rejected/json"
      ]
    },
    {
      "Effect": "Allow",
      "Action": "iot:Subscribe",
      "Resource": [
        "arn:aws:iot:&lt;region&gt;:&lt;account&gt;:topicfilter/$aws/commands/&lt;device_type&gt;/&lt;device_id&gt;/executions/*/request/*",
        "arn:aws:iot:&lt;region&gt;:&lt;account&gt;:topicfilter/$aws/commands/&lt;device_type&gt;/&lt;device_id&gt;/executions/*/response/accepted/json",
        "arn:aws:iot:&lt;region&gt;:&lt;account&gt;:topicfilter/$aws/commands/&lt;device_type&gt;/&lt;device_id&gt;/executions/*/response/rejected/json"
      ]
    },
    {
      "Effect": "Allow",
      "Action": "iot:Connect",
      "Resource": "arn:aws:iot:&lt;region&gt;:&lt;account&gt;:client/test-*"
    }
  ]
}
</pre>

Replace with the following with the data from your AWS account:
* `<region>`: The AWS IoT Core region where you created your AWS IoT Core thing you wish to use with this sample. For example `us-east-1`.
* `<account>`: Your AWS IoT Core account ID. This is the set of numbers in the top right next to your AWS account name when using the AWS IoT Core website.
* `<device_type>`: Can be either `things` or `clients`.
* `<device_id>`: Depending on `<device_type>` value, this is either IoT Thing name or MQTT client ID.

Note that in a real application, you may want to avoid the use of wildcards in your ClientID or use them selectively.
Please follow best practices when working with AWS on production applications using the SDK. Also, for the purposes of
this sample, please make sure your policy allows a client ID of `test-*` to connect or use `--client_id <client ID here>`
to send the client ID your policy supports.

</details>

The AWS CLI triggered control plane operations in the walkthrough require AWS credentials with appropriate permissions
be sourceable. The following permissions must be granted:
<details>
<summary>Sample Policy</summary>
<pre>
{
    "Version": "2012-10-17",
    "Statement":
    {
        "Action": "iot:CreateCommand",
        "Effect": "Allow",
        "Resource": "arn:aws:iot:&lt;region&gt;:&lt;account&gt;:command/&lt;command_name&gt;"
    }
}
</pre>

Replace with the following with the data from your AWS account:
* `<region>`: The AWS IoT Core region where you created your AWS IoT Core thing you wish to use with this sample.
  For example `us-east-1`.
* `<account>`: Your AWS IoT Core account ID. This is the set of numbers in the top right next to your AWS account name
  when using the AWS IoT Core website.
* `<command_name>`: The unique identifier for your AWS IoT command, such as `LockDoor`. If you want to use more than
  one command, you can use `*` or specify multiple commands under the Resource section in the IAM policy.

</details>

## Running the Sample

To run the sample, use the following Shell command:

```shell
mvn compile exec:java -pl samples/CommandsSandbox -Dexec.mainClass=commands.CommandsSandbox \
    -Dexec.args="--endpoint <endpoint> --cert <path to certificate> --key <path to private key> --thing <thing-name> --client-id <mqtt-client-id>"
```

If an AWS IoT Thing resource with the given name does not exist, the sample will first create it.  Once the thing exists,
the sample connects via MQTT, and you can issue commands to the Command service and inspect the results.

## Walkthrough

### Creating AWS IoT Commands

We'll start with the creation of a couple of AWS IoT commands. When creating the AWS IoT command, you must provide a payload.
The payload that you provide is base64 encoded.

The first AWS IoT command contains JSON payload:
```
create-command sample-json-command application/json { "message": "Hello IoT" }
```

The second AWS IoT command will be a plain text: 
```
create-command sample-text-command plain/text hello
```

You can examine the newly created AWS IoT commands in AWS Console or using the following AWS CLI command:

```shell
aws iot list-commands --namespace "AWS-IoT"
```

yields output like the following:
```json
{
    "commands": [
        {
            "commandArn": "arn:aws:iot:...:command/sample-json-command",
            "commandId": "sample-json-command",
            "displayName": "sample-json-command",
            "deprecated": false,
            "createdAt": "...",
            "lastUpdatedAt": "...",
            "pendingDeletion": false
        },
        {
            "commandArn": "arn:aws:iot:...:command/sample-text-command",
            "commandId": "sample-text-command",
            "displayName": "sample-text-command",
            "deprecated": false,
            "createdAt": "...",
            "lastUpdatedAt": "...",
            "pendingDeletion": false
        }
    ]
}
```

### Subscribing to AWS IoT Command Executions

Now, let's subscribe to AWS IoT commands with JSON payloads using the following sample command:
```
open-thing-stream application/json
```

Let's open another stream, this time for generic payloads and MQTT client:
```
open-client-stream generic
```

To examine the open streaming operations use the `list-streams` sample command:
```
list-streams
```

and you will see something like this
```
Streams:
  1: device type 'things', device ID 'MyIotThing', payload type 'application/json'
  2: device type 'clients', device ID 'MyIotThing', payload type 'generic'
```

You can close a streaming operation using the `close-stream` sample command:
```
close-stream <stream-id>
```
, where `<stream-id>` is a sequence number of the operation. For our example, JSON operation has stream ID 1 and generic
operation has stream ID 2.

For example, to close `generic` stream, execute this sample command:
```
close-stream 2
```

### Sending AWS IoT Command Executions

AWS IoT command just defines a set of instructions. It cannot target any device. For sending AWS IoT command to a device,
you need to create AWS IoT command execution.

This can be done with the following sample commands:

```
send-command-to-thing sample-json-command
```
amd
```
send-command-to-client sample-text-command
```

The sample should receive these newly created AWS IoT command executions and output something similar to:

```
Received new command execution
  execution ID: 11111111-1111-1111-1111-111111111111
  payload format: application/json
  execution timeout: 9
  payload size: 26
  JSON payload: '{ "message": "Hello IoT" }'
```
and, unless you closed the corresponding stream on the previous step,
```
Received new command execution
  execution ID: 22222222-2222-2222-2222-222222222222
  payload format: plain/text
  execution timeout: 9
  payload size: 5
```

> [!IMPORTANT]
> IoT Java SDK v2 does not parse the payload of the incoming AWS IoT commands. User code gets a structure containing byte
> buffer for payload and additionally payload format if it was specified for the AWS IoT command. User code is supposed
> to parse payload itself.

Your device has only 9-10 seconds to report back the execution status, which is not enough for an interactive application.
The AWS IoT command execution you sent will probably time out before you manage to send the status update.

You can check the AWS IoT command execution status using the following sample command (remember to change execution ID to the one you actually received):

```
get-command-execution 11111111-1111-1111-1111-111111111111
```

will yield something like this:

```
Status of command execution '11111111-1111-1111-1111-111111111111' is TIMED_OUT
```

Let's send another AWS IoT command execution, this time with a timeout more suitable for our sample. Notice that we use
the same AWS IoT command, the only thing that changed is the execution timeout value:

```
send-command-to-thing sample-json-command 300
```

The running sample will receive another notification, with the new execution ID:
```
Received new command execution
  execution ID: 33333333-3333-3333-3333-333333333333
  payload format: application/json
  execution timeout: 299
  payload size: 26
  JSON payload: '{ "message": "Hello IoT" }'
```

Let's proceed to the next section where we're going to update the status of an AWS IoT command execution.

### Updating and monitoring AWS IoT command execution status

The sample didn't yet update the status of the AWS IoT command execution, so the following AWS CLI command

```
get-command-execution 33333333-3333-3333-3333-333333333333
```

should return `CREATED` status:

```
Status of command execution '33333333-3333-3333-3333-333333333333' is CREATED
```

To update the status of a received AWS IoT command execution, we should use the `update-command-execution` sample command.
Take an AWS IoT command execution ID your sample received at the end of the previous section and pass it to
`update-command-execution` along with the `IN_PROGRESS` status:
```
update-command-execution 33333333-3333-3333-3333-333333333333 IN_PROGRESS
```

Then this sample command
```
get-command-execution 33333333-3333-3333-3333-333333333333
```

should return

```
Status of Command execution '33333333-3333-3333-3333-333333333333' is IN_PROGRESS
```

`IN_PROGRESS` is an intermediary execution status, i.e. it's possible to change this status.
`SUCCEEDED`, `FAILED`, and `REJECTED` statuses are terminal - when you set the AWS IoT command execution status to one
of them, it's final.

There is also the `TIMED_OUT` status. Though it's supposed to be set by the service side when there is no response from
the device in `timeout` time, your application may provide additional info by setting the `statusReason` field in the update
event.

Let's set the AWS IoT command execution status to one of the terminal states with sample command:
```
update-command-execution 33333333-3333-3333-3333-333333333333 SUCCEEDED
```
or
```
update-command-execution 33333333-3333-3333-3333-333333333333 FAILED SHORT_FAILURE_CODE A longer description
```

If you try to update the status of the same AWS IoT command execution to something else, it'll fail:
```
update-command-execution <execution-id> REJECTED
```

will yield
```
update-command-execution ExecutionException!
  update-command-execution source exception: Request-response operation failure
  update-command-execution Modeled error: {"error":"TERMINAL_STATE_REACHED","errorMessage":"Command Execution status cannot be updated to REJECTED since execution has already completed with status SUCCEEDED.","executionId":"33333333-3333-3333-3333-333333333333"}
```

### Cleaning up

When all executions for a given AWS IoT command have reached a terminal state (`SUCCEEDED`, `FAILED`, `REJECTED`), you
can delete the AWS IoT command itself with the following sample command:

```
delete-command sample-json-command
```

### Misc Topics

### What happens if I open the same stream twice?

The Java AWS IoT Commands client **does** allow you to subscribe multiple times to the same stream of events. You can even
do this using this sample, just execute the same opening stream sample command few times. The client will receive event
for each opened subscription.

A real-world application may prevent such situations by tracking which streams are open. The uniqueness of the AWS IoT
command executions stream is determined by `device type`, `device ID`, and `payload format`. Most probably, `device type`
and `device ID` will be constant, so the application needs to check `payload format`. Notice that Aws IoT Commands service
distinguishes only JSON and CBOR, all other payload formats will be routed to the generic stream.

#### What is the proper generic architecture for a command-processing application running on a device?

1. On startup, create and open streaming operations for the necessary AWS IoT command events using
   `IotCommandsV2Client.createCommandExecutionsJsonPayloadStream`, `IotCommandsV2Client.CreateCommandExecutionsCborPayloadStream`,
   and/or `IotCommandsV2Client.CreateCommandExecutionsGenericPayloadStream` functions.
2. **DO NOT** process received AWS IoT commands right in the callback passed to `CreateCommandExecutions*PayloadStream`.
   As a general rule, **DO NOT** perform any time-consuming or blocking operations in the callback. One of possible
   approaches is to put incoming IoT commands into a shared queue. Then the designated executor(s) should process them in
   separate thread(s).
3. If your application is expected to receive a lot of AWS IoT commands, monitor the number of them enqueued for processing.
   Consider introducing priorities based on AWS IoT command timeouts or another internal value.
