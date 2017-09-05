package com.yoti.mobile.android.sdk.kernelSDK;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;

import com.yoti.mobile.android.sdk.YotiSDKLogger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;


public class CertificateManager {

    private final Context mContext;

    public CertificateManager(Context context) {
        this.mContext = context;
    }

    /**
     * Store a certificate in the private file of the application
     *
     * @param certificateResourceId - the certificate resource identifier ot store in the key store
     * @param storeName             - the key name used to identify this very store
     * @param password              - the password to secure the keystore in which the certificate will be stored
     * @param alias                 - the alias for this certificate in the in the keystore
     * @return true if the certificate has been successfully stored
     */
    public boolean storeCrt(@RawRes int certificateResourceId,
                            @NonNull String storeName, @NonNull String password, @NonNull String alias) {

        try {
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);

            loadKeyStore(storeName, password, keyStore);
            storeCertificateInKeyStore(certificateResourceId, storeName, password, alias, keyStore);

        } catch (Exception e) {
            YotiSDKLogger.error("Unable to store the designated certificate with the alias:" + alias + " in the file " + storeName, e);
            return false;
        }

        return true;
    }

    /**
     * Force write the certificate in the designated key store
     *
     * @param certificateResourceId - the certificate resource identifier ot store in the key store
     * @param storeName             - the key name used to identify this very store
     * @param password              - the password to secure the keystore in which the certificate will be stored
     * @param alias                 - the alias for this certificate in the in the keystore
     * @param keyStore              - the key store instance
     */
    private void storeCertificateInKeyStore(@RawRes int certificateResourceId, @NonNull String storeName,
                                            @NonNull String password, @NonNull String alias,
                                            KeyStore keyStore) {
        try {
            // ALWAYS store the alias in the store
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(mContext.getResources().openRawResource(certificateResourceId));
            Certificate ca = cf.generateCertificate(caInput);

            if (keyStore.containsAlias(alias)) {
                Certificate storedCert = keyStore.getCertificate(alias);
                if (!storedCert.equals(ca)) {
                    writeCertificateInKeyStore(storeName, password, alias, keyStore, ca);
                }
            } else {
                writeCertificateInKeyStore(storeName, password, alias, keyStore, ca);
            }

        } catch (Exception e) {
            YotiSDKLogger.error("Unable to store the designated certificate with the alias:" + alias + " in the file " + storeName, e);

        }
    }

    /**
     * Save the certificate in the key store and persist this key store on the file system
     *
     * @param storeName - the key name used to identify this very store
     * @param password  - the password to secure the keystore in which the certificate will be stored
     * @param alias     - the alias for this certificate in the in the keystore
     * @param keyStore  - the key store instance
     * @param ca        - the certificate to save
     */
    private void writeCertificateInKeyStore(@NonNull String storeName,
                                            @NonNull String password, @NonNull String alias,
                                            KeyStore keyStore, Certificate ca) {

        FileOutputStream fos = null;
        try {

            keyStore.setCertificateEntry(alias, ca);

            fos = mContext.openFileOutput(storeName, Context.MODE_PRIVATE);
            keyStore.store(fos, password.toCharArray());
        } catch (Exception e) {
            YotiSDKLogger.error("Unable to store the designated certificate with the alias:" + alias + " in the file " + storeName, e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                // Nothing to do here
            }
        }
    }

    /**
     * Attempt to load the keystore from the file system if present or create new one if needed
     *
     * @param storeName the store name
     * @param password  the password to load the existing store
     * @param store     the store instance to load
     */
    private void loadKeyStore(@NonNull String storeName, @NonNull String password, KeyStore store) throws CertificateException, NoSuchAlgorithmException, IOException {
        try {
            // Check if the keystore exists
            FileInputStream storeInputStream = getKeyStoreAr(storeName);
            if (storeInputStream != null) {
                store.load(storeInputStream, password.toCharArray());
            } else {
                // Key store not created yet, not loaded, create one
                store.load(null, null);
            }
        } catch (Exception e) {

            // Try to create a new store in case the current store is corrupted and we were not able to load it
            store.load(null, null);
        }
    }

    /**
     * Check if a key store has been already created
     *
     * @param storeName - the name under which the key store has been saved
     * @return an instance of the key store id it has been found, null otherwise
     */
    public FileInputStream getKeyStoreAr(@NonNull String storeName) {
        FileInputStream storeInputStream;
        try {
            storeInputStream = mContext.openFileInput(storeName);
        } catch (FileNotFoundException e) {
            return null;
        }

        return storeInputStream;
    }
}
