# Sample apps for the AWS IoT Device SDK for Java v2

* [Shadow](#shadow)
* [Jobs](#jobs)
* [fleet provisioning](#fleet-provisioning)

**Additional sample apps not described below:**
* [BasicPubSub](https://github.com/aws/aws-iot-device-sdk-java-v2/tree/main/samples/BasicPubSub)
* [Greengrass](https://github.com/aws/aws-iot-device-sdk-java-v2/tree/main/samples/Greengrass)
* [PubSubStress](https://github.com/aws/aws-iot-device-sdk-java-v2/tree/main/samples/PubSubStress)
* [RawPubSub](https://github.com/aws/aws-iot-device-sdk-java-v2/tree/main/samples/RawPubSub)

## Shadow

This sample uses the AWS IoT
[Device Shadow](https://docs.aws.amazon.com/iot/latest/developerguide/iot-device-shadows.html)
Service to keep a property in
sync between device and server. Imagine a light whose color may be changed
through an app, or set by a local user.

Once connected, type a value in the terminal and press Enter to update
the property's "reported" value. The sample also responds when the "desired"
value changes on the server. To observe this, edit the Shadow document in
the AWS Console and set a new "desired" value.

On startup, the sample requests the shadow document to learn the property's
initial state. The sample also subscribes to "delta" events from the server,
which are sent when a property's "desired" value differs from its "reported"
value. When the sample learns of a new desired value, that value is changed
on the device and an update is sent to the server with the new "reported"
value.

Source: `samples/Shadow`

To Run:

``` sh
> mvn exec:java -pl samples/Shadow -Dexec.mainClass=shadow.ShadowSample -Dexec.args='--endpoint <endpoint> --rootca /path/to/AmazonRootCA1.pem --cert <cert path> --key <key path> --thingName <thing name>'
```

Your Thing's
[Policy](https://docs.aws.amazon.com/iot/latest/developerguide/iot-policies.html)
must provide privileges for this sample to connect, subscribe, publish,
and receive.

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
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/update"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Receive"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/get/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/get/rejected",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/update/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/update/rejected",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/shadow/update/delta"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Subscribe"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/get/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/get/rejected",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/update/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/update/rejected",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/shadow/update/delta"
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
</details>

## Jobs

This sample uses the AWS IoT
[Jobs](https://docs.aws.amazon.com/iot/latest/developerguide/iot-jobs.html)
Service to describe jobs to execute.

This sample requires you to create jobs for your device to execute. See
[instructions here](https://docs.aws.amazon.com/iot/latest/developerguide/create-manage-jobs.html).

On startup, the sample describes a job that is pending execution.

Source: `samples/Jobs`

To Run:

``` sh
> mvn exec:java -pl samples/Jobs -Dexec.mainClass=jobs.JobsSample -Dexec.args='--endpoint <endpoint> --rootca /path/to/AmazonRootCA1.pem --cert <cert path> --key <key path> --thingName <thing name>'
```

Your Thing's
[Policy](https://docs.aws.amazon.com/iot/latest/developerguide/iot-policies.html)
must provide privileges for this sample to connect, subscribe, publish,
and receive.

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
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/start-next",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/*/update"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Receive"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/notify-next",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/start-next/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/start-next/rejected",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/*/update/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/things/<b>thingname</b>/jobs/*/update/rejected"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Subscribe"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/jobs/notify-next",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/jobs/start-next/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/jobs/start-next/rejected",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/jobs/*/update/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/$aws/things/<b>thingname</b>/jobs/*/update/rejected"
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
</details>

## fleet provisioning

This sample uses the AWS IoT
[Fleet provisioning](https://docs.aws.amazon.com/iot/latest/developerguide/provision-wo-cert.html)
to provision devices using either a CSR or KeysAndcertificate and subsequently calls RegisterThing.

On startup, the script subscribes to topics based on the request type of either CSR or Keys topics,
publishes the request to corresponding topic and calls RegisterThing.

Source: `samples/Identity`

cd ~/samples/Identity

Run the sample using CreateKeysAndCertificate:

``` sh
mvn exec:java -Dexec.mainClass="identity.FleetProvisioningSample" -Dexec.args="--endpoint <endpoint> --rootca <root ca path>
--cert <cert path> --key <private key path> --templateName <templatename> --templateParameters <templateParams>"
```

Run the sample using CreateCertificateFromCsr:

``` sh
mvn exec:java -Dexec.mainClass="identity.FleetProvisioningSample" -Dexec.args="--endpoint <endpoint> --rootca <root ca path>
--cert <cert path> --key <private key path> --templateName <templatename> --templateParameters <templateParams> --csr <csr path>"
```

Your Thing's
[Policy](https://docs.aws.amazon.com/iot/latest/developerguide/iot-policies.html)
must provide privileges for this sample to connect, subscribe, publish,
and receive.

<details>
<summary>(see sample policy)</summary>
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
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/certificates/create/json",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/certificates/create-from-csr/json",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/provisioning-templates/<b>templatename</b>/provision/json"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Receive",
        "iot:Subscribe"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/certificates/create/json/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/certificates/create/json/rejected",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/certificates/create-from-csr/json/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/certificates/create-from-csr/json/rejected",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/provisioning-templates/<b>templatename</b>/provision/json/accepted",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/$aws/provisioning-templates/<b>templatename</b>/provision/json/rejected"
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
</details>
