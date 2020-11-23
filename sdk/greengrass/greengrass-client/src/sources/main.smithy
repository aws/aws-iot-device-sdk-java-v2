// The smithy IDL file describing the IPC APIs for Greengrass.
// API design doc - https://quip-amazon.com/mbN5ATEk6Db6/IPC-SDK-API-Design

namespace aws.greengrass

/// Provides communication between Greengrass core and customer component
service GreengrassCoreIPC {
    version: "2020-06-08",
    operations: [
        UpdateState,
        SubscribeToComponentUpdates,
        DeferComponentUpdate,
        GetConfiguration,
        UpdateConfiguration,
        SubscribeToConfigurationUpdate,
        SubscribeToValidateConfigurationUpdates,
        SendConfigurationValidityReport,
        SubscribeToTopic,
        PublishToTopic,
        GetComponentDetails,
        RestartComponent,
        StopComponent,
        UpdateRecipesAndArtifacts,
        CreateLocalDeployment,
        GetLocalDeploymentStatus,
        ListLocalDeployments,
        ListComponents,
        PublishToIoTCore,
        SubscribeToIoTCore,
        ValidateAuthorizationToken,
        GetSecretValue,
        CreateDebugPassword
    ]
}

//-----------Operations--------------------

/// Update status of this component
operation UpdateState {
    input:  UpdateStateRequest,
    output:  UpdateStateResponse,
    errors: [ServiceError, ResourceNotFoundError]
}

/// Subscribe to receive notification if GGC is about to update any components
operation SubscribeToComponentUpdates {
    input: SubscribeToComponentUpdatesRequest,
    output: SubscribeToComponentUpdatesResponse,
    errors: [ServiceError, ResourceNotFoundError]
}

/// Defer the update of components by a given amount of time and check again after that.
operation DeferComponentUpdate {
    input: DeferComponentUpdateRequest,
    output: DeferComponentUpdateResponse,
    errors: [ServiceError, ResourceNotFoundError, InvalidArgumentsError]
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
    output: UpdateConfigurationResponse,
    errors: [ServiceError, UnauthorizedError, ConflictError, FailedUpdateConditionCheckError, InvalidArgumentsError]
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
    input: SubscribeToValidateConfigurationUpdatesRequest,
    output: SubscribeToValidateConfigurationUpdatesResponse,
    errors: [ServiceError]
}

/// This operation should be used in response to event received as part of SubscribeToValidateConfigurationUpdates
/// subscription. It is not necessary to send the report if the configuration is valid (GGC will wait for timeout
/// period and proceed). Sending the report with invalid config status will prevent GGC from applying the updates
operation SendConfigurationValidityReport {
    input: SendConfigurationValidityReportRequest,
    output: SendConfigurationValidityReportResponse,
    errors: [InvalidArgumentsError, ServiceError]
}

/// Creates a subscription for a custom topic
operation SubscribeToTopic {
    input:  SubscribeToTopicRequest,
    output: SubscribeToTopicResponse,
    errors: [InvalidArgumentsError, ServiceError, UnauthorizedError]
}

/// Publish to a custom topic.
operation PublishToTopic {
    input: PublishToTopicRequest,
    output: PublishToTopicResponse,
    errors: [ServiceError, UnauthorizedError]
}

/// Gets the status and version of the component with the given component name
operation GetComponentDetails {
    input: GetComponentDetailsRequest,
    output: GetComponentDetailsResponse,
    errors: [ServiceError, ResourceNotFoundError, InvalidArgumentsError]
}

/// Restarts a component with the given name
operation RestartComponent {
    input: RestartComponentRequest,
    output: RestartComponentResponse,
    errors: [ServiceError, ComponentNotFoundError, InvalidArgumentsError]
}

/// Stops a component with the given name
operation StopComponent {
    input: StopComponentRequest,
    output: StopComponentResponse,
    errors: [ServiceError, ComponentNotFoundError, InvalidArgumentsError]
}

/// Add/Update new recipes, artifacts for new/existing components.
operation UpdateRecipesAndArtifacts {
    input: UpdateRecipesAndArtifactsRequest,
    output: UpdateRecipesAndArtifactsResponse,
    errors: [ServiceError, InvalidRecipeDirectoryPathError,
             InvalidArtifactsDirectoryPathError, InvalidArgumentsError]
}

/// Creates a local deployment on the device.  Also allows to remove existing components.
operation CreateLocalDeployment {
    input: CreateLocalDeploymentRequest,
    output: CreateLocalDeploymentResponse,
    errors: [ServiceError]
}

/// Get status of a local deployment with the given deploymentId
operation GetLocalDeploymentStatus {
    input: GetLocalDeploymentStatusRequest,
    output: GetLocalDeploymentStatusResponse,
    errors: [ServiceError, ResourceNotFoundError]
}

/// Lists the last 5 local deployments along with their statuses
operation ListLocalDeployments {
    input: ListLocalDeploymentsRequest,
    output: ListLocalDeploymentsResponse,
    errors: [ServiceError]
}

