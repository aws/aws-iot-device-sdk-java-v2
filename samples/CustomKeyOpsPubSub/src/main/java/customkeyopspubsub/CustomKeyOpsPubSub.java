/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package customkeyopspubsub;

import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtResource;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.io.*;
import software.amazon.awssdk.crt.mqtt.*;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class CustomKeyOpsPubSub {

    // When run normally, we want to exit nicely even if something goes wrong
    // When run from CI, we want to let an exception escape which in turn causes the
    // exec:java task to return a non-zero exit code
    static String ciPropValue = System.getProperty("aws.crt.ci");
    static boolean isCI = ciPropValue != null && Boolean.valueOf(ciPropValue);

    static String clientId = "test-" + UUID.randomUUID().toString();
    static String rootCaPath;
    static String certPath;
    static String keyPath;
    static String endpoint;
    static String topic = "test/topic";
    static String message = "Hello World!";
    static int    messagesToPublish = 10;
    static boolean showHelp = false;
    static int port = 8883;

    static void printUsage() {
        System.out.println(
                "Usage:\n"+
                "  --help            This message\n"+
                "  --clientId        Client ID to use when connecting (optional)\n"+
                "  -e|--endpoint     AWS IoT service endpoint hostname\n"+
                "  -p|--port         Port to connect to on the endpoint\n"+
                "  -r|--rootca       Path to the root certificate\n"+
                "  -c|--cert         Path to the IoT thing certificate\n"+
                "  -k|--key          Path to the IoT thing private key\n"+
                "  -t|--topic        Topic to subscribe/publish to (optional)\n"+
                "  -m|--message      Message to publish (optional)\n"+
                "  -n|--count        Number of messages to publish (optional)\n"
        );
    }

    static void parseCommandLine(String[] args) {
        for (int idx = 0; idx < args.length; ++idx) {
            switch (args[idx]) {
                case "--help":
                    showHelp = true;
                    break;
                case "--clientId":
                    if (idx + 1 < args.length) {
                        clientId = args[++idx];
                    }
                    break;
                case "-e":
                case "--endpoint":
                    if (idx + 1 < args.length) {
                        endpoint = args[++idx];
                    }
                    break;
                case "-p":
                case "--port":
                    if (idx + 1 < args.length) {
                        port = Integer.parseInt(args[++idx]);
                    }
                    break;
                case "-r":
                case "--rootca":
                    if (idx + 1 < args.length) {
                        rootCaPath = args[++idx];
                    }
                    break;
                case "-c":
                case "--cert":
                    if (idx + 1 < args.length) {
                        certPath = args[++idx];
                    }
                    break;
                case "-k":
                case "--key":
                    if (idx + 1 < args.length) {
                        keyPath = args[++idx];
                    }
                    break;
                case "-t":
                case "--topic":
                    if (idx + 1 < args.length) {
                        topic = args[++idx];
                    }
                    break;
                case "-m":
                case "--message":
                    if (idx + 1 < args.length) {
                        message = args[++idx];
                    }
                    break;
                case "-n":
                case "--count":
                    if (idx + 1 < args.length) {
                        messagesToPublish = Integer.parseInt(args[++idx]);
                    }
                    break;
                default:
                    System.out.println("Unrecognized argument: " + args[idx]);
            }
        }
    }

    /*
     * When called during a CI run, throw an exception that will escape and fail the exec:java task
     * When called otherwise, print what went wrong (if anything) and just continue (return from main)
     */
    static void onApplicationFailure(Throwable cause) {
        if (isCI) {
            throw new RuntimeException("CustomKeyOpsPubSub execution failure", cause);
        } else if (cause != null) {
            System.out.println("Exception encountered: " + cause.toString());
        }
    }

    static class MyKeyOperationHandler implements TlsKeyOperationHandler {
        RSAPrivateKey key;

        MyKeyOperationHandler(String keyPath) {
            key = loadPrivateKey(keyPath);
        }

        public void performOperation(TlsKeyOperation operation) {
            try {
                System.out.println("MyKeyOperationHandler.performOperation" + operation.getType().name());
                if (operation.getType() != TlsKeyOperation.Type.SIGN) {
                    throw new RuntimeException("Simple sample only handles SIGN operations");
                }

                if (operation.getSignatureAlgorithm() != TlsSignatureAlgorithm.RSA) {
                    throw new RuntimeException("Simple sample only handles RSA keys");
                }

                if (operation.getDigestAlgorithm() != TlsHashAlgorithm.SHA256) {
                    throw new RuntimeException("Simple sample only handles SHA256 digests");
                }

                // A SIGN operation's inputData is the 32bytes of the SHA-256 digest.
                // Before doing the RSA signature, we need to construct a PKCS1 v1.5 DigestInfo.
                // See https://datatracker.ietf.org/doc/html/rfc3447#section-9.2
                byte[] digest = operation.getInput();

                // These are the appropriate bytes for the SHA-256 AlgorithmIdentifier:
                // https://tools.ietf.org/html/rfc3447#page-43
                byte[] sha256DigestAlgorithm = { 0x30, 0x31, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte)0x86, 0x48, 0x01,
                        0x65, 0x03, 0x04, 0x02, 0x01, 0x05, 0x00, 0x04, 0x20 };

                ByteArrayOutputStream digestInfoStream = new ByteArrayOutputStream();
                digestInfoStream.write(sha256DigestAlgorithm);
                digestInfoStream.write(digest);
                byte[] digestInfo = digestInfoStream.toByteArray();

                // Sign the DigestInfo
                Signature rsaSign = Signature.getInstance("NONEwithRSA");
                rsaSign.initSign(key);
                rsaSign.update(digestInfo);
                byte[] signatureBytes = rsaSign.sign();

                operation.complete(signatureBytes);

            } catch (Exception ex) {
                System.out.println("Error during key operation:" + ex);
                operation.completeExceptionally(ex);
            }
        }

        RSAPrivateKey loadPrivateKey(String filepath) {
            /* Adapted from: https://stackoverflow.com/a/27621696
             * You probably need to convert your private key file from PKCS#1
             * to PKCS#8 to get it working with this sample:
             *
             * $ openssl pkcs8 -topk8 -in my-private.pem.key -out my-private-pk8.pem.key -nocrypt
             *
             * IoT Core vends keys as PKCS#1 by default,
             * but Java only seems to have this PKCS8EncodedKeySpec class */
            try {
                /* Read the BASE64-encoded contents of the private key file */
                StringBuilder pemBase64 = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Strip off PEM header and footer
                        if (line.startsWith("---")) {
                            if (line.contains("RSA")) {
                                throw new RuntimeException("private key must be converted from PKCS#1 to PKCS#8");
                            }
                            continue;
                        }
                        pemBase64.append(line);
                    }
                }

                String pemBase64String = pemBase64.toString();
                byte[] der = Base64.getDecoder().decode(pemBase64String);

                /* Create PrivateKey instance */
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(der);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
                return (RSAPrivateKey)privateKey;

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void main(String[] args) {

        parseCommandLine(args);
        if (showHelp || endpoint == null || certPath == null || keyPath == null) {
            printUsage();
            onApplicationFailure(null);
            return;
        }


        MqttClientConnectionEvents callbacks = new MqttClientConnectionEvents() {
            @Override
            public void onConnectionInterrupted(int errorCode) {
                if (errorCode != 0) {
                    System.out.println("Connection interrupted: " + errorCode + ": " + CRT.awsErrorString(errorCode));
                }
            }

            @Override
            public void onConnectionResumed(boolean sessionPresent) {
                System.out.println("Connection resumed: " + (sessionPresent ? "existing session" : "clean session"));
            }
        };

        MyKeyOperationHandler myKeyOperationHandler = new MyKeyOperationHandler(keyPath);
        TlsContextCustomKeyOperationOptions keyOperationOptions = new TlsContextCustomKeyOperationOptions(myKeyOperationHandler)
                .withCertificateFilePath(certPath);

        try(EventLoopGroup eventLoopGroup = new EventLoopGroup(1);
            HostResolver resolver = new HostResolver(eventLoopGroup);
            ClientBootstrap clientBootstrap = new ClientBootstrap(eventLoopGroup, resolver);
            AwsIotMqttConnectionBuilder builder = AwsIotMqttConnectionBuilder.newMtlsCustomKeyOperationsBuilder(keyOperationOptions)) {

            if (rootCaPath != null) {
                builder.withCertificateAuthorityFromPath(null, rootCaPath);
            }

            builder.withBootstrap(clientBootstrap)
                .withConnectionEventCallbacks(callbacks)
                .withClientId(clientId)
                .withEndpoint(endpoint)
                .withPort((short)port)
                .withCleanSession(true)
                .withProtocolOperationTimeoutMs(60000);

            try(MqttClientConnection connection = builder.build()) {

                CompletableFuture<Boolean> connected = connection.connect();
                try {
                    boolean sessionPresent = connected.get();
                    System.out.println("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");
                } catch (Exception ex) {
                    throw new RuntimeException("Exception occurred during connect", ex);
                }

                CountDownLatch countDownLatch = new CountDownLatch(messagesToPublish);

                CompletableFuture<Integer> subscribed = connection.subscribe(topic, QualityOfService.AT_LEAST_ONCE, (message) -> {
                    String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                    System.out.println("MESSAGE: " + payload);
                    countDownLatch.countDown();
                });

                subscribed.get();

                int count = 0;
                while (count++ < messagesToPublish) {
                    CompletableFuture<Integer> published = connection.publish(new MqttMessage(topic, message.getBytes(), QualityOfService.AT_LEAST_ONCE, false));
                    published.get();
                    Thread.sleep(1000);
                }

                countDownLatch.await();

                CompletableFuture<Void> disconnected = connection.disconnect();
                disconnected.get();
            }
        } catch (CrtRuntimeException | InterruptedException | ExecutionException ex) {
            onApplicationFailure(ex);
        }

        CrtResource.waitForNoResources();

        System.out.println("Complete!");
    }
}
