package com.yoti.mobile.android.sdk.kernelSDK;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yoti.mobile.android.sdk.R;
import com.yoti.mobile.android.sdk.YotiSDKLogger;
import com.yoti.mobile.android.sdk.model.CustomCertificate;
import com.yoti.mobile.android.sdk.model.Scenario;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import static com.yoti.mobile.android.sdk.kernelSDK.EnvironmentConfiguration.YOTI_PUBLIC_KEYSTORE;
import static com.yoti.mobile.android.sdk.kernelSDK.EnvironmentConfiguration.YOTI_PUBLIC_KEYSTORE_ALIAS;
import static com.yoti.mobile.android.sdk.kernelSDK.EnvironmentConfiguration.YOTI_PUBLIC_KEYSTORE_PWD;

public class KernelSDK {

    private final Context mContext;
    private final CertificateManager mCertMgr;

    public KernelSDK(Context context) {
        mContext = context;

        mCertMgr = new CertificateManager(mContext);

        mCertMgr.storeCrt(R.raw.core,
                YOTI_PUBLIC_KEYSTORE,
                YOTI_PUBLIC_KEYSTORE_PWD,
                YOTI_PUBLIC_KEYSTORE_ALIAS);
    }

    /**
     * Will make a call to the Yoti Connect API to retrieve the Uri for the provided Scenario
     *
     * @param currentScenario
     * @return the uri of the provided scenario to forward to the Yoti app
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public String retrieveScenarioUri(Scenario currentScenario) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {

        URL url = new URL(String.format(EnvironmentConfiguration.GET_QR_CODE_URL, currentScenario.getClientSDKId(), currentScenario.getScenarioId()));

        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setSSLSocketFactory(getSslSocketFactory(YOTI_PUBLIC_KEYSTORE, YOTI_PUBLIC_KEYSTORE_PWD));

        byte[] response = getBytes(urlConnection);

        return response == null ? null : new String(response);

    }


    /**
     * Process the result of a share attribute flow, will make a call to the third party backend defined in the scenario.
     * @param currentScenario
     * @param listener
     */
    public void processShareResult(@NonNull Scenario currentScenario, @NonNull ICallbackBackendListener listener) {

        try {

            URL url = new URL(currentScenario.getCallbackBackendUrl());

            CustomCertificate certificate = currentScenario.getCustomCertificate();
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            if (certificate != null) {
                mCertMgr.storeCrt(certificate.getCertificateResourceId(),
                        certificate.getStoreName(),
                        certificate.getPassword(),
                        certificate.getAlias());

                urlConnection.setSSLSocketFactory(getSslSocketFactory(certificate.getStoreName(), certificate.getPassword()));
            }

            int statusCode = urlConnection.getResponseCode();

            byte[] response = getBytes(urlConnection);

            if (listener != null) {
                if (statusCode >= 200 && statusCode <= 299) {
                    listener.onSuccess(response);
                } else {
                    listener.onError(statusCode, null, response);
                }
            } else {
                YotiSDKLogger.warning("ICallbackBackendListener is null");
            }

        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            if (listener != null) {
                listener.onError(-1, e, null);
            } else {
                YotiSDKLogger.warning("ICallbackBackendListener is null");
            }
        }
    }

    /**
     * return a SSlSocketFactory for the provided store name
     * @param storeName
     * @param storePassword
     * @return
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyManagementException
     */
    private SSLSocketFactory getSslSocketFactory(String storeName, String storePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException {

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream fos = mCertMgr.getKeyStoreAr(storeName);
        keyStore.load(fos, storePassword.toCharArray());

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    /**
     * read bytes from a UrlConnection
     * @param urlConnection
     * @return
     * @throws IOException
     */
    private byte[] getBytes(HttpsURLConnection urlConnection) throws IOException {
        byte[] response = null;

        try {
            InputStream is = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                is = urlConnection.getInputStream();

                byte[] byteChunk = new byte[4096];
                int n;

                while ((n = is.read(byteChunk)) > 0) {
                    baos.write(byteChunk, 0, n);
                }

                response = baos.toByteArray();
                is.close();

            } catch (IOException e) {
                if (is != null) {
                    is.close();
                }
            }

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;
    }
}
