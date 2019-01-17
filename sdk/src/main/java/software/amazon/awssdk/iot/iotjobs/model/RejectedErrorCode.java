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

public enum RejectedErrorCode {
    UNKNOWN_ENUM_VALUE("UNKNOWN_ENUM_VALUE"),
    INVALID_TOPIC("InvalidTopic"),
    INVALID_STATE_TRANSITION("InvalidStateTransition"),
    RESOURCE_NOT_FOUND("ResourceNotFound"),
    INVALID_REQUEST("InvalidRequest"),
    REQUEST_THROTTLED("RequestThrottled"),
    INTERNAL_ERROR("InternalError"),
    TERMINAL_STATE_REACHED("TerminalStateReached"),
    INVALID_JSON("InvalidJson"),
    VERSION_MISMATCH("VersionMismatch");

    private String value;

    private RejectedErrorCode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    static RejectedErrorCode fromString(String val) {
        for (RejectedErrorCode e : RejectedErrorCode.class.getEnumConstants()) {
            if (e.toString() == val) {
                return e;
            }
        }
        return UNKNOWN_ENUM_VALUE;
    }
}
