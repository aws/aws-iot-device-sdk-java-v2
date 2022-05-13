# PREREQUISITES

## Java 8+

You can install Java 8+ by following the instructions on the Java website and downloading a build for your operating system: [Download and Install Java](https://www.java.com/en/download/help/download_options.html)

## JDK 8 or above

You can install the Java Developer Kit (JDK) 8+ by following the instructions on the Oracle website and downloading a build for your operating system: [Download and install JDK](https://docs.oracle.com/en/java/javase/18/install/overview-jdk-installation.html#GUID-8677A77F-231A-40F7-98B9-1FD0B48C346A)

## Set JAVA_HOME

Below are instructions on how you can set `JAVA_HOME`, which varies from depending on whether you are on Windows or are on MacOS/Linux:

### Windows
1. Open "Edit the system environment variable"
2. Click "New" to create new environment variable
   - variable name: `JAVA_HOME`
   - variable value: `<jdk_install_path>` (example: `C:\Program Files\Java\jdk-17.0.2`)
3. Press "Ok" to save the changes
4. re-open the command prompt for the environment variables to apply

### MacOS and Linux
Run the following command to set the JAVA_PATH
``` sh
# (example: "/Library/Java/JavaVirtualMachines/jdk-10.jdk/Contents/Home")
export JAVA_HOME=<jdk_install_path>
```

## C++ 11 Compiler

To build the SDK, you will need a compiler that can compile C++ 11 code or higher. C++ compilers vary based on platform, but listed below are a few of the most common and the minimum version required:

* Clang: 3.9 or higher
* GCC: 4.8 or higher
* MSVC: 2015 or higher

Listed below are ways to install C++ 11 compilers on [Windows](#windows-c-compilers), [MacOS](#macos-c-compiler), and [Linux](#linux-c-compilers).

## CMake 3.1+

You will also need CMake to build the SDK. The minimum required version is CMake 3.1.

Below are the instructions to install CMake in a non-platform specific way:

1. Download CMake3.1+ for your platform: https://cmake.org/download/
2. Run the Cmake Installer. Make sure you add CMake into **PATH**.
3. Restart the command prompt / terminal.

Listed below are also instructions to install CMake on [Windows](#windows-cmake), [MacOS](#macos-cmake), and [Linux](#linux-cmake).

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

### Windows C++ Compilers

#### MinGW-w64

MinGW-w64 is a project that allows for `GCC` compiler support on Windows. There are several ways to install MinGW-w64, and this document will explain the workflow for adding MinGW-w64 using MSYS2, a software distribution and building platform for Windows.

MinGW-w64 is fully supported on [Visual Studio Code](https://code.visualstudio.com/), a programming IDE that is a lightweight alternative to Visual Studio that supports many programming languages. The steps below are loosely based on the documentation for [installing MinGW-w64 for Visual Studio code](https://code.visualstudio.com/docs/cpp/config-mingw).

1. Download and run the MSYS2 installer from the [MSYS2 website](https://www.msys2.org/).
2. Follow the install instructions on the [MSYS2 website](https://www.msys2.org/).
   * Make sure to follow the instructions on the website for updating the database and base packages!
   * Installing MinGW-w64 may be part of these instructions. If so, install MinGW-w64 using the instructions there.
3. If MinGW-w64 was not part of the install instructions for MSYS2, run `pacman -S --needed base-devel mingw-w64-x85_64-toolchain`.
4. Next you need to add MinGW to your windows `PATH` environment variables so you can run it from the terminal.
5. Open the Windows Settings. You can do this by typing `settings` into the search bar or by opening the Windows start menu and navigating to the Windows Settings (should be called "Settings") application.
6. Once the Windows Settings window is open, search for `Edit environment variables for your account`.
7. Select the `Path` variable in the `User variables` property and press the `Edit` button.
8. Select `New` and then add the MinGW-w64 `bin` folder to this path. If you used the command in step 3, it should be located in `C:\msys64\mingw64\bin`. If you installed using a different method, you will need to find the `mingw64/bin` folder on your computer.
9. Once you have added the path to the `Path` variable in the `User variables` property, select `OK` and save.
10. Close any console/terminal windows you have open. This is because the console/terminal will not see the updated `PATH` variable unless it is restarted by closing and reopening.
11. Confirm that MingW-w64 is installed by running either `g++ --version` or `gdb --version`. You should get a print out showing the installed version of the C++ compiler.

#### MSVC

Microsoft Visual C++ (MSVC) is a C++ compiler that is supported and maintained by Microsoft, and is supported by the C++ SDK. To install MSVC, you will need to install Visual Studio using the instructions below.

Install Visual Studio with MSVC
1. Download **Visual Studio Installer** https://visualstudio.microsoft.com/downloads/
2. Run the installer, check the **Desktop development with C++** workload and select Install.
3. Verify your MSVC installation
   * In Windows Start up Menu, try open "Developer Command Prompt for VS".
   * In the opened terminal/console window, type `cl.exe` and it *should* output the compiler version.
   * You can also find the compiler version by opening Visual Studio by selecting `help` and then `about`.

If using MSVC, you will need to use the Developer Command Prompt instead of the standard terminal when compiling the SDK and samples.

### Windows CMake

#### MinGW-w64

If you installed MinGW-w64 via `MSYS32` in the steps above, you can easily install CMake using the following:

1. Run `pacman -S mingw-w64-x86_64-cmake`.
2. You will also need to install a build tool. You can install `ninja` or `make`.
3. To install `ninja` run `pacman -S mingw-w64-x86_64-ninja`.
4. To install `make` run `pacman -S mingw-w64-x86_64-make`.
5. Run `cmake --version` to check that CMake is properly installed.

#### Manual Install

You can also install CMake manually by following the install instructions on the CMake website:

1. Download CMake3.1+ for Windows: https://cmake.org/download/
2. Run the Cmake Installer.
4. Next you need to add CMake to your windows `PATH` environment variables so you can run it from the terminal.
  * Note: The installer should include an option to add CMake to the system path for all users. If you have checked this box, you can skip steps `5` through `9`.
5. Open the Windows Settings. You can do this by typing `settings` into the search bar or by opening the Windows start menu and navigating to the Windows Settings (should be called "Settings") application.
6. Once the Windows Settings window is open, search for `Edit environment variables for your account`.
7. Select the `Path` variable in the `User variables` property and press the `Edit` button.
8. Select `New` and then add the CMake `bin` folder to this path. If you do not modify the install path, it should be located around `C:\Program Files (x86)\CMake.x.x` where `x.x` is the version. If you installed CMake to a different directory, then you will need to modify the path accordingly.
9. Once you have added the path to the `Path` variable in the `User variables` property, select `OK` and save.
10. Close any console/terminal windows you have open. This is because the console/terminal will not see the updated `PATH` variable unless it is restarted by closing and reopening.
11. Run `cmake --version` to check that CMake is properly installed.


## MacOS Instructions

### MacOS Java 8+

Go to the [download page for Java](https://www.java.com/en/download/) and download the installer for your platform. Once the downloader is installed, launch it and follow the prompts to install Java. You can find detailed steps on installing Java for MacOS on the Java website [page for MacOS](https://www.java.com/en/download/help/download_options.html#mac).

### MacOS JDK 8+

Go to the Oracle website [download page for MacOS](https://docs.oracle.com/en/java/javase/18/install/installation-jdk-macos.html#GUID-2FE451B0-9572-4E38-A1A5-568B77B146DE) and download the JDK `dmg` file following the instructions on the `Installing the JDK on MacOS` section. Once the JDK `dmg` is downloaded, launch it and follow the prompts to install the JDK.

### MacOS JAVA_HOME

Below are the steps to set `JAVA_HOME` for MacOS:

``` sh
# (example: "/Library/Java/JavaVirtualMachines/jdk-10.jdk/Contents/Home")
export JAVA_HOME=<jdk_install_path>
```

### MacOS C++ Compiler

#### XCode Command Line Tools using `brew`

XCode Command Line Tools is the easiest way to install C++ compilers on MacOS, as it is officially supported and maintained by Apple. By installing the XCode Command Line tools, you will automatically install `clang`, which can compile C++ 11 code. One way to install XCode Command Line Tools is using `brew`.

[Brew](https://brew.sh/) is a command line package manager that makes it easy to install packages and software dependencies. The instructions to install through `brew` are below:

1. Open a new terminal and input the following command:
``` sh
bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```
2. If XCode Command Line Tools are not installed, the `brew` install process will ask if you want to install. Type `y` to install.
3. Wait for `brew` to install the XCode Command Line Tools. This make take some time.
4. Once `brew` is finished, confirm the XCode Command Line Tools have installed by opening a new terminal and inputting `clang --version`.

If stuck waiting for `brew` to install XCode Command Line Tools for over 15-20 minutes, you many need to cancel the installation (`CTRL-C` in the terminal) and install XCode Command Line Tools though the installer.

##### XCode Command Line Tools using installer

You can also install XCode Command Line Tools manually through an installer download on Apple's website. The instructions to install through the installer are below:

1. Go to [developer.apple.com/downloads](https://developer.apple.com/download/all/).
2. Input your AppleID to access the developer downloads.
3. From the presented list, scroll until you find `Command Line Tools for Xcode <version>`.
4. Select `view more details` and then select `Additionals Tools for Xcode <version>.dmg`.
5. Once downloaded, double click the `.dmg` and follow the installer instructions.
6. Confirm XCode Command Line Tools have installed by opening a new terminal and inputting `clang --version`.

### MacOS CMake
#### CMake using `brew`

CMake can easily be installed using `brew`, so if you installed `brew` for XCode Command Line Tools, you can run the following to install CMake:

1. Confirm you have `brew` installed:
``` sh
brew --version
```
2. Install CMake by running `brew install cmake`.
3. Close any console/terminal windows you have open. This is to refresh the console/terminal so it uses the latest changes.
4. Confirm CMake is installed by running `cmake --version`.

#### Manual Install

You can also install CMake manually by following the install instructions on the CMake website:

1. Go to [cmake.org/install](https://cmake.org/install/).
2. Follow the install instructions for MacOS on the website page.
3. Drag and drop the CMake application from the downloaded installer into your Applications folder. A window should open once you have mounted the CMake installer that easily allows you to do this via drag-and-drop.
4. You may need to manually add CMake to your `path` so you can run it in the terminal. To do this, run the following command:
``` sh
sudo "/Applications/CMake.app/Contents/bin/cmake-gui" --install
```
5. This will create the symlinks so you can run CMake from the terminal.
6. Close any console/terminal windows you have open. This is to refresh the console/terminal so it uses the latest changes.

## Linux

### Linux Java 8+

Go to the [download page for Java](https://www.java.com/en/download/) and download the installer for your platform. Once the downloader is installed, launch it and follow the prompts to install Java. You can find detailed steps on installing Java for Linux on the Java website [page for Linux](https://www.java.com/en/download/help/download_options.html#linux).

### Linux JDK 8+

Go to the Oracle website [download page for Linux](https://docs.oracle.com/en/java/javase/18/install/installation-jdk-linux-platforms.html#GUID-737A84E4-2EFF-4D38-8E60-3E29D1B884B8) and follow the download and install instructions for your Linux operating system.

### Linux JAVA_HOME

Below are the steps to set `JAVA_HOME` for Linux:

``` sh
# (example: "/usr/lib64/jvm/jdk-10.jdk/jre/bin/java")
export JAVA_HOME=<jdk_install_path>
```

### Linux C++ Compilers

Many Linux operating systems have C++ compilers installed by default, so you might already `clang` or `gcc` preinstalled.
To test, try running the following in a new terminal:

``` sh
clang --version
```
``` sh
gcc --version
```

If these commands fail, then please follow the instructions below for installing a C++ compiler on your Linux operating system.

If your Linux operating system is not in the list, please use a search engine to find out how to install either `clang` or `gcc` on your Linux operating system.

#### Install GCC or Clang on Ubuntu

1. Open a new terminal
2. (optional) Run `sudo apt-get update` to get latest package updates.
3. (optional) Run `sudo apt-get upgrade` to install latest package updates.
4. Run `sudo apt-get install build-essential` to install GCC or `sudo apt-get install clang` to install Clang.
5. Once the install is finished, close the terminal and reopen it.
6. Confirm GCC is installed by running `gcc --version` or Clang is installed by running `clang --version`.

#### Install GCC or Clang on Arch Linux

1. Open a new terminal.
2. Run `sudo pacman -S gcc` to install GCC or `sudo pacman -S clang` to install Clang.
3. Once the install is finished, close the terminal and reopen it.
4. Confirm Clang is installed by running `gcc --version`.

### Linux CMake

There are several ways to install CMake depending on the Linux operating system. Several Linux operating systems include CMake in their software repository applications, like the Ubuntu Software Center for example, so you may want to check there first. Below are the instructions to install CMake for Ubuntu and Arch Linux.

If your Linux operating system is not in the list below, please use a search engine to find out how to install CMake on your Linux operating system. You can also always try to install CMake manually using the generic install instructions at the top of this page.

#### Install CMake on Ubuntu

1. Open the Ubuntu Software Center
2. In the search bar enter `cmake` and select `CMake - cross-platform build system` from the list
3. Press the `install` button
4. After CMake has installed open a new terminal
5. Type `cmake --version` to confirm CMake is installed

Or using the command line:

1. Open a new terminal
2. Run `sudo snap install cmake` to install CMake from the snap store
3. After CMake has installed, close the terminal and reopen it
4. Type `cmake --version` to confirm CMake is installed

#### Install CMake on Arch Linux

1. Open a new terminal.
2. Run `sudo pacman -S cmake` to install Cmake
3. After CMake has installed, close the terminal and reopen it
4. Type `cmake --version` to confirm CMake is installed.
