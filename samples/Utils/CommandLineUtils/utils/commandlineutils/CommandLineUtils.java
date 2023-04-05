/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package utils.commandlineutils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletableFuture;
import java.io.UnsupportedEncodingException;

import software.amazon.awssdk.crt.*;
import software.amazon.awssdk.crt.io.*;
import software.amazon.awssdk.crt.mqtt.*;
import software.amazon.awssdk.crt.mqtt5.*;
import software.amazon.awssdk.crt.mqtt5.packets.*;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;
import software.amazon.awssdk.crt.http.HttpProxyOptions;
import software.amazon.awssdk.crt.auth.credentials.X509CredentialsProvider;
import software.amazon.awssdk.crt.auth.credentials.CognitoCredentialsProvider;
import software.amazon.awssdk.crt.Log;
import software.amazon.awssdk.crt.Log.LogLevel;

public class CommandLineUtils {
    private String programName;
    private final HashMap<String, CommandLineOption> registeredCommands = new HashMap<>();
    private List<String> commandArguments;

    /**
     * Functions for registering and command line arguments
     */

    public void registerProgramName(String newProgramName) {
        programName = newProgramName;
    }

    public void registerCommand(CommandLineOption option) {
        if (registeredCommands.containsKey(option.commandName)) {
            System.out.println("Cannot register command: " + option.commandName + ". Command already registered");
            return;
        }
        registeredCommands.put(option.commandName, option);
    }

    public void registerCommand(String commandName, String exampleInput, String helpOutput) {
        registerCommand(new CommandLineOption(commandName, exampleInput, helpOutput));
    }

    public void removeCommand(String commandName) {
        registeredCommands.remove(commandName);
    }

    public void updateCommandHelp(String commandName, String newCommandHelp) {
        if (registeredCommands.containsKey(commandName)) {
            registeredCommands.get(commandName).helpOutput = newCommandHelp;
        }
    }

    public void sendArguments(String[] arguments) {
        // Automatically register the help command
        registerCommand(m_cmd_help, "", "Prints this message");

        commandArguments = Arrays.asList(arguments);

        // Automatically check for help and print if present
        if (hasCommand(m_cmd_help))
        {
            printHelp();
            System.exit(-1);
        }
    }

    public boolean hasCommand(String command) {
        return commandArguments.contains("--" + command);
    }

    public String getCommand(String command) {
        for (Iterator<String> iter = commandArguments.iterator(); iter.hasNext();) {
            String value = iter.next();
            if (Objects.equals(value,"--" + command)) {
                if (iter.hasNext()) {
                    return iter.next();
                }
                else {
                    System.out.println("Error - found command but at end of arguments!\n");
                    return "";
                }
            }
        }
        return "";
    }

    public String getCommandOrDefault(String command, String commandDefault) {
        if (commandArguments.contains("--" + command)) {
            return getCommand(command);
        }
        return commandDefault;
    }

    public String getCommandRequired(String command, String optionalAdditionalMessage) {
        if (commandArguments.contains("--" + command)) {
            return getCommand(command);
        }
        printHelp();
        System.out.println("Missing required argument: --" + command + "\n");
        if (!Objects.equals(optionalAdditionalMessage, "")) {
            System.out.println(optionalAdditionalMessage + "\n");
        }
        System.exit(-1);
        return "";
    }

    public void printHelp() {
        System.out.println("Usage:");

        String messageOne = programName;
        for (String commandName : registeredCommands.keySet()) {
            messageOne += " --" + commandName + " " + registeredCommands.get(commandName).exampleInput;
        }
        System.out.println(messageOne + "\n");

        for (String commandName : registeredCommands.keySet()) {
            messageOne += " --" + commandName + " " + registeredCommands.get(commandName).exampleInput;
            System.out.println("* " + commandName + "\t\t" + registeredCommands.get(commandName).helpOutput);
        }
    }

    /**
     * Helper functions for registering commands
     */

