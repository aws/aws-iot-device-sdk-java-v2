package software.amazon.awssdk.iotsamples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.PrintStream
import java.lang.Exception
import kotlin.concurrent.thread

val SAMPLES = mapOf(
    "Publish/Subscribe Sample" to "pubsub.PubSub",
    "Jobs Client Sample" to "jobs.JobsSample",
    "Shadow Client Sample" to "shadow.ShadowSample"
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

    private fun runSample(name: String) {
        val classLoader = Thread.currentThread().contextClassLoader
        val sampleClass = classLoader?.loadClass(name);
        if (sampleClass == null) {
            clearConsole()
            writeToConsole("Could not find sample '${name}'")
        }
        writeToConsole("Running sample '${name}'\n")

        thread(name="sample_runner", contextClassLoader = classLoader) {

            var isResourcesFound: Boolean = true
            val args = mutableListOf<String?>()
            var resourceNames = mutableListOf<String>()
            val resourceMap = HashMap<String, String>()

            // All samples require endpoint.txt
            resourceNames.add("endpoint.txt")

            // Add required files for Samples here
            if (name == "pubsub.PubSub" || name == "jobs.JobsSample" || name == "shadow.ShadowSample"){
                resourceNames.add("certificate.pem")
                resourceNames.add("privatekey.pem")
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
                args.addAll(arrayOf(
                    "--endpoint", assetContents("endpoint.txt"),
                    "--cert", resourceMap["certificate.pem"],
                    "--key", resourceMap["privatekey.pem"],
                    "--port", assetContentsOr("port.txt", "8883"),
                    "--client_id", assetContentsOr("clientId.txt", "android-java-crt-test")))

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

                if (name == "pubsub.PubSub") {
                    args.addAll(arrayOf(
                        "--topic", assetContentsOr("topic.txt", "test/topic"),
                        "--message", assetContentsOr("message.txt", "Hello World From Android")))
                } else if (name in arrayOf("jobs.JobsSample", "shadow.ShadowSample")) {
                    args.addAll(arrayOf(
                        "--thing_name", assetContentsOr("thingName.txt", "aws-iot-unit-test")
                    ))
                }

                val main = sampleClass?.getMethod("main", Array<String>::class.java)

                try {
                    main?.invoke(null, args.toTypedArray())
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
