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
    private boolean isCI;

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
            if (isCI == true) {
                throw new RuntimeException("Help argument called");
            } else {
                System.exit(-1);
            }
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

    public String getCommandRequired(String command) {
        if (commandArguments.contains("--" + command)) {
            return getCommand(command);
        }
        printHelp();
        System.out.println("Missing required argument: --" + command + "\n");

        if (isCI == true) {
            throw new RuntimeException("Missing required argument");
        } else {
            System.exit(-1);
        }
        return "";
    }

    public String getCommandRequired(String command, String commandAlt){
        if(commandArguments.contains("--" + commandAlt)){
            return getCommand(commandAlt);
        }
        return getCommandRequired(command);
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

    public void determineIfCI() {
        String ciPropValue = System.getProperty("aws.crt.ci");
        isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);
    }

    /**
     * Helper functions for registering commands
     */

    public void addCommonLoggingCommands() {
        registerCommand(m_cmd_verbosity, "<str>", "The amount of detail in the logging output of the sample." +
                        " Options: 'Fatal', 'Error', 'Warn', 'Info', 'Debug', 'Trace' or 'None' (optional, default='None').");
        registerCommand(m_cmd_log_destination, "<str>", "Where logging should be routed to." +
                        " Options: 'Stdout', 'Stderr', 'File' (optional, default='Stderr').");
        registerCommand(m_cmd_log_file_name, "<str>", "File name to save logging to." +
                        " (optional, default='log.txt').");
    }

    public void addClientIdAndPort() {
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
        registerCommand(m_cmd_port, "<int>", "Port to connect to on the endpoint (optional, default='8883').");
    }

    public void addCommonMQTTCommands() {
        registerCommand(m_cmd_endpoint, "<str>", "The endpoint of the mqtt server, not including a port.");
        registerCommand(m_cmd_ca_file, "<path>", "Path to AmazonRootCA1.pem (optional, system trust store used by default).");
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
        registerCommand(m_cmd_count, "<int>", "Number of messages to publish (optional, default='10').");
    }

    public void addKeyAndCertCommands()
    {
        registerCommand(m_cmd_key_file, "<path>", "Path to your key in PEM format.");
        registerCommand(m_cmd_cert_file, "<path>", "Path to your client certificate in PEM format.");
    }

    public void addAndroidKeyChainCommands(){
        registerCommand(m_cmd_endpoint, "<str>", "The endpoint of the mqtt server, not including a port.");
        registerCommand(m_cmd_android_keychain_alias, "<str>", "Alias of Private Key and Certificate to access from Android KeyChain.");
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
    }

    /**
     * Helper functions for parsing commands
     */

    private void parseCommonLoggingCommands(SampleCommandLineData returnData){
        String verbosity = getCommandOrDefault(m_cmd_verbosity, "None");
        String log_destination = getCommandOrDefault(m_cmd_log_destination, "Stderr");
        String log_file_name = getCommandOrDefault(m_cmd_log_file_name, "log.txt");

        if(verbosity != "None"){
            switch (log_destination) {
                case "Stderr":
                    Log.initLoggingToStderr(LogLevel.valueOf(verbosity));
                    break;
                case "Stdout":
                    Log.initLoggingToStdout(LogLevel.valueOf(verbosity));
                    break;
                case "File":
                    Log.initLoggingToFile(LogLevel.valueOf(verbosity), log_file_name);
                    break;
                default:
                    break;
            }
        }
    }

    private void parseMqttVersion(SampleCommandLineData returnData) {
        returnData.input_mqtt_version = Integer.parseInt(getCommandOrDefault(m_cmd_mqtt_version, "3"));
    }

    private void parseCommonMQTTCommands(SampleCommandLineData returnData) {
        returnData.input_endpoint = getCommandRequired(m_cmd_endpoint);
        returnData.input_ca = getCommandOrDefault(m_cmd_ca_file, "");
    }

    private void parseKeyAndCertCommandsRequired(SampleCommandLineData returnData)
    {
        returnData.input_cert = getCommandRequired(m_cmd_cert_file);
        returnData.input_key = getCommandRequired(m_cmd_key_file);
    }

    private void parseKeyAndCertCommandsOptional(SampleCommandLineData returnData)
    {
        returnData.input_cert = getCommandOrDefault(m_cmd_cert_file, "");
        returnData.input_key = getCommandOrDefault(m_cmd_key_file, "");
    }

    private void parseClientIdAndPort(SampleCommandLineData returnData) {
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_port = Integer.parseInt(getCommandOrDefault(m_cmd_port, "8883"));
    }

    private void parseCommonTopicMessageCommands(SampleCommandLineData returnData)
    {
        if (isCI == true) {
            returnData.input_topic = getCommandOrDefault(m_cmd_topic, "test/topic/" + UUID.randomUUID().toString());
            returnData.input_message = getCommandOrDefault(m_cmd_message, "Hello World!");
        } else {
            returnData.input_topic = getCommandOrDefault(m_cmd_topic, "test/topic");
            returnData.input_message = getCommandOrDefault(m_cmd_message, "Hello World!");
        }
        returnData.input_count = Integer.parseInt(getCommandOrDefault(m_cmd_count, "10"));
    }

    private void parseCommonProxyCommands(SampleCommandLineData returnData) {
        returnData.input_proxyHost = getCommandOrDefault(m_cmd_proxy_host, "");
        returnData.input_proxyPort = Integer.parseInt(getCommandOrDefault(m_cmd_proxy_port, "0"));
    }

    private void parseCommonX509Commands(SampleCommandLineData returnData)
    {
        returnData.input_x509Endpoint = getCommandRequired(m_cmd_x509_endpoint);
        returnData.input_x509Role = getCommandRequired(m_cmd_x509_role);
        returnData.input_x509ThingName = getCommandRequired(m_cmd_x509_thing_name);
        returnData.input_x509Cert = getCommandRequired(m_cmd_x509_cert_file);
        returnData.input_x509Key = getCommandRequired(m_cmd_x509_key_file);
        returnData.input_x509Ca = getCommandOrDefault(m_cmd_x509_ca_file, null);
    }

    private void parseAndroidKeyChainCommands(SampleCommandLineData returnData)
    {
        returnData.input_endpoint = getCommandRequired(m_cmd_endpoint);
        returnData.input_KeyChainAlias = getCommandRequired(m_cmd_android_keychain_alias);
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
    }

    /**
     * Functions to register commands on a per-sample basis, as well as getting a struct containing all the data
     */

    public class SampleCommandLineData
    {
        // General use
        public String input_endpoint;
        public String input_cert;
        public String input_key;
        public String input_ca;
        public String input_clientId;
        public int input_port;
        public int input_mqtt_version;
        // Proxy
        public String input_proxyHost;
        public int input_proxyPort;
        // PubSub
        public String input_topic;
        public String input_message;
        public int input_count;
        // Websockets
        public String input_signingRegion;
        // Cognito
        public String input_cognitoIdentity;
        // Custom auth
        public String input_customAuthUsername;
        public String input_customAuthorizerName;
        public String input_customAuthorizerSignature;
        public String input_customAuthPassword;
        public String input_customAuthorizerTokenKeyName;
        public String input_customAuthorizerTokenValue;
        // Fleet provisioning
        public String input_templateName;
        public String input_templateParameters;
        public String input_csrPath;
        // Services (Shadow, Jobs, Greengrass, etc)
        public String input_thingName;
        public String input_mode;
        // Shadow specifics
        public String input_shadowProperty;
        public String input_shadowValue;
        public String input_shadowName;
        // Java Keystore
        public String input_keystore;
        public String input_keystorePassword;
        public String input_keystoreFormat;
        public String input_certificateAlias;
        public String input_certificatePassword;
        // Shared Subscription
        public String input_groupIdentifier;
        // PKCS#11
        public String input_pkcs11LibPath;
        public String input_pkcs11UserPin;
        public String input_pkcs11TokenLabel;
        public Long input_pkcs11SlotId;
        public String input_pkcs11KeyLabel;
        // Raw Connect
        public String input_username;
        public String input_password;
        public String input_protocolName;
        public List<String> input_authParams;
        // X509
        public String input_x509Endpoint;
        public String input_x509Role;
        public String input_x509ThingName;
        public String input_x509Cert;
        public String input_x509Key;
        public String input_x509Ca;
        // PKCS12
        public String input_pkcs12File;
        public String input_pkcs12Password;
        // Greengrass Basic Discovery
        public Boolean inputPrintDiscoverRespOnly;
        // Android KeyChain
        public String input_KeyChainAlias;
    }

    public SampleCommandLineData parseSampleInputBasicConnect(String[] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addCommonProxyCommands();
        addKeyAndCertCommands();
        addClientIdAndPort();
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseCommonProxyCommands(returnData);
        parseKeyAndCertCommandsRequired(returnData);
        parseClientIdAndPort(returnData);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputPubSub(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addCommonTopicMessageCommands();
        addKeyAndCertCommands();
        addCommonProxyCommands();
        addClientIdAndPort();
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseKeyAndCertCommandsRequired(returnData);
        parseCommonTopicMessageCommands(returnData);
        parseCommonProxyCommands(returnData);
        parseClientIdAndPort(returnData);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputCognitoConnect(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        registerCommand(m_cmd_signing_region, "<str>", "AWS IoT service region.");
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
        registerCommand(m_cmd_cognito_identity, "<str>", "The Cognito identity ID to use to connect via Cognito");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        returnData.input_signingRegion = getCommandRequired(m_cmd_signing_region, m_cmd_region);
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_cognitoIdentity = getCommandRequired(m_cmd_cognito_identity);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputCustomAuthorizerConnect(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
        registerCommand(m_cmd_custom_auth_username, "<str>", "Username for connecting to custom authorizer (optional, default=null).");
        registerCommand(m_cmd_custom_auth_authorizer_name, "<str>", "Name of custom authorizer (optional, default=null).");
        registerCommand(m_cmd_custom_auth_authorizer_signature, "<str>", "Signature passed when connecting to custom authorizer (optional, default=null).");
        registerCommand(m_cmd_custom_auth_password, "<str>", "Password for connecting to custom authorizer (optional, default=null).");
        registerCommand(m_cmd_custom_auth_token_key_name, "<str>", "Key used to extract the custom authorizer token (optional, default=null).");
        registerCommand(m_cmd_custom_auth_token_value, "<str>", "The opaque token value for the custom authorizer (optional, default=null).");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_customAuthUsername = getCommandOrDefault(m_cmd_custom_auth_username, null);
        returnData.input_customAuthorizerName = getCommandOrDefault(m_cmd_custom_auth_authorizer_name, null);
        returnData.input_customAuthorizerSignature = getCommandOrDefault(m_cmd_custom_auth_authorizer_signature, null);
        returnData.input_customAuthPassword = getCommandOrDefault(m_cmd_custom_auth_password, null);
        returnData.input_customAuthorizerTokenKeyName = getCommandOrDefault(m_cmd_custom_auth_token_key_name, null);
        returnData.input_customAuthorizerTokenValue = getCommandOrDefault(m_cmd_custom_auth_token_value, null);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputCustomKeyOpsConnect(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addKeyAndCertCommands();
        addClientIdAndPort();
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseKeyAndCertCommandsRequired(returnData);
        parseClientIdAndPort(returnData);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputAndroidKeyChainPubSub(String [] args)
    {
        addCommonLoggingCommands();
        addAndroidKeyChainCommands();
        addCommonTopicMessageCommands();
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseAndroidKeyChainCommands(returnData);
        parseCommonTopicMessageCommands(returnData);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputFleetProvisioning(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addKeyAndCertCommands();
        addClientIdAndPort();
        registerCommand(m_cmd_fleet_template_name, "<str>", "Provisioning template name.");
        registerCommand(m_cmd_fleet_template_parameters, "<json>", "Provisioning template parameters.");
        registerCommand(m_cmd_fleet_template_csr, "<path>", "Path to the CSR file (optional).");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseMqttVersion(returnData);
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseKeyAndCertCommandsRequired(returnData);
        parseClientIdAndPort(returnData);
        returnData.input_templateName = getCommandRequired(m_cmd_fleet_template_name);
        returnData.input_templateParameters = getCommandRequired(m_cmd_fleet_template_parameters);
        returnData.input_csrPath = getCommandOrDefault(m_cmd_fleet_template_csr, null);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputGreengrassDiscovery(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        removeCommand(m_cmd_endpoint);
        addKeyAndCertCommands();
        addCommonProxyCommands();
        registerCommand(m_cmd_region, "<str>", "AWS IoT service region (optional, default='us-east-1').");
        registerCommand(m_cmd_thing_name, "<str>", "The name of the IoT thing.");
        registerCommand(m_cmd_topic, "<str>", "Topic to subscribe/publish to (optional, default='test/topic').");
        registerCommand(m_cmd_mode, "<str>", "Mode options: 'both', 'publish', or 'subscribe' (optional, default='both').");
        registerCommand(m_cmd_print_discover_resp_only, "<str>", "Exists the sample after printing the discovery result (optional, default='False')");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseKeyAndCertCommandsRequired(returnData);
        returnData.input_ca = getCommandOrDefault(m_cmd_ca_file, null);
        returnData.input_thingName = getCommandRequired(m_cmd_thing_name);
        returnData.input_signingRegion = getCommandRequired(m_cmd_region, m_cmd_signing_region);
        returnData.input_topic = getCommandOrDefault(m_cmd_topic, "test/topic");
        returnData.input_mode = getCommandOrDefault(m_cmd_mode, "Hello World!");
        returnData.input_proxyHost = getCommandOrDefault(m_cmd_proxy_host, "");
        returnData.input_proxyPort = Integer.parseInt(getCommandOrDefault(m_cmd_proxy_port, "0"));
        returnData.inputPrintDiscoverRespOnly = hasCommand(m_cmd_print_discover_resp_only);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputKeystoreConnect(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addCommonProxyCommands();
        addClientIdAndPort();
        registerCommand(m_cmd_javakeystore_path, "<file>", "The path to the Java keystore to use");
        registerCommand(m_cmd_javakeystore_password, "<str>", "The password for the Java keystore");
        registerCommand(m_cmd_javakeystore_format, "<str>", "The format of the Java keystore (optional, default='PKCS12')");
        registerCommand(m_cmd_javakeystore_certificate, "<str>", "The certificate alias to use to access the key and certificate in the Java keystore");
        registerCommand(m_cmd_javakeystore_key_password, "<str>", "The password associated with the key and certificate in the Java keystore");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseCommonProxyCommands(returnData);
        parseClientIdAndPort(returnData);
        returnData.input_keystore = getCommandRequired(m_cmd_javakeystore_path);
        returnData.input_keystorePassword = getCommandRequired(m_cmd_javakeystore_password);
        returnData.input_keystoreFormat = getCommandOrDefault(m_cmd_javakeystore_format, "PKCS12");
        returnData.input_certificateAlias = getCommandRequired(m_cmd_javakeystore_certificate);
        returnData.input_certificatePassword = getCommandRequired(m_cmd_javakeystore_key_password);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputJobs(String[] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addKeyAndCertCommands();
        addClientIdAndPort();
        registerCommand(m_cmd_thing_name, "<str>", "The name of the IoT thing.");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseMqttVersion(returnData);
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseKeyAndCertCommandsRequired(returnData);
        parseClientIdAndPort(returnData);
        returnData.input_thingName = getCommandRequired(m_cmd_thing_name);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputMqtt5PubSub(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addCommonTopicMessageCommands();
        addKeyAndCertCommands();
        addCommonProxyCommands();
        addClientIdAndPort();
        registerCommand(m_cmd_signing_region, "<string>", "Websocket region to use (will use websockets to connect if defined).");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseCommonTopicMessageCommands(returnData);
        parseKeyAndCertCommandsOptional(returnData);
        parseCommonProxyCommands(returnData);
        parseClientIdAndPort(returnData);
        returnData.input_signingRegion = getCommandOrDefault(m_cmd_signing_region, null);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputMqtt5SharedSubscription(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addCommonTopicMessageCommands();
        addKeyAndCertCommands();
        addCommonProxyCommands();
        addClientIdAndPort();
        registerCommand(m_cmd_group_identifier, "<string>", "The group identifier to use in the shared subscription (optional, default='java-sample')");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseCommonTopicMessageCommands(returnData);
        parseKeyAndCertCommandsRequired(returnData);
        parseCommonProxyCommands(returnData);
        parseClientIdAndPort(returnData);
        returnData.input_groupIdentifier = getCommandOrDefault(m_cmd_group_identifier, "java-sample");
        return returnData;
    }

    public SampleCommandLineData parseSampleInputPkcs11Connect(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addClientIdAndPort();
        registerCommand(m_cmd_cert_file, "<path>", "Path to your client certificate in PEM format.");
        registerCommand(m_cmd_pkcs11_lib, "<path>", "Path to PKCS#11 library.");
        registerCommand(m_cmd_pkcs11_pin, "<int>", "User PIN for logging into PKCS#11 token.");
        registerCommand(m_cmd_pkcs11_token, "<str>", "Label of PKCS#11 token to use (optional).");
        registerCommand(m_cmd_pkcs11_slot, "<int>", "Slot ID containing PKCS#11 token to use (optional).");
        registerCommand(m_cmd_pkcs11_key, "<str>", "Label of private key on the PKCS#11 token (optional).");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseClientIdAndPort(returnData);
        returnData.input_cert = getCommandRequired(m_cmd_cert_file);
        returnData.input_pkcs11LibPath = getCommandRequired(m_cmd_pkcs11_lib);
        returnData.input_pkcs11UserPin = getCommandRequired(m_cmd_pkcs11_pin);
        returnData.input_pkcs11TokenLabel = getCommandOrDefault(m_cmd_pkcs11_token, "");
        returnData.input_pkcs11SlotId = null;
        if (hasCommand(m_cmd_pkcs11_slot)) {
            returnData.input_pkcs11SlotId = Long.parseLong(getCommandOrDefault(m_cmd_pkcs11_slot, "-1"));
        }
        returnData.input_pkcs11KeyLabel = getCommandOrDefault(m_cmd_pkcs11_key, "");
        return returnData;
    }

    public SampleCommandLineData parseSampleInputShadow(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addKeyAndCertCommands();
        addClientIdAndPort();
        registerCommand(m_cmd_thing_name, "<str>", "The name of the IoT thing.");
        registerCommand(m_cmd_shadow_property, "<str>", "The property in Shadow to update.");
        registerCommand(m_cmd_shadow_value, "<str>", "The value for Shadow property.");
        registerCommand(m_cmd_shadow_name, "<str>", "The name of Named Shadow.");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseMqttVersion(returnData);
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseKeyAndCertCommandsRequired(returnData);
        parseClientIdAndPort(returnData);
        returnData.input_thingName = getCommandRequired(m_cmd_thing_name);
        returnData.input_shadowProperty = getCommand(m_cmd_shadow_property);
        returnData.input_shadowValue = getCommand(m_cmd_shadow_value);
        returnData.input_shadowName = getCommand(m_cmd_shadow_name);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputWebsocketConnect(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addCommonProxyCommands();
        registerCommand(m_cmd_signing_region, "<str>", "AWS IoT service region.");
        registerCommand(m_cmd_client_id, "<int>", "Client id to use (optional, default='test-*').");
        registerCommand(m_cmd_port, "<int>", "Port to connect to on the endpoint (optional, default='443').");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseCommonProxyCommands(returnData);
        returnData.input_signingRegion = getCommandRequired(m_cmd_signing_region, m_cmd_region);
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_port = Integer.parseInt(getCommandOrDefault(m_cmd_port, "443"));
        return returnData;
    }

    public SampleCommandLineData parseSampleInputWindowsCertConnect(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addClientIdAndPort();
        registerCommand(m_cmd_cert_file, "<str>", "Path to certificate in Windows cert store. " +
                                                  "e.g. \"CurrentUser\\MY\\6ac133ac58f0a88b83e9c794eba156a98da39b4c\"");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseClientIdAndPort(returnData);
        returnData.input_cert = getCommandRequired(m_cmd_cert_file);
        return returnData;
    }

    public SampleCommandLineData parseSampleInputX509Connect(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addCommonProxyCommands();
        addCommonX509Commands();
        addClientIdAndPort();
        registerCommand(m_cmd_signing_region, "<str>", "AWS IoT service region.");
        sendArguments(args);

        /**
         * Gather the input from the command line
         */
        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseCommonProxyCommands(returnData);
        parseCommonX509Commands(returnData);
        returnData.input_signingRegion = getCommandRequired(m_cmd_signing_region, m_cmd_region);
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_port = Integer.parseInt(getCommandOrDefault(m_cmd_port, "443"));
        return returnData;
    }

    public SampleCommandLineData parseSampleInputPkcs12Connect(String[] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addClientIdAndPort();
        registerCommand(m_cmd_pkcs12_file, "<path>", "Path to your client PKCS12 certificate.");
        registerCommand(m_cmd_pkcs12_password, "<path>", "Path to your client certificate in PEM format.");
        sendArguments(args);

        SampleCommandLineData returnData = new SampleCommandLineData();
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseClientIdAndPort(returnData);
        returnData.input_pkcs12File = getCommandRequired(m_cmd_pkcs12_file);
        returnData.input_pkcs12Password = getCommandRequired(m_cmd_pkcs12_password);
        return returnData;
    }

    /**
     * Based on the sample string: sets up the arguments, parses the arguments, and returns the command line data all in one go
     */
    public static SampleCommandLineData getInputForIoTSample(String sampleName, String[] args)
    {
        CommandLineUtils cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName(sampleName);
        cmdUtils.determineIfCI();

        if (sampleName.equals("BasicConnect")) {
            return cmdUtils.parseSampleInputBasicConnect(args);
        } else if (sampleName.equals("PubSub")) {
            return cmdUtils.parseSampleInputPubSub(args);
        } else if (sampleName.equals("CognitoConnect")) {
            return cmdUtils.parseSampleInputCognitoConnect(args);
        } else if (sampleName.equals("CustomAuthorizerConnect")) {
            return cmdUtils.parseSampleInputCustomAuthorizerConnect(args);
        } else if (sampleName.equals("CustomKeyOpsConnect")) {
            return cmdUtils.parseSampleInputCustomKeyOpsConnect(args);
        } else if (sampleName.equals("AndroidKeyChainPubSub")) {
            return cmdUtils.parseSampleInputAndroidKeyChainPubSub(args);
        } else if (sampleName.equals("FleetProvisioningSample")) {
            return cmdUtils.parseSampleInputFleetProvisioning(args);
        } else if (sampleName.equals("BasicDiscovery")) {
            return cmdUtils.parseSampleInputGreengrassDiscovery(args);
        } else if (sampleName.equals("JavaKeystoreConnect")) {
            return cmdUtils.parseSampleInputKeystoreConnect(args);
        } else if (sampleName.equals("Jobs")) {
            return cmdUtils.parseSampleInputJobs(args);
        } else if (sampleName.equals("Mqtt5PubSub")) {
            return cmdUtils.parseSampleInputMqtt5PubSub(args);
        } else if (sampleName.equals("Mqtt5SharedSubscription")) {
            return cmdUtils.parseSampleInputMqtt5SharedSubscription(args);
        } else if (sampleName.equals("Pkcs11Connect")) {
            return cmdUtils.parseSampleInputPkcs11Connect(args);
        } else if (sampleName.equals("Shadow")) {
            return cmdUtils.parseSampleInputShadow(args);
        } else if (sampleName.equals("WebsocketConnect")) {
            return cmdUtils.parseSampleInputWebsocketConnect(args);
        } else if (sampleName.equals("WindowsCertConnect")) {
            return cmdUtils.parseSampleInputWindowsCertConnect(args);
        } else if (sampleName.equals("x509CredentialsProviderConnect")) {
            return cmdUtils.parseSampleInputX509Connect(args);
        } else if (sampleName.equals("Pkcs12Connect")) {
            return cmdUtils.parseSampleInputPkcs12Connect(args);
        } else {
            throw new RuntimeException("Unknown sample name!");
        }
    }

    /**
     * Constants for commonly used/needed commands
     */
    private static final String m_cmd_mqtt_version = "mqtt_version";
    private static final String m_cmd_log_destination = "log_destination";
    private static final String m_cmd_log_file_name = "log_file_name";
    private static final String m_cmd_verbosity = "verbosity";
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
    private static final String m_cmd_pkcs11_pin = "pin";
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
    private static final String m_cmd_custom_auth_token_key_name = "custom_auth_token_key_name";
    private static final String m_cmd_custom_auth_token_value = "custom_auth_token_value";
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
    private static final String m_cmd_shadow_property = "shadow_property";
    private static final String m_cmd_shadow_value = "shadow_value";
    private static final String m_cmd_shadow_name = "shadow_name";
    private static final String m_cmd_group_identifier = "group_identifier";
    private static final String m_cmd_username = "username";
    private static final String m_cmd_password = "password";
    private static final String m_cmd_protocol = "protocol";
    private static final String m_cmd_auth_params = "auth_params";
    private static final String m_cmd_pkcs12_file = "pkcs12_file";
    private static final String m_cmd_pkcs12_password = "pkcs12_password";
    private static final String m_cmd_region = "region";
    private static final String m_cmd_print_discover_resp_only = "print_discover_resp_only";
    private static final String m_cmd_android_keychain_alias = "keychain_alias";
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
