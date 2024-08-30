/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

package software.amazon.awssdk.iot;

import software.amazon.awssdk.crt.Log;
import software.amazon.awssdk.crt.Log.LogLevel;
import software.amazon.awssdk.crt.Log.LogSubject;
import software.amazon.awssdk.crt.io.TlsContextCustomKeyOperationOptions;
import software.amazon.awssdk.crt.io.TlsAndroidPrivateKeyOperationHandler;
import software.amazon.awssdk.crt.io.TlsContextOptions;
import software.amazon.awssdk.crt.utils.StringUtils;

import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateEncodingException;

import android.content.Context;
import android.security.KeyChain;
import android.security.KeyChainException;

/**
 * Builders for making a TlsContextCustomKeyOperationOptions using different Android KeyChain arguments.
 */
public class AndroidKeyChainHandlerBuilder {
    private PrivateKey privateKey;
    private String certificateFileContents;
    private String certificateFilePath;

    private AndroidKeyChainHandlerBuilder(PrivateKey privateKey, String certificateContents, String certificateFile){
        this.privateKey = privateKey;
        this.certificateFileContents = certificateContents;
        this.certificateFilePath = certificateFile;
    }

    private static PrivateKey getPrivateKey(Context context, String alias){
        try {
            PrivateKey privateKey = KeyChain.getPrivateKey(context, alias);
            if (privateKey == null){
                throw new RuntimeException("PrivateKey with alias '"+ alias +
                    "' either does not exist in Android KeyChain or permission has not been granted.");
            }
            Log.log(LogLevel.Debug,
            LogSubject.JavaAndroidKeychain,
            "PrivateKey retreived from Android KeyChain using Alias '" + alias + "'.");
            return privateKey;
        } catch (KeyChainException ex) {
            Log.log(LogLevel.Error,
            LogSubject.JavaAndroidKeychain,
            "KeyChainException encountered during GetPrivateKey: " + ex.getMessage());
        } catch (IllegalStateException ex) {
            Log.log(LogLevel.Error,
            LogSubject.JavaAndroidKeychain,
            "IllegalStateException encountered during GetPrivateKey: " + ex.getMessage());
        } catch (InterruptedException ex){
            Log.log(LogLevel.Error,
            LogSubject.JavaAndroidKeychain,
            "InterruptedException encountered during GetPrivateKey: " + ex.getMessage());
        }
        return null;
    }

