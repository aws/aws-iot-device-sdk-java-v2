Feature: Testing device
    @HelloWorld
    Scenario: As a developer, I can create a component and deploy it on my device
        Given my device is registered as a Thing
        And my device is running Greengrass
        When I create a Greengrass deployment with components
            | com.example.PythonHelloWorld | file:/Users/igorabd/projects/aws-iot-device-sdk-java-v2/tests/greengrass/HelloWorld/recipe.yaml |
        And I deploy the Greengrass deployment configuration
        Then the Greengrass deployment is COMPLETED on the device after 180 seconds
        And I call my custom step