    public void addCommonMQTTCommands() {
        registerCommand(m_cmd_endpoint, "<str>", "The endpoint of the mqtt server, not including a port.");
        registerCommand(m_cmd_ca_file, "<path>", "Path to AmazonRootCA1.pem (optional, system trust store used by default).");
        registerCommand("verbosity", "<str>", "The amount of detail in the logging output of the sample." +
                        " Options: 'fatal', 'error', 'warn', 'info', 'debug', 'trace' or 'none' (optional, default='none').");
    }

    public void addCommonProxyCommands() {
        registerCommand(m_cmd_proxy_host, "<str>", "Websocket proxy host to use (optional, required if --proxy_port is set).");
        registerCommand(m_cmd_proxy_port, "<int>", "Websocket proxy port to use (optional, default=8080, required if --proxy_host is set).");
    }

    public void addCommonX509Commands()
    {
        registerCommand(
            m_cmd_x509_role, "<str>", "Role alias to use with the x509 credentials provider (required for x509)");
        registerCommand(m_cmd_x509_endpoint, "<str>", "The credentials endpoint to fetch x509 credentials from (required for x509)");
        registerCommand(
            m_cmd_x509_thing_name, "<str>", "Thing name to fetch x509 credentials on behalf of (required for x509)");
        registerCommand(
            m_cmd_x509_cert_file,
            "<path>",
            "Path to the IoT thing certificate used in fetching x509 credentials (required for x509)");
        registerCommand(
            m_cmd_x509_key_file,
            "<path>",
            "Path to the IoT thing private key used in fetching x509 credentials (required for x509)");
        registerCommand(
            m_cmd_x509_ca_file,
            "<path>",
            "Path to the root certificate used in fetching x509 credentials (required for x509)");
    }

    public void addCommonTopicMessageCommands()
    {
        registerCommand(m_cmd_message, "<str>", "The message to send in the payload (optional, default='Hello world!')");
        registerCommand(m_cmd_topic, "<str>", "Topic to publish, subscribe to. (optional, default='test/topic')");
    }

    public void addKeyAndCertCommands()
    {
        registerCommand(m_cmd_key_file, "<path>", "Path to your key in PEM format.");
        registerCommand(m_cmd_cert_file, "<path>", "Path to your client certificate in PEM format.");
    }

    /**
     * Functions to register commands on a per-sample basis, as well as getting a struct containing all the data
     */

    public class SampleCommandLineData
    {
        public String input_endpoint;
        public String input_cert;
        public String input_key;
        public String input_ca;
        public String input_clientId;
        public int input_port;
        public String input_proxyHost;
        public int input_proxyPort;
        public String input_topic;
        // PubSub
        public String input_message;
        public int input_messagesToPublish;
        // Websockets
        public String input_signingRegion;
        // Cognito
        public String input_cognitoIdentity;
        // Custom auth
        public String input_customAuthUsername;
        public String input_customAuthorizerName;
        public String input_customAuthorizerSignature;
        public String input_customAuthPassword;
        // Fleet provisioning
        public String input_templateName;
        public String input_templateParameters;
        public String input_csrPath;
        // Services (Shadow, Jobs, Greengrass, etc)
        public String input_thingName;
        public String input_mode;
        // Java Keystore
        public String input_keystore;
        public String input_keystorePassword;
        public String input_keystoreFormat;
        public String input_certificateAlias;
        public String input_certificatePassword;
    }

