/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iotsamples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.util.Log
import android.content.pm.ApplicationInfo
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.PrintStream
import java.io.IOException
import java.io.File
import java.lang.Exception
import kotlin.concurrent.thread

val SAMPLES = mapOf(
    "Publish/Subscribe MQTT5 Sample" to "mqtt5.pubsub.PubSub",
    "Publish/Subscribe MQTT3 Sample" to "pubsub.PubSub",
    "Jobs Client Sample" to "jobs.JobsSample",
    "Shadow Client Sample" to "shadow.ShadowSample",
    "Cognito Client Sample" to "cognitoconnect.CognitoConnect",
    "PKCS11 Connect Sample" to "pkcs11connect.Pkcs11Connect"
)

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private class StreamTee(val source: OutputStream, val log: (message: String) -> Unit)
        : PrintStream(source, true) {
        init {
            if (source == System.out) {
                System.setOut(this)
            } else if (source == System.err) {
                System.setErr(this)
            }
        }

        override fun write(buf: ByteArray, off: Int, len: Int) {
            source.write(buf, off, len)
            log(String(buf.slice(IntRange(off, off+len-1)).toByteArray()))
        }

        override fun write(b: ByteArray) {
            source.write(b)
            log(String(b))
        }
    }

    private val stdout : StreamTee;
    private val stderr : StreamTee;

    private var console: TextView? = null;
    private var sampleSelect: Spinner? = null;

    init {
        stdout = StreamTee(System.out) { writeToConsole(it) }
        stderr = StreamTee(System.err) { writeToConsole(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        console = findViewById<TextView>(R.id.console)
        console?.isEnabled = false

        sampleSelect = findViewById<Spinner>(R.id.sampleSelect);

        val samples = SAMPLES.keys.toMutableList()
        samples.add(0, "Please select a sample")
        val samplesAdapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, samples)
        sampleSelect?.adapter = samplesAdapter

        sampleSelect?.onItemSelectedListener = this
    }

    private fun clearConsole() {
        runOnUiThread() {
            console?.text = ""
        }
    }

    private fun writeToConsole(message: String) {
        runOnUiThread() {
            console?.append(message)
        }
    }

    private fun onSampleComplete() {
        runOnUiThread() {
            writeToConsole("Sample Complete\n")
            sampleSelect?.isEnabled = true
        }
    }

    private fun assetContents(assetName: String) : String {
        resources.assets.open(assetName).use {res ->
            val bytes = ByteArray(res.available())
            res.read(bytes)
            return String(bytes).trim()
        }
    }

    private fun assetContentsOr(assetName: String, defaultValue: String) : String {
        return try {
            assetContents(assetName)
        } catch (fnf: FileNotFoundException) {
            defaultValue
        }
    }

    private fun debugLog(message: String){
        val logMessage = "DEBUG LOG " + message
        Log.e("STEVE", logMessage)
    }

    private fun checkForFiles(directoryPath: String){
        debugLog("Checking Path: ${directoryPath}")
        val libraryDir = File(directoryPath)

        if (libraryDir.exists()){
            if (libraryDir.isDirectory) {
                val filesInLibraryDir: Array<File>? = libraryDir.listFiles()
                if(filesInLibraryDir != null){
                    debugLog("Files in ${directoryPath}: ${filesInLibraryDir.contentToString()}")
                } else {
                    debugLog("listFiles(${directoryPath}) resulted in null")
                }
            } else {
                debugLog("${directoryPath} exists and is not a directory.")
            }
        } else {
            debugLog("${directoryPath} does not exist.")
        }
    }

    private fun SaveAssetToInternalStorage(context: Context, fileName: String): String{
        val internalDir: File = context.filesDir
        val libraryPath: String = "${internalDir}/${fileName}"
        // debugLog("${internalDir.absolutePath}")
        try {
            context.assets.open(fileName).use { inputStream ->
                FileOutputStream(libraryPath).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
            }
            debugLog("SaveAssetToInternalStorage: ${fileName}")
            checkForFiles(libraryPath)
            return libraryPath
        } catch (e: IOException) {
            debugLog("exception occured during opensc-pkcs11.so file write")
            return "UHOH"
        }
    }

    private fun runSample(name: String) {
        val classLoader = Thread.currentThread().contextClassLoader
        val sampleClass = try { classLoader?.loadClass(name) } catch(e:Exception) { null }
        if (sampleClass == null) {
            clearConsole()
            writeToConsole("Could not find sample '${name}'")
            return
        }
        writeToConsole("Running sample '${name}'\n")
        debugLog("Running Sample ${name}")
        thread(name="sample_runner", contextClassLoader = classLoader) {

            var isResourcesFound: Boolean = true
            val args = mutableListOf<String?>()
            var resourceNames = mutableListOf<String>()
            val resourceMap = HashMap<String, String>()

            // PKCS11 sample reqs
            // --endpoint <endpoint>
            // --cert <path to certificate>
            // --pkcs11_lib <path to PKCS11 lib>
            // --pin <user-pin>
            // --token_label <token-label>
            // --key_label <key-label>

            // All samples require endpoint.txt
            resourceNames.add("endpoint.txt")

            // Add required files for Samples here
            when(name) {
                "mqtt5.pubsub.PubSub", "pubsub.PubSub", "jobs.JobsSample", "shadow.ShadowSample" -> {
                    resourceNames.add("certificate.pem")
                    resourceNames.add("privatekey.pem")
                }

                "cognitoconnect.CognitoConnect" -> {
                    resourceNames.add("cognitoIdentity.txt")
                    resourceNames.add("signingRegion.txt")
                }

                "pkcs11connect.Pkcs11Connect" -> {
                    resourceNames.add("pkcs11-endpoint.txt")
                    resourceNames.add("opensc-pkcs11.so")
                    resourceNames.add("pkcs11-cert.pem")
                    // resourceNames.add("pkcs11_lib.txt")
                    resourceNames.add("pkcs11_pin.txt")
                    resourceNames.add("pkcs11_token_label.txt")
                    resourceNames.add("pkcs11_key_label.txt")
                }
            }

            // Copy to cache and get file locations for required files
            for (resourceName in resourceNames) {
                try {
                    resources.assets.open(resourceName).use { res ->
                        val cachedName = "${externalCacheDir}/${resourceName}"
                        FileOutputStream(cachedName).use { cachedRes ->
                            res.copyTo(cachedRes)
                        }
                        resourceMap[resourceName] = cachedName
                    }
                } catch (e: Exception) {
                    isResourcesFound = false;
                    writeToConsole(e.toString())
                    writeToConsole("\n'${resourceName}' must be in the assets folder for sample to run.\n")
                    break
                }
            }

            if(isResourcesFound) {
                // args.addAll(arrayOf("--endpoint", assetContents("endpoint.txt")))

                when(name) {
                    "mqtt5.pubsub.PubSub", "pubsub.PubSub" -> {
                        args.addAll(arrayOf(
                            "--endpoint", assetContents("endpoint.txt"),
                            "--cert", resourceMap["certificate.pem"],
                            "--key", resourceMap["privatekey.pem"],
                            "--port", assetContentsOr("port.txt", "8883"),
                            "--client_id", assetContentsOr("clientId.txt", "android-java-crt-test"),
                            "--topic", assetContentsOr("topic.txt", "test/topic"),
                            "--message", assetContentsOr("message.txt", "Hello World From Android")))
                    }

                    "jobs.JobsSample", "shadow.ShadowSample" -> {
                        args.addAll(arrayOf(
                            "--endpoint", assetContents("endpoint.txt"),
                            "--cert", resourceMap["certificate.pem"],
                            "--key", resourceMap["privatekey.pem"],
                            "--port", assetContentsOr("port.txt", "8883"),
                            "--client_id", assetContentsOr("clientId.txt", "android-java-crt-test"),
                            "--thing_name", assetContentsOr("thingName.txt", "aws-iot-unit-test")))
                    }

                    "cognitoconnect.CognitoConnect" -> {
                        args.addAll(arrayOf(
                            "--endpoint", assetContents("endpoint.txt"),
                            "--signing_region", assetContents("signingRegion.txt"),
                            "--cognito_identity", assetContents("cognitoIdentity.txt")))
                    }

                    "pkcs11connect.Pkcs11Connect" -> {
                        // Uncompressing and moving the PKCS11 library from assets folder to the internalDir
                        // for access by aws-c-io
                        val libraryPath = SaveAssetToInternalStorage(this, "opensc-pkcs11.so")
                        // val internalDir: File = this.filesDir
                        // val libraryPath: String = "${internalDir}/opensc-pkcs11.so"
                        // debugLog("${internalDir.absolutePath}")
                        // try {
                        //     this.assets.open("opensc-pkcs11.so").use { inputStream ->
                        //         FileOutputStream(libraryPath).use { outputStream ->
                        //             val buffer = ByteArray(1024)
                        //             var length: Int
                        //             while (inputStream.read(buffer).also { length = it } > 0) {
                        //                 outputStream.write(buffer, 0, length)
                        //             }
                        //         }
                        //     }
                        //     debugLog("libraryPath")
                        //     checkForFiles(libraryPath)
                        // } catch (e: IOException) {
                        //     debugLog("exception occured during opensc-pkcs11.so file write")
                        // }

                        val nativeLibraryDir: String = applicationInfo.nativeLibraryDir
                        val modifiedLibPath = nativeLibraryDir.replace("arm64", "arm64-v8a")
                        val modifiedPath = nativeLibraryDir.replace("/lib/", "/base.apk!/lib/")
                        val finalPath = modifiedPath.replace("arm64", "arm64-v8a")
                        val crtPkcs11Path = finalPath.replace("arm64-v8a", "arm64-v8a/libopensc-pkcs11.so")
                        val crtGetResourceNamePath = "/lib/arm64-v8a/libopensc-pkcs11.so"
                        val testPath = nativeLibraryDir + "/libopensc-pkcs11.so"
                        val cachedPath = resourceMap["opensc-pkcs11.so"]

                        debugLog("nativeLibraryDir")
                        checkForFiles(nativeLibraryDir)
                        debugLog("modifiedLibPath")
                        checkForFiles(modifiedLibPath)
                        debugLog("finalPath")
                        checkForFiles(finalPath)
                        debugLog("crtGetResourceNamePath")
                        checkForFiles(crtGetResourceNamePath)
                        debugLog("testPath")
                        checkForFiles(testPath)
                        if(cachedPath != null){
                            debugLog("cached file")
                            checkForFiles(cachedPath)
                        }

                        debugLog("Build.SUPPORTED_ABIS: ${Build.SUPPORTED_ABIS.contentToString()}")

                        // debugLog("Try loading from cached ${resourceMap["opensc-pkcs11.so"]}")

                        // Loading using the full path+file of the copied and saved to local cache version of the opensc-pkcs11.so
                        // file results in a library not accessible from namespace error. This seems to be related to permissions.
                        // System.load(resourceMap["opensc-pkcs11.so"])

                        // System.Load() will open a specific file at a full path location
                        // System.Load() with the full path+file name used by System.loadLibrary(opensc-pkcs11.so)
                        //results in the bad ELF magic error. Indicating the file was found but the library is the wrong arch.
                        // System.load(crtPkcs11Path)

                        // debugLog("trying System.loadLibrary(aws-crt-jni)")
                        // System.loadLibrary("aws-crt-jni")

                        // debugLog("trying System.loadLibrary(opensc-pkcs11)")
                        // System.loadLibrary() loads a library using just the base file name. For some reason it
                        // appears to add "lib" to the front resulting in "opensc-pkcs11" looking for and opening a file
                        // named "libopensc-pkcs11.so"
                        // System.loadLibrary("opensc-pkcs11")
                        // System.loadLibrary("empty-pkcs11")

                        // debugLog("loaded System.loadLibrary(opensc-pkcs11)")

                        args.addAll(arrayOf(
                            "--endpoint", assetContents("pkcs11-endpoint.txt"),
                            "--cert", resourceMap["pkcs11-cert.pem"],
                            "--pkcs11_lib",
                            // resourceMap["opensc-pkcs11.so"],
                            // "opensc-pkcs11.so",
                            // crtPkcs11Path,
                            // "empty-pkcs11",
                            // "opensc-pkcs11",
                            // crtGetResourceNamePath,
                            // testPath,
                            libraryPath,
                            "--pin", assetContents("pkcs11_pin.txt"),
                            // "--token_label", assetContents("pkcs11_token_label.txt"),
                            "--key_label", assetContents("pkcs11_key_label.txt"))
                            )
                    }
                }

                // Check for optional root CA file
                try {
                    resources.assets.open("rootca.pem").use { res ->
                        val cachedName = "${externalCacheDir}/rootca.pem"
                        FileOutputStream(cachedName).use { cachedRes ->
                            res.copyTo(cachedRes)
                        }
                        args.addAll(arrayOf("--ca_file", cachedName))
                    }
                } catch (e: Exception) {}


                // Check for optional logging level
                try {
                    resources.assets.open("verbosity.txt").use { res ->
                        val cachedName = "${externalCacheDir}/verbosity.txt"
                        FileOutputStream(cachedName).use { cachedRes ->
                            res.copyTo(cachedRes)
                        }
                        args.addAll(arrayOf("--verbosity", assetContents("verbosity.txt")))
                        val logLevel = assetContents("verbosity.txt")
                        writeToConsole("Logging at level: '${logLevel}'\n")
                    }
                } catch (e: Exception) {}

                val main = sampleClass.getMethod("main", Array<String>::class.java)

                try {
                    main.invoke(null, args.toTypedArray())
                } catch (e: Exception) {
                    writeToConsole(e.toString())
                }
            }
            onSampleComplete();
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        clearConsole()
        writeToConsole("Please select a sample above")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        clearConsole()
        val sampleName = parent?.getItemAtPosition(pos).toString()
        val sampleClassName = SAMPLES[sampleName]
        if (sampleClassName != null) {
            return runSample(sampleClassName)
        }
    }
}
