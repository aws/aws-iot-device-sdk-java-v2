# ShadowSandbox

[**Return to main sample list**](../README.md)

This is an interactive sample that supports a set of commands that allow you to interact with "classic" (unnamed) shadows of the AWS IoT [Device Shadow](https://docs.aws.amazon.com/iot/latest/developerguide/iot-device-shadows.html) Service.

### Commands
Once connected, the sample supports the following shadow-related commands:

* `get` - gets the current full state of the classic (unnamed) shadow.  This includes both a "desired" state component and a "reported" state component.
* `delete` - deletes the classic (unnamed) shadow completely
* `update-desired <desired-state-json-document>` - applies an update to the classic shadow's desired state component.  Properties in the JSON document set to non-null will be set to new values.  Properties in the JSON document set to null will be removed.
* `update-reported <reported-state-json-document>` - applies an update to the classic shadow's reported state component.  Properties in the JSON document set to non-null will be set to new values.  Properties in the JSON document set to null will be removed.

Two additional commands are supported:
* `help` - prints the set of supported commands
* `quit` - quits the sample application

### Prerequisites
Your IoT Core Thing's [Policy](https://docs.aws.amazon.com/iot/latest/developerguide/iot-policies.html) must provide privileges for this sample to connect, subscribe, publish, and receive. Below is a sample policy that can be used on your IoT Core Thing that will allow this sample to run as intended.

<details>
<summary>Sample Policy</summary>
<pre>
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "iot:Publish"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/get",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/delete",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/update"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Receive"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/get/*",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/delete/*",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/update/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Subscribe"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/get/*",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/delete/*",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/update/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": "iot:Connect",
      "Resource": "arn:aws:iot:<b>region</b>:<b>account</b>:client/test-*"
    }
  ]
}
</pre>

Replace with the following with the data from your AWS account:
* `<region>`: The AWS IoT Core region where you created your AWS IoT Core thing you wish to use with this sample. For example `us-east-1`.
* `<account>`: Your AWS IoT Core account ID. This is the set of numbers in the top right next to your AWS account name when using the AWS IoT Core website.
* `<thingname>`: The name of your AWS IoT Core thing you want the device connection to be associated with

Note that in a real application, you may want to avoid the use of wildcards in your ClientID or use them selectively. Please follow best practices when working with AWS on production applications using the SDK. Also, for the purposes of this sample, please make sure your policy allows a client ID of `test-*` to connect or use `--client_id <client ID here>` to send the client ID your policy supports.

</details>

## Walkthrough

To run the Shadow sample use the following command:

``` sh
mvn compile exec:java -pl samples/ServiceClients/ShadowSandbox -Dexec.mainClass=shadow.ShadowSandbox -Dexec.args="--endpoint <endpoint> --cert <path to certificate> --key <path to private key> --thing <thing name>"
```

The sample also listens to a pair of event streams related to the classic (unnamed) shadow state of your thing, so in addition to responses, you will occasionally see output from these streaming operations as they receive events from the shadow service.

Once successfully connected, you can issue commands.

### Initialization

Start off by getting the shadow state:

```
get
```

If your thing does have shadow state, you will get its current value, which this sample has no control over.  

If your thing does not have any shadow state, you'll get a ResourceNotFound error:

```
Get ExecutionException!
  Get source exception: Request-response operation failure
  Get Modeled error: {"clientToken":"<Some UUID>","code":404,"message":"No shadow exists with name: '<YourThingName>'"}
```

To create a shadow, you can issue an update call that will initialize the shadow to a starting state:

```
update-reported {"Color":"green"}
```

which will yield output similar to:

```
UpdateShadowResponse: 
  {"clientToken":"c3bae0fb-5f5c-46d3-ab6e-ef276ce2e6af","state":{"reported":{"Color":"green"}},"metadata":{"reported":{"Color":{"timestamp":1.736882722E9}}},"timestamp":1736882722,"version":1}
ShadowUpdated event: 
  {"current":{"state":{"reported":{"Color":"green"}},"metadata":{"reported":{"Color":{"timestamp":1.736882722E9}}},"version":1},"timestamp":1736882722}
```

Notice that in addition to receiving a response to the update request, you also receive a `ShadowUpdated` event containing what changed about
the shadow plus additional metadata (version, update timestamps, etc...).  Every time a shadow is updated, this
event is triggered.  If you wish to listen and react to this event, use the `createShadowUpdatedStream` API in the shadow client to create a
streaming operation that converts the raw MQTT publish messages into modeled data that the streaming operation emits.

Issue one more update to get the shadow's reported and desired states in sync:

```
update-desired {"Color":"green"}
```

yielding output similar to:

```
UpdateShadowResponse: 
  {"clientToken":"a7e0454b-3bdf-4f01-bae3-17fb1ec3c094","state":{"desired":{"Color":"green"}},"metadata":{"desired":{"Color":{"timestamp":1.736882875E9}}},"timestamp":1736882875,"version":2}
<ShadowUpdated event omitted>
```

### Changing Properties
A device shadow contains two independent states: reported and desired.  "Reported" represents the device's last-known local state, while
"desired" represents the state that control application(s) would like the device to change to.  In general, each application (whether on the device or running
remotely as a control process) will only update one of these two state components.

Let's walk through the multi-step process to coordinate a change-of-state on the device.  First, a control application needs to update the shadow's desired
state with the change it would like applied:

```
update-desired {"Color":"red"}
```

For our sample, this yields output similar to:

```
ShadowUpdated event: 
  {"previous":{"state":{"desired":{"Color":"green"},"reported":{"Color":"green"}},"metadata":{"desired":{"Color":{"timestamp":1.736882875E9}},"reported":{"Color":{"timestamp":1.736882722E9}}},"version":2},"current":{"state":{"desired":{"Color":"red"},"reported":{"Color":"green"}},"metadata":{"desired":{"Color":{"timestamp":1.736882961E9}},"reported":{"Color":{"timestamp":1.736882722E9}}},"version":3},"timestamp":1736882961}
ShadowDeltaUpdated event: 
  {"state":{"Color":"red"},"metadata":{"Color":{"timestamp":1.736882961E9}},"timestamp":1736882961,"version":3,"clientToken":"c2447b9b-3601-4150-b113-320c7d93da6d"}
UpdateShadowResponse: 
  {"clientToken":"c2447b9b-3601-4150-b113-320c7d93da6d","state":{"desired":{"Color":"red"}},"metadata":{"desired":{"Color":{"timestamp":1.736882961E9}}},"timestamp":1736882961,"version":3}
```

The key thing to notice here is that in addition to the update response (which only the control application would see) and the ShadowUpdated event,
there is a new event, ShadowDeltaUpdated, which indicates properties on the shadow that are out-of-sync between desired and reported.  All out-of-sync
properties will be included in this event, including properties that became out-of-sync due to a previous update.

Like the ShadowUpdated event, ShadowDeltaUpdated events can be listened to by creating and configuring a streaming operation, this time by using
the createShadowDeltaUpdatedStream API.  Using the ShadowDeltaUpdated events (rather than ShadowUpdated) lets a device focus on just what has
changed without having to do complex JSON diffs on the full shadow state itself.

Assuming that the change expressed in the desired state is reasonable, the device should apply it internally and then let the service know it
has done so by updating the reported state of the shadow:

```
update-reported {"Color":"red"}
```

yielding

```
UpdateShadowResponse: 
  {"clientToken":"5209d058-261b-471a-8859-d682e795798d","state":{"reported":{"Color":"red"}},"metadata":{"reported":{"Color":{"timestamp":1.736883022E9}}},"timestamp":1736883022,"version":4}
ShadowUpdated event: 
  {"previous":{"state":{"desired":{"Color":"red"},"reported":{"Color":"green"}},"metadata":{"desired":{"Color":{"timestamp":1.736882961E9}},"reported":{"Color":{"timestamp":1.736882722E9}}},"version":3},"current":{"state":{"desired":{"Color":"red"},"reported":{"Color":"red"}},"metadata":{"desired":{"Color":{"timestamp":1.736882961E9}},"reported":{"Color":{"timestamp":1.736883022E9}}},"version":4},"timestamp":1736883022}
```

Notice that no ShadowDeltaUpdated event is generated because the reported and desired states are now back in sync.

### Multiple Properties
Not all shadow properties represent device configuration.  To illustrate several more aspects of the Shadow service, let's add a second property to our shadow document,
starting out in sync (output omitted):

```
update-reported {"Status":"Great"}
```

```
update-desired {"Status":"Great"}
```

Notice that shadow updates work by deltas rather than by complete state changes.  Updating the "Status" property to a value had no effect on the shadow's
"Color" property:

```
get
```

yields

```
GetShadowResponse: 
  {"clientToken":"bcefd4e7-f9ac-48b3-8542-aa2fce3d044d","state":{"desired":{"Status":"Great","Color":"red"},"reported":{"Status":"Great","Color":"red"}},"metadata":{"desired":{"Status":{"timestamp":1.736885497E9},"Color":{"timestamp":1.736882961E9}},"reported":{"Status":{"timestamp":1.736885487E9},"Color":{"timestamp":1.736883022E9}}},"timestamp":1736885515,"version":6}
```

Suppose something goes wrong with the device and its status is no longer "Great"

```
update-reported {"Status":"Awful"}
```

which yields output similar to:

```
UpdateShadowResponse: 
  {"clientToken":"55c67835-67c9-412a-a943-1e2052d8c76f","state":{"reported":{"Status":"Awful"}},"metadata":{"reported":{"Status":{"timestamp":1.736885551E9}}},"timestamp":1736885551,"version":7}
ShadowDeltaUpdated event: 
  {"state":{"Status":"Great"},"metadata":{"Status":{"timestamp":1.736885497E9}},"timestamp":1736885551,"version":7,"clientToken":"55c67835-67c9-412a-a943-1e2052d8c76f"}
ShadowUpdated event: 
  {"previous":{"state":{"desired":{"Status":"Great","Color":"red"},"reported":{"Status":"Great","Color":"red"}},"metadata":{"desired":{"Status":{"timestamp":1.736885497E9},"Color":{"timestamp":1.736882961E9}},"reported":{"Status":{"timestamp":1.736885487E9},"Color":{"timestamp":1.736883022E9}}},"version":6},"current":{"state":{"desired":{"Status":"Great","Color":"red"},"reported":{"Status":"Awful","Color":"red"}},"metadata":{"desired":{"Status":{"timestamp":1.736885497E9},"Color":{"timestamp":1.736882961E9}},"reported":{"Status":{"timestamp":1.736885551E9},"Color":{"timestamp":1.736883022E9}}},"version":7},"timestamp":1736885551}
```

Similar to how updates are delta-based, notice how the ShadowDeltaUpdated event only includes the "Status" property, leaving the "Color" property out because it
is still in sync between desired and reported.

### Removing properties
Properties can be removed from a shadow by setting them to null.  Removing a property completely would require its removal from both the
reported and desired states of the shadow (output omitted):

```
update-reported {"Status":null}
```

```
update-desired {"Status":null}
```

If you now get the shadow state:

```
get
```

its output yields something like

```
GetShadowResponse: 
  {"clientToken":"02c11e3d-5e5f-47bf-a5a6-9bc584defeed","state":{"desired":{"Color":"Red"},"reported":{"Color":"Red"}},"metadata":{"desired":{"Color":{"timestamp":1.736880637E9}},"reported":{"Color":{"timestamp":1.736880651E9}}},"timestamp":1736888076,"version":17}
```

The Status property has been fully removed from the shadow state.

### Removing a shadow
To remove a shadow, you must invoke the DeleteShadow API (setting the reported and desired
states to null will only clear the states, but not delete the shadow resource itself).

```
delete
```

yields something like

```
DeleteShadowResponse: 
  {"clientToken":"ec7e0fd2-0ef0-4215-bead-693a3a37f0f1","timestamp":1736888506,"version":17}
```

## ⚠️ Usage disclaimer

These code examples interact with services that may incur charges to your AWS account. For more information, see [AWS Pricing](https://aws.amazon.com/pricing/).

Additionally, example code might theoretically modify or delete existing AWS resources. As a matter of due diligence, do the following:

- Be aware of the resources that these examples create or delete.
- Be aware of the costs that might be charged to your account as a result.
- Back up your important data.
