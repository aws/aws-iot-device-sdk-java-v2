// The smithy IDL file used for testing event-stream-rpc libraries. Changing the models here
// will aversely affect the expectation of unit tests in the package.

namespace awstest

@documentation("Service model defined to test event-stream-rpc client, server, and model.")
service EchoTestRPC {
    version: "2020-10-14",
    operations: [
        EchoMessage,
        EchoStreamMessages,
        CauseServiceError,
        CauseStreamServiceToError,
    ]
}

//-----------Operations--------------------

@documentation("Returns the same data sent in the request to the response")
operation EchoMessage {
    input:  EchoMessageRequest,
    output: EchoMessageResponse
}

@documentation("Initial request and response are empty, but echos streaming messages sent by client")
operation EchoStreamMessages {
    input:  EchoStreamingRequest,
    output: EchoStreamingResponse
}

@documentation("Throws a ServiceError instead of returning a response.")
operation CauseServiceError {
    errors: [ServiceError]
}

@documentation("Responds to initial request normally then throws a ServiceError on stream response")
operation CauseStreamServiceToError {
    input: EchoStreamingRequest,
    output: EchoStreamingResponse,
    errors: [ServiceError]
}

//-----------Shapes------------------------


structure EchoMessageRequest {
    message: MessageData
}

structure EchoMessageResponse {
    message: MessageData
}

structure EchoStreamingRequest {
    message: EchoStreamingMessage
}

structure EchoStreamingResponse {
    message: EchoStreamingMessage
}

structure MessageData {
    stringMessage: String,
    booleanMessage: Boolean,
    timeMessage: Timestamp,
    documentMessage: Document,
    enumMessage: FruitEnum,
    blobMessage: Blob,
    stringListMessage: StringList,
    keyValuePairList: KeyValuePairList
}

@streaming
union EchoStreamingMessage {
    streamMessage: MessageData,
    keyValuePair: Pair
}

structure Pair {
    @required
    key: String,
    @required
    value: String
}

list StringList {
    member: String
}

list KeyValuePairList {
    member: Pair
}

@enum([
    {
        value: "apl",
        name: "APPLE"
    },
    {
        value: "org",
        name: "ORANGE"
    },
    {
        value: "ban",
        name: "BANANA"
    },
    {
        value: "pin",
        name: "PINEAPPLE"
    }
])
string FruitEnum

//----------errors----------------------

@error("server")
structure ServiceError {
    message: String
}

