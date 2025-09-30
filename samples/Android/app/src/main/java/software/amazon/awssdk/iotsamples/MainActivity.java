package software.amazon.awssdk.iotsamples;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.security.KeyChain;
import android.security.KeyChainAliasCallback;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Samples to load for the Android App
    private static final Map<String, String> SAMPLES = new LinkedHashMap<String, String>() {{
        put("Select a Sample",""); // empty default
        put("MQTT5 X509 Publish/Subscribe", "mqtt5x509.Mqtt5X509");
        put("KeyChain Publish/Subscribe", "androidkeychainpubsub.AndroidKeyChainPubSub");
        put("KeyChain Alias Permission", "load.privateKey");
    }};

    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());
    private final StreamTee stdout;
    private final StreamTee stderr;
    private TextView console;
    private Spinner sampleSelect;
    private Context context;
    Map<String, String> resourceMap = new HashMap<>();

    private interface LogCallback {
        void log(String message);
    }

    private class StreamTee extends PrintStream {
        private final OutputStream source;
        private final LogCallback logCallback;

        public StreamTee(OutputStream source, LogCallback logCallback) {
            super(source, true);
            this.source = source;
            this.logCallback = logCallback;
            if (source == System.out) {
                System.setOut(this);
            } else if (source == System.err) {
                System.setErr(this);
            }
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            try {
                source.write(buf, off, len);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "An exception occurred in StreamTee", e);
            }
            logCallback.log(new String(buf, off, len));
        }

        @Override
        public void write(byte[] b) {
            try {
                source.write(b);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "An exception occurred in StreamTee", e);
            }
            logCallback.log(new String(b));
        }
    }

    public MainActivity() {
        super();
        stdout = new StreamTee(System.out, this::writeToConsole);
        stderr = new StreamTee(System.err, this::writeToConsole);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        console = findViewById(R.id.console);
        console.setEnabled(false);

        sampleSelect = findViewById(R.id.sampleSelect);
        ArrayAdapter<String> samplesAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            SAMPLES.keySet().toArray(new String[0])
        );
        samplesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sampleSelect.setAdapter(samplesAdapter);
        sampleSelect.setOnItemSelectedListener(this);

        

        loadAssets();
        context = this;
    }

    private void clearConsole() {
        runOnUiThread(() -> console.setText(""));
    }

    private void writeToConsole(String message) {
        runOnUiThread(() -> console.append(message));
    }

    private void onSampleComplete() {
        runOnUiThread(() -> {
            writeToConsole("\nSample Complete\n");
            sampleSelect.setSelection(0);
            sampleSelect.setEnabled(true);
        });
    }

    private String assetContents(String assetName) throws IOException {
        try (InputStream res = getResources().getAssets().open(assetName)) {
            byte[] bytes = new byte[res.available()];
            res.read(bytes);
            return new String(bytes).trim();
        } catch (IOException e) {
            throw new IOException("Error reading asset file: " + assetName, e);
        }
    }

    // Load files from assets folder for use into resourceMap
    private void loadAssets(){

        writeToConsole("Loading Asset Files:\n");
        // Sample asset files in the assets folder
        List<String> resourceNames = new ArrayList<>();
        resourceNames.add("endpoint.txt");
        resourceNames.add("privatekey.pem");
        resourceNames.add("certificate.pem");
        resourceNames.add("keychainAlias.txt");
        resourceNames.add("clientId.txt");
        resourceNames.add("topic.txt");
        resourceNames.add("message.txt");
        resourceNames.add("count.txt");

        // Copy to cache and store file locations for file assets and contents for .txt assets
        for (String resourceName : resourceNames) {
            try {
                try (InputStream res = getResources().getAssets().open(resourceName)) {
                    // .txt files will store contents of the file
                    if(resourceName.endsWith(".txt")){
                        byte[] bytes = new byte[res.available()];
                        res.read(bytes);
                        String contents = new String(bytes).trim();
                        resourceMap.put(resourceName, contents);
                        writeToConsole("'" + resourceName + "' file found and contents copied\n");
                    } else {
                        // non .txt file types will copy to cache and store accessible file location
                        String cachedName = getExternalCacheDir() + "/" + resourceName;
                        try (OutputStream cachedRes = new FileOutputStream(cachedName)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = res.read(buffer)) != -1) {
                                cachedRes.write(buffer, 0, length);
                            }
                        }
                        resourceMap.put(resourceName, cachedName);
                        writeToConsole("'" + resourceName + "' file found and cached\n");
                    }
                }
            } catch (IOException e) {
                writeToConsole("'" + resourceName + "' file not found\n");
            }
        }
    }

    // Set a required argument from loaded assets
    private boolean argSetRequired(String argName, String fileName, List<String> args){
        if(resourceMap.containsKey(fileName)){
            args.addAll(Arrays.asList(argName, resourceMap.get(fileName)));
            return true;
        }
        writeToConsole("Required argument '" + argName + "' needs to be set. '" + fileName + "' File missing from assets folder\n");
        return false;
    }

    // Check for optional argument and set if it's available
    private void argSetOptional(String argName, String fileName, List<String>args){
        if(resourceMap.containsKey(fileName)){
            args.addAll(Arrays.asList(argName, resourceMap.get(fileName)));
        }
    }

    // Retreive sample specific arguments needed from loaded asset files
    private String[] sampleArgs(String sampleClassName){
        List<String> args = new ArrayList<>();

        // Every sample requires the endpoint argument
        if (!argSetRequired("--endpoint", "endpoint.txt", args)){
            return null;
        }

        // Shared optional arguments
        argSetOptional("--client_id", "clientId.txt", args);

        // Missing required arguments will return null
        switch(sampleClassName){
            case "mqtt5x509.Mqtt5X509":
                if (!argSetRequired("--cert", "certificate.pem", args) ||
                    !argSetRequired("--key", "privatekey.pem", args)) {
                    return null;
                }
                argSetOptional("--topic", "topic.txt", args);
                argSetOptional("--message", "message.txt", args);
                argSetOptional("--count", "count.txt", args);
                break;

            case "androidkeychainpubsub.AndroidKeyChainPubSub":
                if (!argSetRequired("--keychain_alias", "keychainAlias.txt", args)) {
                    return null;
                }
                argSetOptional("--topic", "topic.txt", args);
                argSetOptional("--message", "message.txt", args);
                argSetOptional("--count", "count.txt", args);
            break;
        }
        writeToConsole(" with Arguments\n");
        for (String str : args){
            if(str.contains("--")){
             writeToConsole(str + ":'");
            } else {
                writeToConsole(str + "'\n");
            }
        }
        writeToConsole("\n");
        return args.toArray(new String[0]);
    }

    public class SampleRunnable implements Runnable {
        private final String[] args;
        private final Method sampleMain;
        private final Context context;

        public SampleRunnable(String[] args, Method sampleMain){
            this.args = args;
            this.sampleMain = sampleMain;
            this.context = null;
        }

        public SampleRunnable(String[] args, Method sampleMain, Context context){
            this.args = args;
            this.sampleMain = sampleMain;
            this.context = context;
        }

        @Override
        public void run(){
            try {
                if(context != null){
                    sampleMain.invoke(null, (Object) args, (Object) context);
                } else {
                    sampleMain.invoke(null, (Object) args);
                }
            } catch (Exception e){
                writeToConsole("Exception occurred in run(): " + e.toString() +
                "\nCause: " + e.getCause().toString());
            }
            onSampleComplete();
        }
    }

    private void runSample(String sampleName, String sampleClassName){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> sampleClass = null;

        // Get Permission to Load KeyChain PrivateKey
        if (sampleClassName.contains("load.")){
            sampleSelect.setEnabled(false);
            String keyChainAlias = resourceMap.get("keychainAlias.txt");

            KeyChain.choosePrivateKeyAlias(this, new KeyChainAliasCallback(){
                @Override
                public void alias(String chosenAlias){
                    if (chosenAlias != null){
                        writeToConsole("KeyChain alias '" + chosenAlias + "' chosen.\n");
                    }
                    else {
                        writeToConsole("PrivateKey not loaded.\n");
                    }
                    onSampleComplete();
                }
            }, null,null,null,keyChainAlias);

            writeToConsole("Requesting KeyChain Permission\n");
            return;
        }

        try {
            sampleClass = classLoader.loadClass(sampleClassName);
        } catch (ClassNotFoundException e){
            writeToConsole("Cound not find sample '" + sampleClassName + "'\n");
            return;
        }

        if(sampleClass != null){
            Method main = null;
            try {
                if (sampleClassName == "androidkeychainpubsub.AndroidKeyChainPubSub"){
                    main = sampleClass.getMethod("main", String[].class, Context.class);
                    if (main != null){
                        sampleSelect.setEnabled(false);
                        writeToConsole("Running '" + sampleName + "'");

                        boolean isECC = sampleName.contains("ECC");
                        String[] args = sampleArgs(sampleClassName);
                        if (args == null){
                            writeToConsole("\nMissing required arguments/files\n");
                            onSampleComplete();
                            return;
                        }
                        new Thread(new SampleRunnable(args, main, context), "sample_runner").start();
                    }
                } else {
                    main = sampleClass.getMethod("main", String[].class);
                    if (main != null){
                        sampleSelect.setEnabled(false);
                        writeToConsole("Running '" + sampleName + "''\n\n");
                        String[] args = sampleArgs(sampleClassName);
                        if (args == null){
                            writeToConsole("\nMissing required arguments/files\n");
                            onSampleComplete();
                            return;
                        }
                        new Thread(new SampleRunnable(args, main), "sample_runner").start();
                    }
                }
            } catch (Exception e) {
                writeToConsole("Exception encountered: " + e.toString());
                onSampleComplete();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String sampleName = parent.getItemAtPosition(pos).toString();
            if (sampleName != "Select a Sample") {
                clearConsole();
                String sampleClassName = SAMPLES.get(sampleName);
                if (sampleClassName != null) {
                    runSample(sampleName, sampleClassName);
                }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        clearConsole();
        writeToConsole("Please select a sample above");
    }
}
