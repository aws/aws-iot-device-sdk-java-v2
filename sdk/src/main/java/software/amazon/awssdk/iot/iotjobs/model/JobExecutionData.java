/* Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License").
* You may not use this file except in compliance with the License.
* A copy of the License is located at
*
*  http://aws.amazon.com/apache2.0
*
* or in the "license" file accompanying this file. This file is distributed
* on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
* express or implied. See the License for the specific language governing
* permissions and limitations under the License.

* This file is generated
*/


package software.amazon.awssdk.iot.iotjobs.model;

import java.util.HashMap;
import java.util.Optional;
import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotjobs.model.JobStatus;

public class JobExecutionData {
    public Optional<String> jobId;
    public Optional<String> thingName;
    public Optional<HashMap<String, Object>> jobDocument;
    public Optional<Long> executionNumber;
    public Optional<HashMap<String, String>> statusDetails;
    public Optional<JobStatus> status;
    public Optional<Integer> versionNumber;
    public Optional<Timestamp> queuedAt;
    public Optional<Timestamp> lastUpdatedAt;
    public Optional<Timestamp> startedAt;
}
