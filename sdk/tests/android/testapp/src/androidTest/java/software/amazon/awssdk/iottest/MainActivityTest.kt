/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iottest

import android.app.Instrumentation
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

import java.lang.System
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    fun assetContents(assetName: String) : String {
        val testContext = InstrumentationRegistry.getInstrumentation().targetContext
        val testRes = testContext.getResources()

        testRes.assets.open(assetName).use {res ->
            val bytes = ByteArray(res.available())
            res.read(bytes)
            return String(bytes).trim()
        }
    }

    fun getArgsForSample(name: String) : Array<String?> {
        val testContext = InstrumentationRegistry.getInstrumentation().targetContext
        val testRes = testContext.getResources()
        var resourceNames = mutableListOf<String>()
        var resourceMap = HashMap<String, String>()
        val args = mutableListOf<String?>()

        resourceNames.add("endpoint.txt")

        // Add sample specific file loading here
        when(name) {

            "shadow.ShadowSample" -> {
                resourceNames.add("shadowCertificate.pem")
                resourceNames.add("shadowPrivatekey.pem")
            }

            "jobs.JobsSample" -> {
                resourceNames.add("jobsCertificate.pem")
                resourceNames.add("jobsPrivatekey.pem")
            }

            "cognitoconnect.CognitoConnect" -> {
                resourceNames.add("cognitoIdentity.txt")
            }

            "mqtt5x509.Mqtt5X509" -> {
                resourceNames.add("mqtt5PubSubCertificate.pem")
                resourceNames.add("mqtt5PubSubPrivatekey.pem")
            }

            "customkeyopsconnect.CustomKeyOpsConnect" -> {
                resourceNames.add("customKeyOpsKey.pem")
                resourceNames.add("customKeyOpsCert.pem")
            }
        }

        // Load resource into a cached location for use by sample
        for(resourceName in resourceNames) {
            try {
                testRes.assets.open(resourceName).use { res->
                    val cachedName = "${testContext.externalCacheDir}/${resourceName}"
                    FileOutputStream(cachedName).use { cachedRes ->
                        res.copyTo(cachedRes)
                    }
                    resourceMap[resourceName] = cachedName
                }
            }
            catch (e: Exception) {
                // If a file that's supposed to be here is missing, fail the test
                fail(e.toString())
            }
        }

        // Args for all samples
        args.addAll(arrayOf(
            "--endpoint", assetContents("endpoint.txt")))

        // Set sample specific args
        when(name){

            "jobs.JobsSample"  -> {
                args.addAll(arrayOf(
                    "--cert", resourceMap["jobsCertificate.pem"],
                    "--key", resourceMap["jobsPrivatekey.pem"],
                    "--port", "8883",
                    "--thing_name", "CI_Jobs_Thing"))
            }

            "shadow.ShadowSample" -> {
                args.addAll(arrayOf(
                    "--cert", resourceMap["shadowCertificate.pem"],
                    "--key", resourceMap["shadowPrivatekey.pem"],
                    "--port", "8883",
                    "--thing_name", "CI_Shadow_Thing"))
            }

            "cognitoconnect.CognitoConnect" -> {
                args.addAll(arrayOf(
                    "--signing_region", "us-east-1",
                    "--cognito_identity", assetContents("cognitoIdentity.txt")))
            }

            "mqtt5x509.Mqtt5X509" -> {
                args.addAll(arrayOf(
                    "--cert", resourceMap["mqtt5PubSubCertificate.pem"],
                    "--key", resourceMap["mqtt5PubSubPrivatekey.pem"],
                    "--message", "Hello World From Android"
                ))
            }

            "customkeyopsconnect.CustomKeyOpsConnect" -> {
                args.addAll(arrayOf(
                    "--cert", resourceMap["customKeyOpsCert.pem"],
                    "--key", resourceMap["customKeyOpsKey.pem"]))
            }
        }

        return args.toTypedArray()
    }

    fun runSample(name: String) {
        try {
            val classLoader = Thread.currentThread().contextClassLoader
            println("Loading class: $name")
            val sampleClass = classLoader?.loadClass(name)
            println("Class loaded successfully: ${sampleClass?.name}")
            
            val sampleArgs = getArgsForSample(name)
            println("Args prepared: ${sampleArgs.joinToString(" ")}")
            
            val main = sampleClass?.getMethod("main", Array<String>::class.java)
            println("Main method found: ${main != null}")
            
            main?.invoke(null, sampleArgs)
            println("Sample execution completed")
        }
        catch (e: ClassNotFoundException) {
            fail("Class not found: $name - ${e.message}")
        }
        catch (e: NoSuchMethodException) {
            fail("Main method not found in $name - ${e.message}")
        }
        catch (e: Exception) {
            fail("Sample execution failed: ${e.javaClass.simpleName} - ${e.message} - Cause: ${e.cause}")
        }
    }

    @Test
    fun mqtt5PubSubSample(){
        runSample("mqtt5x509.Mqtt5X509")
    }
}
