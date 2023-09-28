# Developping Instructions

Please first follow the instructions in the [main readme](../README.md)

## Consuming Local IoT Device SDK from Maven in your application
To build the SDK locally and test local changes do the following: 

Modify [sdk/pom.xml](https://github.com/aws/aws-iot-device-sdk-java-v2/sdk/pom.xml)
``` xml
<dependency>
  <groupId>software.amazon.awssdk.iotdevicesdk</groupId>
  <artifactId>aws-iot-device-sdk</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```
Note the version number matches the sdk version number in [aws-crt-java](https://github.com/awslabs/aws-crt-java/pom.xml)
under
```xml
    <version>1.0.0-SNAPSHOT</version>
```

Please note: The default location for local builds with maven is
Linux: /home/<User_Name>/.m2
Windows: C:\Users\<User\_Name>\.m2
Mac: /Users/<user_name>/.m2

It is safe to delete the whole directory
It is possible to pass this directory as an argument to the maven command
```bash
mvn -Dmaven.repo.local=/my/local/repository/path clean install
```
It is also possible to change it globally by modifying setting.xml in the maven
configuration files

After that making your changes, follow the guide in the [main page](../README.md)
