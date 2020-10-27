// The smithy IDL file describing the IPC APIs for Greengrass.
// API design doc - https://quip-amazon.com/mbN5ATEk6Db6/IPC-SDK-API-Design

namespace aws.greengrass

/// Provides communication between Greengrass core and customer component
service GreengrassCoreIPC {
    version: "2020-06-08",
    operations: [
        UpdateStatus,
        SubscribeToComponentUpdates,
        DeferComponentUpdate,
        GetConfiguration,
        UpdateConfiguration,
        SubscribeToConfigurationUpdate,
        SubscribeToValidateConfigurationUpdates,
        SendConfigurationValidityReport,
        SubscribeToTopic,
        PublishToTopic
    ]
}

//-----------Operations--------------------

/// Update status of this component
operation UpdateStatus {
    input:  UpdateStatusRequest,
    errors: [ServiceError]
}

/// Subscribe to receive notification if GGC is about to update any components
operation SubscribeToComponentUpdates {
    output: SubscribeToComponentUpdatesResponse,
    errors: [ServiceError]
}

/// Defer the update of components by a given amount of time and check again after that.
operation DeferComponentUpdate {
    input: DeferComponentUpdateRequest,
    errors: [ServiceError, ResourceNotFoundError]
}

/// Get value of a given key from the configuration
operation GetConfiguration {
    input: GetConfigurationRequest,
    output: GetConfigurationResponse,
    errors: [ServiceError, ResourceNotFoundError]
}

/// Update this component's configuration by replacing the value of given keyName with the newValue.
/// If an oldValue is specified then update will only take effect id the current value matches the given oldValue
operation UpdateConfiguration {
    input: UpdateConfigurationRequest,
    errors: [ServiceError, UnauthorizedError, ConflictError]
}

/// Subscribes to be notified when GGC updates the configuration for a given componentName and keyName.
operation SubscribeToConfigurationUpdate {
    input: SubscribeToConfigurationUpdateRequest,
    output: SubscribeToConfigurationUpdateResponse,
    errors: [ServiceError, ResourceNotFoundError]
}

/// Subscribes to be notified when GGC is about to update configuration for this component
/// GGC will wait for a timeout period before it proceeds with the update.
/// If the new configuration is not valid this component can use the SendConfigurationValidityReport
/// operation to indicate that
operation SubscribeToValidateConfigurationUpdates {
    output: SubscribeToValidateConfigurationUpdatesResponse,
    errors: [ServiceError]
}

/// This operation should be used in response to event received as part of SubscribeToValidateConfigurationUpdates
/// subscription. It is not necessary to send the report if the configuration is valid (GGC will wait for timeout
/// period and proceed). Sending the report with invalid config status will prevent GGC from applying the updates
operation SendConfigurationValidityReport {
    input: SendConfigurationValidityReportRequest,
    errors: [ServiceError]
}

/// Creates a subscription for a custom topic
operation SubscribeToTopic {
    input:  SubscribeToTopicRequest,
    output: SubscribeToTopicResponse,
    errors: [InvalidArgumentError, ServiceError, UnauthorizedError]
}

/// Publish to a custom topic.
operation PublishToTopic {
    input: PublishToTopicRequest,
    errors: [ServiceError, UnauthorizedError]
}

//-----------Shapes------------------------

structure UpdateStatusRequest {
    @required
    state: ReportState,
    serviceName: String
}

structure SubscribeToComponentUpdatesResponse {
    messages: ComponentUpdatePolicyEvents
}

@streaming
union ComponentUpdatePolicyEvents {
    preUpdateEvent: PreComponentUpdateEvent
}

structure PreComponentUpdateEvent {
    @required
    isGgcRestarting: Boolean
}

structure DeferComponentUpdateRequest {
    componentName: String,
    recheckAfterMs: Long
}

structure GetConfigurationRequest {
    componentName: String,
    @required
    keyName: String
}

structure GetConfigurationResponse {
    componentName: String,
    value: Document
}

structure UpdateConfigurationRequest {
    componentName: String,
    @required
    keyName: String,
    @required
    timestamp: Timestamp,
    @required
    newValue: Document,
    oldValue: Document
}

structure SubscribeToConfigurationUpdateRequest {
    componentName: String,
    @required
    keyName: String
}

structure SubscribeToConfigurationUpdateResponse {
    messages: ConfigurationUpdateEvents
}

@streaming
union ConfigurationUpdateEvents {
    configurationUpdateEvent: ConfigurationUpdateEvent
}

structure ConfigurationUpdateEvent {
    @required
    componentName: String,
    @required
    keyName: String
}

structure SubscribeToValidateConfigurationUpdatesResponse {
    messages: ValidateConfigurationUpdateEvents
}

union ValidateConfigurationUpdateEvents {
    validateConfigurationUpdateEvent: ValidateConfigurationUpdateEvent
}

structure ValidateConfigurationUpdateEvent {
    configuration: Document
}

structure SendConfigurationValidityReportRequest {
    @required
    status: ConfigurationValidityStatus,
    message: String
}

structure SubscribeToTopicRequest {
    @required
    topic: String,
    source: String
}

structure SubscribeToTopicResponse {
    topicName: String,
    messages: SubscriptionResponseMessage
}

@streaming
union SubscriptionResponseMessage{
    jsonMessage: JsonMessage,
    binaryMessage: BinaryMessage
}

structure PublishToTopicRequest {
    @required
    topic: String,
    @required
    publishMessage: PublishMessage
}

union PublishMessage{
    jsonMessage: JsonMessage,
    binaryMessage: BinaryMessage
}

structure JsonMessage {
    message: Document
}

structure BinaryMessage {
    message: Blob
}

//----------enums-----------------------

@enum([
    {
        value: "RUNNING",
        name: "RUNNING"
    },
    {
        value:"ERRORED",
        name:"ERRORED"
    }
])
string ReportState

@enum([
    {
        value: "ACCEPTED",
        name: "ACCEPTED"
    },
    {
        value: "REJECTED",
        name: "REJECTED"
    }
])
string ConfigurationValidityStatus

//----------errors----------------------

@error("client")
structure InvalidArgumentError {
    message: String
}

@error("client")
structure UnauthorizedError {
    message: String
}

@error("client")
structure ResourceNotFoundError {
    message: String,
    resourceType: String,
    resourceName: String
}

@error("client")
structure ConflictError {
    message: String
}

@error("server")
structure ServiceError {
    message: String
}


