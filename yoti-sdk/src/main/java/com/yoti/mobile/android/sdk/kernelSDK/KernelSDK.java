package com.yoti.mobile.android.sdk.kernelSDK;

import android.support.annotation.NonNull;

import com.yoti.mobile.android.sdk.YotiSDKLogger;
import com.yoti.mobile.android.sdk.model.Scenario;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class KernelSDK {

    /**
     * Will make a call to the Yoti Connect API to retrieve the Uri for the provided Scenario
     *
     * @param currentScenario
     * @return the uri of the provided scenario to forward to the Yoti app
     * @throws IOException
     */
    public String retrieveScenarioUri(Scenario currentScenario) throws IOException {

        URL url = new URL(String.format(EnvironmentConfiguration.GET_QR_CODE_URL, currentScenario.getClientSDKId(), currentScenario.getScenarioId()));

        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        byte[] response = getBytes(urlConnection);

        return response == null ? null : new String(response);

    }


    /**
     * Process the result of a share attribute flow, will make a call to the third party backend defined in the scenario.
     *
     * @param currentScenario
     * @param listener
     */
    public void processShareResult(@NonNull Scenario currentScenario, @NonNull ICallbackBackendListener listener) {

        try {
            URL url = new URL(currentScenario.getCallbackBackendUrl());

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

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

        } catch (Exception e) {
            if (listener != null) {
                listener.onError(-1, e, null);
            } else {
                YotiSDKLogger.warning("ICallbackBackendListener is null");
            }
        }
    }

    /**
     * read bytes from a UrlConnection
     *
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