    public SampleCommandLineData parseSampleInputBasicConnect(String[] args)
    {
        addCommonMQTTCommands();
        addCommonProxyCommands();
        addKeyAndCertCommands();
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
        registerCommand(m_cmd_port, "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        returnData.input_endpoint = getCommandRequired(m_cmd_endpoint, "");
        returnData.input_cert = getCommandRequired(m_cmd_cert_file, "");
        returnData.input_key = getCommandRequired(m_cmd_key_file, "");
        returnData.input_ca = getCommandOrDefault(m_cmd_ca_file, "");
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_port = Integer.parseInt(getCommandOrDefault(m_cmd_port, "8883"));
        returnData.input_proxyHost = getCommandOrDefault(m_cmd_proxy_host, "");
        returnData.input_proxyPort = Integer.parseInt(getCommandOrDefault(m_cmd_proxy_port, "0"));
        return returnData;
    }

    public SampleCommandLineData parseSampleInputPubSub(String [] args)
    {
        addCommonMQTTCommands();
        addCommonTopicMessageCommands();
        addKeyAndCertCommands();
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
        registerCommand(m_cmd_port, "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        registerCommand(m_cmd_count, "<int>", "Number of messages to publish (optional, default='10').");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        returnData.input_endpoint = getCommandRequired(m_cmd_endpoint, "");
        returnData.input_cert = getCommandRequired(m_cmd_cert_file, "");
        returnData.input_key = getCommandRequired(m_cmd_key_file, "");
        returnData.input_ca = getCommandOrDefault(m_cmd_ca_file, "");
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_port = Integer.parseInt(getCommandOrDefault(m_cmd_port, "8883"));
        returnData.input_proxyHost = getCommandOrDefault(m_cmd_proxy_host, "");
        returnData.input_proxyPort = Integer.parseInt(getCommandOrDefault(m_cmd_proxy_port, "0"));
        returnData.input_topic = getCommandOrDefault(m_cmd_topic, "test/topic");
        returnData.input_message = getCommandOrDefault(m_cmd_message, "Hello World!");
        returnData.input_messagesToPublish = Integer.parseInt(getCommandOrDefault(m_cmd_count, "10"));
        return returnData;
    }

    public SampleCommandLineData parseSampleInputCognitoConnect(String [] args)
    {
        addCommonMQTTCommands();
        registerCommand(m_cmd_signing_region, "<str>", "AWS IoT service region.");
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
        registerCommand(m_cmd_cognito_identity, "<str>", "The Cognito identity ID to use to connect via Cognito");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        returnData.input_endpoint = getCommandRequired(m_cmd_endpoint, "");
        returnData.input_signingRegion = getCommandOrDefault(m_cmd_signing_region, "us-east-1");
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_cognitoIdentity = getCommandRequired(m_cmd_cognito_identity, "");
        return returnData;
    }

    public SampleCommandLineData parseSampleInputCustomAuthorizerConnect(String [] args)
    {
        addCommonMQTTCommands();
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
        registerCommand(m_cmd_custom_auth_username, "<str>", "Username for connecting to custom authorizer (optional, default=null).");
        registerCommand(m_cmd_custom_auth_authorizer_name, "<str>", "Name of custom authorizer (optional, default=null).");
        registerCommand(m_cmd_custom_auth_authorizer_signature, "<str>", "Signature passed when connecting to custom authorizer (optional, default=null).");
        registerCommand(m_cmd_custom_auth_password, "<str>", "Password for connecting to custom authorizer (optional, default=null).");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        returnData.input_endpoint = getCommandRequired(m_cmd_endpoint, "");
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_customAuthUsername = getCommandOrDefault(m_cmd_custom_auth_username, null);
        returnData.input_customAuthorizerName = getCommandOrDefault(m_cmd_custom_auth_authorizer_name, null);
        returnData.input_customAuthorizerSignature = getCommandOrDefault(m_cmd_custom_auth_authorizer_signature, null);
        returnData.input_customAuthPassword = getCommandOrDefault(m_cmd_custom_auth_password, null);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputCustomKeyOpsConnect(String [] args)
    {
        addCommonMQTTCommands();
        addKeyAndCertCommands();
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
        registerCommand(m_cmd_port, "<int>", "Port to connect to on the endpoint (optional, default='8883').");

        SampleCommandLineData returnData = new SampleCommandLineData();
        returnData.input_endpoint = getCommandRequired(m_cmd_endpoint, "");
        returnData.input_cert = getCommandRequired(m_cmd_cert_file, "");
        returnData.input_key = getCommandRequired(m_cmd_key_file, "");
        returnData.input_ca = getCommandOrDefault(m_cmd_ca_file, "");
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_port = Integer.parseInt(getCommandOrDefault(m_cmd_port, "8883"));
        return returnData;
    }

    public SampleCommandLineData parseSampleInputFleetProvisioning(String [] args)
    {
        addCommonMQTTCommands();
        addKeyAndCertCommands();
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
        registerCommand(m_cmd_port, "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        registerCommand(m_cmd_fleet_template_name, "<str>", "Provisioning template name.");
        registerCommand(m_cmd_fleet_template_parameters, "<json>", "Provisioning template parameters.");
        registerCommand(m_cmd_fleet_template_csr, "<path>", "Path to the CSR file (optional).");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        returnData.input_endpoint = getCommandRequired(m_cmd_endpoint, "");
        returnData.input_cert = getCommandRequired(m_cmd_cert_file, "");
        returnData.input_key = getCommandRequired(m_cmd_key_file, "");
        returnData.input_ca = getCommandOrDefault(m_cmd_ca_file, "");
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_port = Integer.parseInt(getCommandOrDefault(m_cmd_port, "8883"));
        returnData.input_templateName = getCommandRequired(m_cmd_fleet_template_name, "");
        returnData.input_templateParameters = getCommandRequired(m_cmd_fleet_template_parameters, "");
        returnData.input_csrPath = getCommandOrDefault(m_cmd_fleet_template_csr, null);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputGreengrassDiscovery(String [] args)
    {
        addCommonMQTTCommands();
        removeCommand(m_cmd_endpoint);
        addKeyAndCertCommands();
        registerCommand(m_cmd_signing_region, "<str>", "AWS IoT service region (optional, default='us-east-1').");
        registerCommand(m_cmd_thing_name, "<str>", "The name of the IoT thing.");
        registerCommand(m_cmd_topic, "<str>", "Topic to subscribe/publish to (optional, default='test/topic').");
        registerCommand(m_cmd_mode, "<str>", "Mode options: 'both', 'publish', or 'subscribe' (optional, default='both').");
        addCommonProxyCommands();
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        returnData.input_thingName = getCommandRequired(m_cmd_thing_name, "");
        returnData.input_signingRegion = getCommandOrDefault(m_cmd_signing_region, "us-east-1");
        returnData.input_ca = getCommandOrDefault(m_cmd_ca_file, null);
        returnData.input_cert = getCommandRequired(m_cmd_cert_file, "");
        returnData.input_key = getCommandRequired(m_cmd_key_file, "");
        returnData.input_proxyHost = getCommandOrDefault(m_cmd_proxy_host, "");
        returnData.input_proxyPort = Integer.parseInt(getCommandOrDefault(m_cmd_proxy_port, "0"));
        returnData.input_topic = getCommandOrDefault(m_cmd_topic, "test/topic");
        returnData.input_mode = getCommandOrDefault(m_cmd_mode, "Hello World!");
        return returnData;
    }

    public SampleCommandLineData parseSampleInputKeystoreConnect(String [] args)
    {
        addCommonMQTTCommands();
        addCommonProxyCommands();
        registerCommand(m_cmd_javakeystore_path, "<file>", "The path to the Java keystore to use");
        registerCommand(m_cmd_javakeystore_password, "<str>", "The password for the Java keystore");
        registerCommand(m_cmd_javakeystore_format, "<str>", "The format of the Java keystore (optional, default='PKCS12')");
        registerCommand(m_cmd_javakeystore_certificate, "<str>", "The certificate alias to use to access the key and certificate in the Java keystore");
        registerCommand(m_cmd_javakeystore_key_password, "<str>", "The password associated with the key and certificate in the Java keystore");
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
        registerCommand(m_cmd_port, "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        returnData.input_endpoint = getCommandRequired(m_cmd_endpoint, "");
        returnData.input_ca = getCommandOrDefault(m_cmd_ca_file, "");
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_port = Integer.parseInt(getCommandOrDefault(m_cmd_port, "8883"));
        returnData.input_proxyHost = getCommandOrDefault(m_cmd_proxy_host, "");
        returnData.input_proxyPort = Integer.parseInt(getCommandOrDefault(m_cmd_proxy_port, "0"));
        returnData.input_keystore = getCommandRequired(m_cmd_javakeystore_path, "");
        returnData.input_keystorePassword = getCommandRequired(m_cmd_javakeystore_password, "");
        returnData.input_keystoreFormat = getCommandOrDefault(m_cmd_javakeystore_format, "PKCS12");
        returnData.input_certificateAlias = getCommandRequired(m_cmd_javakeystore_certificate, "");
        returnData.input_certificatePassword = getCommandRequired(m_cmd_javakeystore_key_password, "");
        return returnData;
    }

    public SampleCommandLineData parseSampleInputJobs(String[] args)
    {
        addCommonMQTTCommands();
        addKeyAndCertCommands();
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
        registerCommand(m_cmd_port, "<int>", "Port to connect to on the endpoint (optional, default='8883').");
        registerCommand(m_cmd_thing_name, "<str>", "The name of the IoT thing.");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        returnData.input_endpoint = getCommandRequired(m_cmd_endpoint, "");
        returnData.input_cert = getCommandRequired(m_cmd_cert_file, "");
        returnData.input_key = getCommandRequired(m_cmd_key_file, "");
        returnData.input_ca = getCommandOrDefault(m_cmd_ca_file, "");
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_port = Integer.parseInt(getCommandOrDefault(m_cmd_port, "8883"));
        returnData.input_thingName = getCommandRequired(m_cmd_thing_name, "");
        return returnData;
    }

    // Constants for commonly used/needed commands
    private static final String m_cmd_endpoint = "endpoint";
    private static final String m_cmd_ca_file = "ca_file";
    private static final String m_cmd_cert_file = "cert";
    private static final String m_cmd_key_file = "key";
    private static final String m_cmd_client_id = "client_id";
    private static final String m_cmd_port = "port";
    private static final String m_cmd_proxy_host = "proxy_host";
    private static final String m_cmd_proxy_port = "proxy_port";
    private static final String m_cmd_signing_region = "signing_region";
    private static final String m_cmd_x509_endpoint = "x509_endpoint";
    private static final String m_cmd_x509_role = "x509_role_alias";
    private static final String m_cmd_x509_thing_name = "x509_thing_name";
    private static final String m_cmd_x509_cert_file = "x509_cert";
    private static final String m_cmd_x509_key_file = "x509_key";
    private static final String m_cmd_x509_ca_file = "x509_ca_file";
    private static final String m_cmd_pkcs11_lib = "pkcs11_lib";
    private static final String m_cmd_pkcs11_cert = "cert";
    private static final String m_cmd_pkcs11_pin = "ppin";
    private static final String m_cmd_pkcs11_token = "token_label";
    private static final String m_cmd_pkcs11_slot = "slot_id";
    private static final String m_cmd_pkcs11_key = "key_label";
    private static final String m_cmd_message = "message";
    private static final String m_cmd_topic = "topic";
    private static final String m_cmd_help = "help";
    private static final String m_cmd_custom_auth_username = "custom_auth_username";
    private static final String m_cmd_custom_auth_authorizer_name = "custom_auth_authorizer_name";
    private static final String m_cmd_custom_auth_authorizer_signature = "custom_auth_authorizer_signature";
    private static final String m_cmd_custom_auth_password = "custom_auth_password";
    private static final String m_cmd_javakeystore_path = "keystore";
    private static final String m_cmd_javakeystore_password = "keystore_password";
    private static final String m_cmd_javakeystore_format = "keystore_format";
    private static final String m_cmd_javakeystore_certificate = "certificate_alias";
    private static final String m_cmd_javakeystore_key_password = "certificate_password";
    private static final String m_cmd_cognito_identity = "cognito_identity";
    private static final String m_cmd_count = "count";
    private static final String m_cmd_fleet_template_name = "template_name";
    private static final String m_cmd_fleet_template_parameters = "template_parameters";
    private static final String m_cmd_fleet_template_csr = "csr";
    private static final String m_cmd_thing_name = "thing_name";
    private static final String m_cmd_mode = "mode";
}

class CommandLineOption {
    public String commandName;
    public String exampleInput;
    public String helpOutput;

    CommandLineOption(String name, String example, String help) {
        commandName = name;
        exampleInput = example;
        helpOutput = help;
    }
}