operation ListComponents {
    input: ListComponentsRequest,
    output: ListComponentsResponse,
    errors: [ServiceError]
}

/// Publish an MQTT message to AWS IoT message broker
operation PublishToIoTCore {
    input: PublishToIoTCoreRequest,
    output: PublishToIoTCoreResponse,
    errors: [ServiceError, UnauthorizedError]
}

/// Subscribe to a topic in AWS IoT message broker.
operation SubscribeToIoTCore {
    input: SubscribeToIoTCoreRequest,
    output: SubscribeToIoTCoreResponse,
    errors: [ServiceError, UnauthorizedError]
}

// This API can be used only by stream manager, customer component calling this API will receive UnauthorizedError
operation ValidateAuthorizationToken {
    input: ValidateAuthorizationTokenRequest,
    output: ValidateAuthorizationTokenResponse,
    errors: [InvalidTokenError, UnauthorizedError, ServiceError]
}

// Retrieves a secret stored in AWS secrets manager
operation GetSecretValue {
    input: GetSecretValueRequest,
    output: GetSecretValueResponse,
    errors: [UnauthorizedError, ResourceNotFoundError, ServiceError]
}

// Generate a password for the HttpDebugView component
operation CreateDebugPassword {
    input: CreateDebugPasswordRequest,
    output: CreateDebugPasswordResponse,
    errors: [UnauthorizedError, ServiceError]
}

//-----------Shapes------------------------

structure UpdateStateRequest {
    @required
    state: ReportedLifecycleState
}

structure SubscribeToComponentUpdatesResponse {
    messages: ComponentUpdatePolicyEvents
}

@streaming
union ComponentUpdatePolicyEvents {
    preUpdateEvent: PreComponentUpdateEvent,
    postUpdateEvent: PostComponentUpdateEvent
}

structure PreComponentUpdateEvent {
    @required
    deploymentId: String,
    @required
    isGgcRestarting: Boolean
}

structure PostComponentUpdateEvent {
    @required
    deploymentId: String
}

structure DeferComponentUpdateRequest {
    @required
    deploymentId: String,
    message: String,
    recheckAfterMs: Long
}

list KeyPath {
    member: String
}

structure GetConfigurationRequest {
    componentName: String,
    @required
    keyPath: KeyPath
}

structure GetConfigurationResponse {
    componentName: String,
    value: Document
}

structure UpdateConfigurationRequest {
    keyPath: KeyPath,
    @required
    timestamp: Timestamp,
    @required
    valueToMerge: Document
}

structure SubscribeToConfigurationUpdateRequest {
    componentName: String,
    @required
    keyPath: KeyPath
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
    keyPath: KeyPath
}

structure SubscribeToValidateConfigurationUpdatesResponse {
    messages: ValidateConfigurationUpdateEvents
}

@streaming
union ValidateConfigurationUpdateEvents {
    validateConfigurationUpdateEvent: ValidateConfigurationUpdateEvent
}

structure ValidateConfigurationUpdateEvent {
    configuration: Document,
    @required
    deploymentId: String
}

structure ConfigurationValidityReport {
    @required
    status: ConfigurationValidityStatus,
    @required
    deploymentId: String,
    message: String
}

structure SendConfigurationValidityReportRequest {
    @required
    configurationValidityReport: ConfigurationValidityReport
}

