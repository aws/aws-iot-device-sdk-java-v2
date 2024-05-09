Feature: Testing features of Greengrassv2 GDK_COMPONENT_NAME

    Background:
        Given my device is registered as a Thing
        And my device is running Greengrass

    @Sample @com.example.PythonHelloWorld
    Scenario: As a developer, I can create a component and deploy it on my device
        When I create a Greengrass deployment with components
            | com.example.PythonHelloWorld | /Users/igorabd/projects/aws-iot-device-sdk-java-v2/tests/greengrass/HelloWorld/recipe.yaml |
        And I deploy the Greengrass deployment configuration
        Then the Greengrass deployment is COMPLETED on the device after 180 seconds
        And I call my custom step
