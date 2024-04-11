/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 *
 * This file is generated.
 */

package software.amazon.awssdk.iot.iotidentity.model;


/**
 * Response payload to a CreateCertificateFromCsr request.
 *
 */
public class CreateCertificateFromCsrResponse {

    /**
     * The ID of the certificate.
     *
     */
    public String certificateId;


    /**
     * The certificate data, in PEM format.
     *
     */
    public String certificatePem;


    /**
     * The token to prove ownership of the certificate during provisioning.
     *
     */
    public String certificateOwnershipToken;


}
