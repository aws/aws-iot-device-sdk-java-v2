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
    "pubsub.PubSub" to "Publish/Subscribe Sample",
    "jobs.Jobs" to "Jobs Client Sample",
    "shadow.Shadow" to "Shadow Client Sample",
    "pubsubstress.PubSubStress" to "Publish/Subscribe Load Test"
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

        val samples = SAMPLES.values.toMutableList()
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
        val sampleClass = classLoader.loadClass(name);
        if (sampleClass == null) {
            clearConsole()
            writeToConsole("Could not find sample '${name}'")
        }

        thread(name="sample_runner", contextClassLoader = classLoader) {
            // find resources, copy them into the cache so samples can get paths to them
            val resourceNames = listOf("AmazonRootCA1.pem", "ca-certificates.crt", "certificate.pem", "privatekey.pem")
            val resourceMap = HashMap<String, String>()
            for (resourceName in resourceNames) {
                resources.assets.open(resourceName).use { res ->
                    val cachedName = "${externalCacheDir}/${resourceName}"
                    FileOutputStream(cachedName).use { cachedRes ->
                        res.copyTo(cachedRes)
                    }

                    resourceMap[resourceName] = cachedName
                }
            }

            var endpoint: String = ""
            resources.assets.open("endpoint.txt").use {res ->
                val bytes = ByteArray(res.available())
                res.read(bytes)
                endpoint = String(bytes).trim()
            }

            val args = arrayOf(
                "--endpoint", assetContents("endpoint.txt"),
                "--rootca", resourceMap["AmazonRootCA1.pem"],
                "--cert", resourceMap["certificate.pem"],
                "--key", resourceMap["privatekey.pem"],
                "--port", assetContentsOr("port.txt", "8883"),
                "--clientId", assetContentsOr("clientId.txt", "android-java-crt-test"),
                "--topic", assetContentsOr("topic.txt", "/samples/test"),
                "--message", assetContentsOr("message.txt", "Hello World From Android")
            )
            val main = sampleClass.getMethod("main", Array<String>::class.java)
            try {
                main.invoke(null, args);
            } catch (e: Exception) {
                writeToConsole(e.toString())
            }
            onSampleComplete();
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        clearConsole()
        writeToConsole("Please select a sample above")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        val sampleName = parent?.getItemAtPosition(pos).toString()
        for (sample in SAMPLES) {
            if (sample.value == sampleName) {
                return runSample(sample.key)
            }
        }
    }
}