structure SubscribeToTopicRequest {
    @required
    topic: String
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

structure PublishToTopicResponse { }

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

structure GetComponentDetailsRequest {
    @required
    componentName: String
}

structure GetComponentDetailsResponse {
    @required
    componentDetails: ComponentDetails
}

structure ComponentDetails {
    @required
    componentName: String,
    @required
    version: String,
    @required
    state: LifecycleState,
    configuration: Document
}

structure RestartComponentRequest {
    @required
    componentName: String
}

structure RestartComponentResponse {
    @required
    restartStatus: RequestStatus,
    message: String
}

structure StopComponentRequest {
    @required
    componentName: String
}

structure StopComponentResponse {
    @required
    stopStatus: RequestStatus,
    message: String
}

/// *recipeDirectoryPath*: All recipes files in this directory will be copied over to the kernel package store.
/// *artifactDirectoryPath*: All artifact files in this directory will be copied over to the kernel package store.
structure UpdateRecipesAndArtifactsRequest {
    recipeDirectoryPath: String,
    artifactsDirectoryPath: String
}

/// *groupName*: The thing group name the deployment is targeting. If the group name is
/// not specified, "DEFAULT" will be used
/// *rootComponentVersionsToAdd*: Map of component name to version. Components will be added
/// to the group's existing root components.
/// *rootComponentsToRemove*: List of components that need to be removed from the group
/// For instance if new artifacts were loaded in this request but recipe version did not change
/// *componentToConfiguration*: Map of component names to configuration.
/// *componentToRunWithInfo*: Map of component names to component run as info.
structure CreateLocalDeploymentRequest {
    // None of the members are required by themselves but this structure cannot be empty
    groupName: String,
    rootComponentVersionsToAdd: ComponentToVersionMap,
    rootComponentsToRemove: ComponentList,
    componentToConfiguration: ComponentToConfiguration,
    componentToRunWithInfo: ComponentToRunWithInfo
}

structure CreateLocalDeploymentResponse {
    deploymentId: String
}

list ComponentList {
    member: String
}

map ComponentToVersionMap {
    key: String,
    value: String
}

map ComponentToConfiguration {
    key: String,
    value: Document
}

map ComponentToRunWithInfo {
    key: String,
    value: RunWithInfo
}

structure RunWithInfo {
    posixUser: String
}

structure GetLocalDeploymentStatusRequest {
    // TODO: Add length restrictions after application code implementation
    @required
    deploymentId: String
}

structure GetLocalDeploymentStatusResponse {
    @required
    deployment: LocalDeployment
}

structure LocalDeployment {
    @required
    deploymentId: String,
    @required
    status: DeploymentStatus
}

structure ListLocalDeploymentsResponse {
    localDeployments: ListOfLocalDeployments
}

list ListOfLocalDeployments {
    member: LocalDeployment
}

structure ListComponentsResponse {
    components: ListOfComponents
}

list ListOfComponents {
    member: ComponentDetails
}

structure PublishToIoTCoreRequest {
    @required
    topicName: String,
    @required
    qos: QOS,
    payload: Blob
}

structure SubscribeToIoTCoreRequest {
    @required
    topicName: String,
    @required
    qos: QOS,
}

structure SubscribeToIoTCoreResponse {
    messages: IoTCoreMessage
}

@streaming
union IoTCoreMessage{
    message: MQTTMessage
}

structure MQTTMessage {
    @required
    topicName: String,
    payload: Blob
}

structure ValidateAuthorizationTokenRequest {
    @required
    token: String
}

structure ValidateAuthorizationTokenResponse {
    @required
    isValid: Boolean
}

@sensitive
union SecretValue {
    secretString: String,
    secretBinary: Blob
}

list SecretVersionList {
    member: String
}

structure GetSecretValueRequest {
    @required
    secretId: String,
    versionId: String,
    versionStage: String
}

structure GetSecretValueResponse {
    @required
    secretId: String,
    @required
    versionId: String,
    @required
    versionStage: SecretVersionList,
    @required
    secretValue: SecretValue
}

structure CreateDebugPasswordRequest {
}

structure CreateDebugPasswordResponse {
    @required
    password: String,
    @required
    username: String,
    @required
    passwordExpiration: Timestamp
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
    },
    {
        value:"NEW",
        name:"NEW"
    },
    {
        value:"FINISHED",
        name:"FINISHED"
    },
    {
        value:"INSTALLED",
        name:"INSTALLED"
    },
    {
        value:"BROKEN",
        name:"BROKEN"
    },
    {
        value:"STARTING",
        name:"STARTING"
    },
    {
        value:"STOPPING",
        name:"STOPPING"
    }
])
string LifecycleState

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
string ReportedLifecycleState

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

@enum([
    {
        value: "SUCCEEDED",
        name: "SUCCEEDED"
    },
    {
        value: "FAILED",
        name: "FAILED"
    }
])
string RequestStatus

@enum([
    {
        value: "QUEUED",
        name: "QUEUED"
    },
    {
        value:"IN_PROGRESS",
        name:"IN_PROGRESS"
    },
    {
        value:"SUCCEEDED",
        name:"SUCCEEDED"
    },
    {
        value:"FAILED",
        name:"FAILED"
    }
])
string DeploymentStatus

// values for QOS are not finalised yet.
@enum([
    {
        value: "0",
        name: "AT_MOST_ONCE"
    },
    {
        value: "1",
        name: "AT_LEAST_ONCE"

    }
])
string QOS

//----------errors----------------------

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

@error("client")
structure ComponentNotFoundError {
    message: String
}

@error("client")
structure InvalidRecipeDirectoryPathError {
    message: String
}

@error("client")
structure InvalidArtifactsDirectoryPathError {
    message: String
}

@error("client")
structure InvalidArgumentsError {
    message: String
}

@error("client")
structure FailedUpdateConditionCheckError {
    message: String
}

@error("server")
structure ServiceError {
    message: String
}

@error("server")
structure InvalidTokenError {
    message: String
}

/// Empty structures follow
structure SubscribeToValidateConfigurationUpdatesRequest {}
structure SubscribeToComponentUpdatesRequest {}
structure UpdateStateResponse {}
structure DeferComponentUpdateResponse {}
structure UpdateConfigurationResponse {}
structure SendConfigurationValidityReportResponse {}
structure ListLocalDeploymentsRequest {}
structure ListComponentsRequest {}
structure PublishToIoTCoreResponse {}
structure UpdateRecipesAndArtifactsResponse {}