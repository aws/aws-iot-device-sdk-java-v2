set ANDROID_SDK_ROOT=C:\Users\bg\AppData\Local\Android\Sdk
set JAVA_HOME=C:\Program Files\Java\jdk-17.0.2

call gradlew clean
@rem call gradlew publishToMavenLocal
call gradlew publishToMavenLocal -b iotdevicesdk/build.gradle
