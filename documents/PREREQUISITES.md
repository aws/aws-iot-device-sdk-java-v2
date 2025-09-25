# PREREQUISITES

Choose the operating system for instructions:

- [Windows](#windows-instructions)
- [macOS](#macos-instructions)
- [Linux](#linux-instructions)

## Windows Instructions

### Windows Java 8+

Go to the [download page for Java](https://www.java.com/en/download/) and download the installer for your platform. Once the downloader is installed, launch it and follow the prompts to install Java. You can find detailed steps on installing Java for Windows on the Java website [page for Windows](https://www.java.com/en/download/help/download_options.html#windows).

### Windows JDK 8+

Go to the Oracle website [download page for Windows](https://docs.oracle.com/en/java/javase/18/install/installation-jdk-microsoft-windows-platforms.html#GUID-A7E27B90-A28D-4237-9383-A58B416071CA) and download the JDK installer. Once the JDK installer is downloaded, launch it and follow the prompts to install the JDK.

### Windows JAVA_HOME

Below are the steps to set `JAVA_HOME` for Windows:

1. Open "Edit the system environment variable"
2. Click "New" to create new environment variable
   - variable name: `JAVA_HOME`
   - variable value: `<jdk_install_path>` (example: `C:\Program Files\Java\jdk-17.0.2`)
3. Press "Ok" to save the changes
4. re-open the command prompt for the environment variables to apply

### Windows ANDROID_HOME

1. Open "Edit the system environment variable"
2. Click "New" to create new environment variable
    - variable name: `ANDROID_HOME`
    - variable value: `<android_sdk_path>` (example: `C:\Users\YourUsername\AppData\Local\Android\Sdk`)
3. Press "Ok" to save the changes
4. Re-open the command prompt for the environment variables to apply

## macOS Instructions

### macOS Java 8+

Go to the [download page for Java](https://www.java.com/en/download/) and download the installer for your platform. Once the downloader is installed, launch it and follow the prompts to install Java. You can find detailed steps on installing Java for macOS on the Java website [page for macOS](https://www.java.com/en/download/help/download_options.html#mac).

### macOS JDK 8+

Go to the Oracle website [download page for macOS](https://docs.oracle.com/en/java/javase/18/install/installation-jdk-macos.html#GUID-2FE451B0-9572-4E38-A1A5-568B77B146DE) and download the JDK `dmg` file following the instructions on the `Installing the JDK on macOS` section. Once the JDK `dmg` is downloaded, launch it and follow the prompts to install the JDK.

### macOS JAVA_HOME

Below are the steps to set `JAVA_HOME` for macOS:

``` sh
export JAVA_HOME=<jdk_install_path>
```
, where `<jdk_install_path>` must be replaced with the JAVA SDK installation directory (for example, `/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home`).

### macOS ANDROID_HOME

Run the following command to set the ANDROID_HOME environment variable:

``` sh
export ANDROID_HOME=<android_sdk_path>
```
, where `<android_sdk_path>` must be replaced with the Android SDK installation directory (for example, `/Users/YourUsername/Library/Android/sdk`).

## Linux Instructions

### Linux Java 8+

Go to the [download page for Java](https://www.java.com/en/download/) and download the installer for your platform. Once the downloader is installed, launch it and follow the prompts to install Java. You can find detailed steps on installing Java for Linux on the Java website [page for Linux](https://www.java.com/en/download/help/download_options.html#linux).

### Linux JDK 8+

Go to the Oracle website [download page for Linux](https://docs.oracle.com/en/java/javase/18/install/installation-jdk-linux-platforms.html#GUID-737A84E4-2EFF-4D38-8E60-3E29D1B884B8) and follow the download and install instructions for your Linux operating system.

### Linux JAVA_HOME

Below are the steps to set `JAVA_HOME` for Linux:

``` sh
export JAVA_HOME=<jdk_install_path>
```
, where `<jdk_install_path>` must be replaced with the JAVA SDK installation directory (for example, `/usr/lib/jvm/jdk-17.jdk`).

### Linux ANDROID_HOME

Run the following command to set the ANDROID_HOME environment variable:

``` sh
export ANDROID_HOME=<android_sdk_path>
```
, where `<android_sdk_path>` must be replaced with the Android SDK installation directory (for example, `/home/YourUsername/Library/Android/sdk`).
