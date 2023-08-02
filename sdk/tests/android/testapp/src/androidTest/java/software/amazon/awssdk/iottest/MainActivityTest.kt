package software.amazon.awssdk.iottest

import android.app.Instrumentation
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.Assert.*

import android.util.Log
import kotlin.concurrent.thread
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
        System.setProperty("aws.crt.ci", "True")
        val testContext = InstrumentationRegistry.getInstrumentation().targetContext
        val testRes = testContext.getResources()
        var resourceNames = mutableListOf<String>()
        var resourceMap = HashMap<String, String>()
        val args = mutableListOf<String?>()

        resourceNames.add("endpoint.txt")

        when(name) {
            "pubsub.PubSub" -> {
                resourceNames.add("pubSubCertificate.pem")
                resourceNames.add("pubSubPrivatekey.pem")
            }

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
        }

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
                fail(e.toString())
            }
        }

        args.addAll(arrayOf(
            "--endpoint", assetContents("endpoint.txt"),
            "--verbosity", "Debug"))

        when(name){
            "pubsub.PubSub" -> {
                args.addAll(arrayOf(
                    "--cert", resourceMap["pubSubCertificate.pem"],
                    "--key", resourceMap["pubSubPrivatekey.pem"],
                    "--port", "8883",
                    "--message", "message.txt", "Hello World From Android"))
            }

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
        }

        return args.toTypedArray()
    }


    // @Test
    // fun useAppContext(){
    //     val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    //     assertEquals("software.amazon.awssdk.iottest", appContext.packageName)
    // }

    @Test
    fun pubSubSample(){
        val classLoader = Thread.currentThread().contextClassLoader
        val sampleClass = classLoader?.loadClass("pubsub.PubSub")

        val sampleArgs = getArgsForSample("pubsub.PubSub")

        val main = sampleClass?.getMethod("main", Array<String>::class.java)

        try {
            main?.invoke(null, sampleArgs)
        }
        catch (e:Exception) {
            fail(e.cause.toString())
        }
    }

    @Test
    fun cognitoConnectSample(){
        val classLoader = Thread.currentThread().contextClassLoader
        val sampleClass = classLoader?.loadClass("cognitoconnect.CognitoConnect")

        val sampleArgs = getArgsForSample("cognitoconnect.CognitoConnect")
        val main = sampleClass?.getMethod("main", Array<String>::class.java)

        try {
            main?.invoke(null, sampleArgs)
        }
        catch (e:Exception) {
            fail(e.cause.toString())
        }
    }

    @Test
    fun shadowSample(){
        val classLoader = Thread.currentThread().contextClassLoader
        val sampleClass = classLoader?.loadClass("shadow.ShadowSample")

        val sampleArgs = getArgsForSample("shadow.ShadowSample")
        val main = sampleClass?.getMethod("main", Array<String>::class.java)

        try {
            main?.invoke(null, sampleArgs)
        }
        catch (e:Exception) {
            fail(e.cause.toString())
        }
    }

    @Test
    fun jobsSample(){
        val classLoader = Thread.currentThread().contextClassLoader
        val sampleClass = classLoader?.loadClass("jobs.JobsSample")

        val sampleArgs = getArgsForSample("jobs.JobsSample")
        val main = sampleClass?.getMethod("main", Array<String>::class.java)

        try {
            main?.invoke(null, sampleArgs)
        }
        catch (e:Exception) {
            fail(e.cause.toString())
        }
    }
}