    private static String getCertificateContent(Context context, String alias){
        X509Certificate[] myCertChain = null;
        // Get Certificate from KeyChain
        try {
            myCertChain = KeyChain.getCertificateChain(context, alias);

            if (myCertChain != null){
                // Convert Certificate to PEM formated String
                String certificateString = new String(StringUtils.base64Encode(myCertChain[0].getEncoded()));
                String certificate = "-----BEGIN CERTIFICATE-----\n" + certificateString + "\n-----END CERTIFICATE-----\n";

                Log.log(LogLevel.Debug,
                LogSubject.JavaAndroidKeychain,
                "Certificate retreived from Android KeyChain using Alias '" + alias + "'.");
                return certificate;
            } else {
                Log.log(LogLevel.Debug,
                LogSubject.JavaAndroidKeychain,
                "x509Certificate with alias: '" + alias +
                    "' either does not exist in Android KeyChain or permission has not been granted.");
            }
        } catch (KeyChainException ex){
            Log.log(LogLevel.Debug,
            LogSubject.JavaAndroidKeychain,
            "KeyChainException encountered during getCertificateContent: " + ex.getMessage());
        } catch (IllegalStateException ex){
            Log.log(LogLevel.Debug,
            LogSubject.JavaAndroidKeychain,
            "IllegalStateException encountered during getCertificateContent: " + ex.getMessage());
        } catch (InterruptedException ex){
            Log.log(LogLevel.Debug,
            LogSubject.JavaAndroidKeychain,
            "InterruptedException encountered during getCertificateContent: " + ex.getMessage());
        } catch (CertificateEncodingException  ex){
            Log.log(LogLevel.Debug,
            LogSubject.JavaAndroidKeychain,
            "CertificateEncodingException encountered during getCertificateContent: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Creates a new Android KeyChain Handler Builder. A PrivateKey will be extracted from the context's
     * KeyChain using the provided alias. Permission to access the PrivateKey must be granted prior to creating
     * the Builder. The alias will also be used to attempt to access and set the
     * X509Certificate associated within the KeyChain. If an X509Certificate is not present in the KeyChain,
     * a certificate must be provided before calling build().
     *
     * @param context Interface to global information about an Android application environment.
     * @param alias Alias of PrivateKey stored within the Android KeyChain.
     * @return A new AndroidKeyChainHandlerBuilder
     */
    public static AndroidKeyChainHandlerBuilder newKeyChainHandlerWithAlias(Context context, String alias){
        PrivateKey privateKey = getPrivateKey(context, alias);
        String certContents = getCertificateContent(context, alias);

        return new AndroidKeyChainHandlerBuilder(privateKey, certContents, null);
    }

    /**
     * Creates a new Android KeyChain Handler Builder. The generated Android KeyChain Handler will use the
     * provided PrivateKey to perform SIGN operations during mTLS.
     *
     * @param privateKey PrivateKey from an Android KeyChain.
     * @param certificateFile Path to certificate, in PEM format.
     * @return A new AndroidKeyChainHandlerBuilder
     */
    public static AndroidKeyChainHandlerBuilder newKeyChainHandlerWithPrivateKeyAndCertificateFile(
        PrivateKey privateKey, String certificateFile){
        return new AndroidKeyChainHandlerBuilder(privateKey, null, certificateFile);
    }

    /**
     * Creates a new Android KeyChain Handler Builder. The generated Android KeyChain Handler will use the
     * provided PrivateKey to perform SIGN operations during mTLS.
     *
     * @param privateKey PrivateKey from an Android KeyChain.
     * @param certificateContents contents of PEM-formatted certificate file.
     * @return A new AndroidKeyChainHandlerBuilder
     */
    public static AndroidKeyChainHandlerBuilder newKeyChainHandlerWithPrivateKeyAndCertificateContents(
        PrivateKey privateKey, String certificateContents){

        return new AndroidKeyChainHandlerBuilder(privateKey, certificateContents, null);
    }

    /**
     * Sets the path to the certificate in PEM format. This function will overwrite any other form of certificate
     * currently being used by the Android Key Chain Handler Builder.
     *
     * @param certificatePath Path to certificate, in PEM format.
     * @return The AndroidKeyChainHandlerBuilder
     */
    public AndroidKeyChainHandlerBuilder withCertificateFromPath(String certificatePath){
        //Setting a path overrides all other forms of certificates (with cert contents/ private key extraction)
        this.certificateFilePath = certificatePath;
        this.certificateFileContents = null;
        return this;
    }

    /**
     * Sets the contents of the of PEM-formatted certificate. This function will overwrite any other form of
     * certificate currently being used by the Android Key Chain Builder.
     *
     * @param certificateContents contents of PEM-formatted certificate file.
     * @return The AndroidKeyChainHandlerBuilder
     */
    public AndroidKeyChainHandlerBuilder withCertificateContents(String certificateContents){
        this.certificateFileContents = certificateContents;
        this.certificateFilePath = null;
        return this;
    }

    /**
     * Constructs a TlsContextCustomKeyOperationOptions with the options set.
     * @return A TlsContextCustomKeyOperationOptions
     */
    public TlsContextCustomKeyOperationOptions build() {
        if (this.privateKey == null) {
            throw new RuntimeException("Android KeyChain Handler Builder cannot build TlsContextCustomKeyOperationOptions without a PrivateKey.");
        }

        TlsAndroidPrivateKeyOperationHandler keyChainOperationHandler =
            new TlsAndroidPrivateKeyOperationHandler(this.privateKey);

        TlsContextCustomKeyOperationOptions keyOperationOptions =
            new TlsContextCustomKeyOperationOptions(keyChainOperationHandler);

        if (this.certificateFilePath != null){
            keyOperationOptions.withCertificateFilePath(this.certificateFilePath);
        } else if (this.certificateFileContents != null) {
            keyOperationOptions.withCertificateFileContents(this.certificateFileContents);
        }

        return keyOperationOptions;
    }
}