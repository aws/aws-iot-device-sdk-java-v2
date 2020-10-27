plugins {
    id("software.amazon.smithy").version("0.5.0")
}

repositories {
    mavenLocal()
    mavenCentral()
}

tasks.compileJava {
    dependsOn("smithyBuildJar")
}

sourceSets {
    main {
        java {
            srcDirs("${buildDir}/smithyprojections/greengrass-client/source/event-stream-rpc-java/client/",
                    "${buildDir}/smithyprojections/greengrass-client/source/event-stream-rpc-java/model/")
        }
    }
}

dependencies {
    implementation(project(":smithy-event-stream-rpc-java"))
    implementation(project(":event-stream-rpc-model"))
    implementation(project(":event-stream-rpc-client"))

    implementation("software.amazon.awssdk.crt:aws-crt:1.0.0-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.8.6")
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
