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
        registerCommand(m_cmd_verbosity, "<str>", "The amount of detail in the logging output of the service test." +
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

    public void addKeyAndCertCommands() {
        registerCommand(m_cmd_key_file, "<path>", "Path to your key in PEM format.");
        registerCommand(m_cmd_cert_file, "<path>", "Path to your client certificate in PEM format.");
    }

    /**
     * Helper functions for parsing commands
     */

    private void parseCommonLoggingCommands(ServiceTestCommandLineData returnData){
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

    private void parseUseMqtt5(ServiceTestCommandLineData returnData) {
        returnData.input_use_mqtt5 = Boolean.parseBoolean(getCommandOrDefault(m_cmd_use_mqtt5, "false"));
    }

    private void parseCommonMQTTCommands(ServiceTestCommandLineData returnData) {
        returnData.input_endpoint = getCommandRequired(m_cmd_endpoint);
        returnData.input_ca = getCommandOrDefault(m_cmd_ca_file, "");
    }

    private void parseKeyAndCertCommands(ServiceTestCommandLineData returnData)
    {
        returnData.input_cert = getCommandRequired(m_cmd_cert_file);
        returnData.input_key = getCommandRequired(m_cmd_key_file);
    }

    private void parseClientIdAndPort(ServiceTestCommandLineData returnData) {
        returnData.input_clientId = getCommandOrDefault(m_cmd_client_id, "test-" + UUID.randomUUID().toString());
        returnData.input_port = Integer.parseInt(getCommandOrDefault(m_cmd_port, "8883"));
    }

    public class ServiceTestCommandLineData
    {
        // General use
        public Boolean input_use_mqtt5;
        public String input_endpoint;
        public String input_cert;
        public String input_key;
        public String input_ca;
        public String input_clientId;
        public int input_port;
        // Fleet provisioning
        public String input_templateName;
        public String input_templateParameters;
        public String input_csrPath;
    }

    public ServiceTestCommandLineData parseServiceTestInputFleetProvisioning(String [] args)
    {
        addCommonLoggingCommands();
        addCommonMQTTCommands();
        addKeyAndCertCommands();
        addClientIdAndPort();
        registerCommand(m_cmd_fleet_template_name, "<str>", "Provisioning template name.");
        registerCommand(m_cmd_fleet_template_parameters, "<json>", "Provisioning template parameters.");
        registerCommand(m_cmd_fleet_template_csr, "<path>", "Path to the CSR file (optional).");
        sendArguments(args);

        ServiceTestCommandLineData returnData = new ServiceTestCommandLineData();
        parseUseMqtt5(returnData);
        parseCommonLoggingCommands(returnData);
        parseCommonMQTTCommands(returnData);
        parseKeyAndCertCommands(returnData);
        parseClientIdAndPort(returnData);
        returnData.input_templateName = getCommandRequired(m_cmd_fleet_template_name);
        returnData.input_templateParameters = getCommandRequired(m_cmd_fleet_template_parameters);
        returnData.input_csrPath = getCommandOrDefault(m_cmd_fleet_template_csr, null);
        return returnData;
    }

    public static ServiceTestCommandLineData getInputForServiceTest(String serviceTestName, String[] args)
    {
        CommandLineUtils cmdUtils = new CommandLineUtils();
        cmdUtils.registerProgramName(serviceTestName);
        cmdUtils.determineIfCI();

        if (serviceTestName.equals("FleetProvisioning")) {
            return cmdUtils.parseServiceTestInputFleetProvisioning(args);
        } else {
            throw new RuntimeException("Unknown service test name!");
        }
    }

    /**
     * Constants for commonly used/needed commands
     */
    private static final String m_cmd_use_mqtt5 = "use_mqtt5";
    private static final String m_cmd_log_destination = "log_destination";
    private static final String m_cmd_log_file_name = "log_file_name";
    private static final String m_cmd_verbosity = "verbosity";
    private static final String m_cmd_endpoint = "endpoint";
    private static final String m_cmd_ca_file = "ca_file";
    private static final String m_cmd_cert_file = "cert";
    private static final String m_cmd_key_file = "key";
    private static final String m_cmd_client_id = "client_id";
    private static final String m_cmd_port = "port";
    private static final String m_cmd_help = "help";
    private static final String m_cmd_fleet_template_name = "template_name";
    private static final String m_cmd_fleet_template_parameters = "template_parameters";
    private static final String m_cmd_fleet_template_csr = "csr";
    private static final String m_cmd_region = "region";
    private static final String m_cmd_print_discover_resp_only = "print_discover_resp_only";
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
