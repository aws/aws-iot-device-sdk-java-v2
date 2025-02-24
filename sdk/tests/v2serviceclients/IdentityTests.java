/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.crt.iot.MqttRequestResponseClientOptions;
import software.amazon.awssdk.iot.iotidentity.IotIdentityV2Client;
import software.amazon.awssdk.iot.iotidentity.model.CreateCertificateFromCsrRequest;
import software.amazon.awssdk.iot.iotidentity.model.CreateCertificateFromCsrResponse;
import software.amazon.awssdk.iot.iotidentity.model.CreateKeysAndCertificateRequest;
import software.amazon.awssdk.iot.iotidentity.model.CreateKeysAndCertificateResponse;
import software.amazon.awssdk.iot.iotidentity.model.RegisterThingRequest;
import software.amazon.awssdk.iot.iotidentity.model.RegisterThingResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.*;
import software.amazon.awssdk.services.sts.StsClient;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class IdentityTests extends V2ServiceClientTestFixture {

    private static class TestContext {
        private String thingName = null;
        private String certificateId = null;
    }

    private IotIdentityV2Client identityClient;
    private IotClient iotClient;

    private String testRegion;
    private String provisioningTemplateName;
    private String provisioningCsrPath;

    private TestContext testContext;

    void populateTestingEnvironmentVariables() {
        super.populateTestingEnvironmentVariables();
        provisioningTemplateName = System.getenv("AWS_TEST_IOT_CORE_PROVISIONING_TEMPLATE_NAME");
        testRegion = System.getenv("AWS_TEST_MQTT5_IOT_CORE_REGION");
        provisioningCsrPath = System.getenv("AWS_TEST_IOT_CORE_PROVISIONING_CSR_PATH");
    }

    boolean hasTestEnvironment() {
        return testRegion != null && provisioningTemplateName != null && provisioningCsrPath != null &&
                super.hasProvisioningTestEnvironment();
    }

    public IdentityTests() {
        super();
        populateTestingEnvironmentVariables();

        if (hasTestEnvironment()) {
            // reference STS to allow STS assume role in ~/.aws/credentials during local testing
            StsClient stsClient = StsClient.builder()
                    .region(Region.of(testRegion))
                    .build();

            iotClient = IotClient.builder()
                    .region(Region.of(testRegion))
                    .build();
        }
    }

    MqttRequestResponseClientOptions createDefaultServiceClientOptions() {
        return MqttRequestResponseClientOptions.builder()
                .withMaxRequestResponseSubscriptions(4)
                .withMaxStreamingSubscriptions(2)
                .withOperationTimeoutSeconds(10)
                .build();
    }

    void setupIdentityClient5(MqttRequestResponseClientOptions serviceClientOptions) {
        setupProvisioningMqtt5Client();

        if (serviceClientOptions == null) {
            serviceClientOptions = createDefaultServiceClientOptions();
        }

        identityClient = IotIdentityV2Client.newFromMqtt5(mqtt5Client, serviceClientOptions);
    }

    void setupIdentityClient311(MqttRequestResponseClientOptions serviceClientOptions) {
        setupProvisioningMqtt311Client();

        if (serviceClientOptions == null) {
            serviceClientOptions = createDefaultServiceClientOptions();
        }

        identityClient = IotIdentityV2Client.newFromMqtt311(mqtt311Client, serviceClientOptions);
    }

    void pause(long millis) {
        try {
            wait(millis);
        } catch (Exception ex) {
            ;
        }
    }

    @AfterEach
    public void tearDown() {
        if (!hasTestEnvironment()) {
            return;
        }

        if (identityClient != null) {
            identityClient.close();
            identityClient = null;
        }

        String certificateArn = null;
        if (testContext.certificateId != null) {
            DescribeCertificateResponse describeResponse = iotClient.describeCertificate(DescribeCertificateRequest.builder().certificateId(testContext.certificateId).build());
            certificateArn = describeResponse.certificateDescription().certificateArn();
        }

        if (testContext.thingName != null) {
            if (certificateArn != null) {
                iotClient.detachThingPrincipal(DetachThingPrincipalRequest.builder().thingName(testContext.thingName).principal(certificateArn).build());
                pause(1000);
            }

            iotClient.deleteThing(DeleteThingRequest.builder().thingName(testContext.thingName).build());
        }

        pause(1000);

        if (testContext.certificateId != null) {
            iotClient.updateCertificate(UpdateCertificateRequest.builder().certificateId(testContext.certificateId).newStatus(CertificateStatus.INACTIVE).build());

            ListAttachedPoliciesResponse listResponse = iotClient.listAttachedPolicies(ListAttachedPoliciesRequest.builder().target(certificateArn).build());
            for (Policy policy : listResponse.policies()) {
                iotClient.detachPolicy(DetachPolicyRequest.builder().policyName(policy.policyName()).target(certificateArn).build());
            }

            pause(1000);
            iotClient.deleteCertificate(DeleteCertificateRequest.builder().certificateId(testContext.certificateId).build());
        }
    }

    @BeforeEach
    public void setup() {
        testContext = new IdentityTests.TestContext();
    }

    void doBasicProvisioningTest() {
        try {
            CreateKeysAndCertificateRequest createRequest = new CreateKeysAndCertificateRequest();

            CreateKeysAndCertificateResponse createResponse = identityClient.createKeysAndCertificate(createRequest).get();
            testContext.certificateId = createResponse.certificateId;

            Assertions.assertNotNull(createResponse.certificateId);
            Assertions.assertNotNull(createResponse.certificatePem);
            Assertions.assertNotNull(createResponse.privateKey);
            Assertions.assertNotNull(createResponse.certificateOwnershipToken);

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("SerialNumber", UUID.randomUUID().toString());

            RegisterThingRequest registerRequest = new RegisterThingRequest();
            registerRequest.templateName = provisioningTemplateName;
            registerRequest.certificateOwnershipToken = createResponse.certificateOwnershipToken;
            registerRequest.parameters = parameters;

            RegisterThingResponse registerResponse = identityClient.registerThing(registerRequest).get();
            testContext.thingName = registerResponse.thingName;

            Assertions.assertNotNull(registerResponse.thingName);
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void basicProvisioning5() {
        assumeTrue(hasTestEnvironment());
        setupIdentityClient5(null);
        doBasicProvisioningTest();
    }

    @Test
    public void basicProvisioning311() {
        assumeTrue(hasTestEnvironment());
        setupIdentityClient311(null);
        doBasicProvisioningTest();
    }

    void doCsrProvisioningTest() {
        try {
            String csrContents = new String(Files.readAllBytes(Paths.get(provisioningCsrPath)));
            CreateCertificateFromCsrRequest createCertificateFromCsrRequest = new CreateCertificateFromCsrRequest();
            createCertificateFromCsrRequest.certificateSigningRequest = csrContents;

            CreateCertificateFromCsrResponse createResponse = identityClient.createCertificateFromCsr(createCertificateFromCsrRequest).get();
            testContext.certificateId = createResponse.certificateId;

            Assertions.assertNotNull(createResponse.certificateId);
            Assertions.assertNotNull(createResponse.certificatePem);
            Assertions.assertNotNull(createResponse.certificateOwnershipToken);

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("SerialNumber", UUID.randomUUID().toString());

            RegisterThingRequest registerRequest = new RegisterThingRequest();
            registerRequest.templateName = provisioningTemplateName;
            registerRequest.certificateOwnershipToken = createResponse.certificateOwnershipToken;
            registerRequest.parameters = parameters;

            RegisterThingResponse registerResponse = identityClient.registerThing(registerRequest).get();
            testContext.thingName = registerResponse.thingName;

            Assertions.assertNotNull(registerResponse.thingName);
        } catch (Exception ex) {
            Assertions.fail(ex);
        }
    }

    @Test
    public void csrProvisioning5() {
        assumeTrue(hasTestEnvironment());
        setupIdentityClient5(null);
        doCsrProvisioningTest();
    }

    @Test
    public void csrProvisioning311() {
        assumeTrue(hasTestEnvironment());
        setupIdentityClient311(null);
        doCsrProvisioningTest();
    }
}
