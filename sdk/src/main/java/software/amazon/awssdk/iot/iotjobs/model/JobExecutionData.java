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
import software.amazon.awssdk.iot.Timestamp;
import software.amazon.awssdk.iot.iotjobs.model.JobStatus;

public class JobExecutionData {
    public String jobId;
    public String thingName;
    public HashMap<String, Object> jobDocument;
    public Long executionNumber;
    public HashMap<String, String> statusDetails;
    public JobStatus status;
    public Integer versionNumber;
    public Timestamp queuedAt;
    public Timestamp lastUpdatedAt;
    public Timestamp startedAt;
}
