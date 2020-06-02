package com.yoti.mobile.android.sdk.kernelSDK;

import com.yoti.mobile.android.sdk.BuildConfig;

/**
 * Environment constants
 */
public class EnvironmentConfiguration {

    private final static String HOST_CONNECT_API = BuildConfig.YOTI_BACKEND_URL;
    private final static String CONNECT_API_PORT = Integer.toString(BuildConfig.YOTI_BACKEND_PORT);

    static final String GET_QR_CODE_URL = HOST_CONNECT_API + ":" + CONNECT_API_PORT + "/api/v1/sessions/apps/%1s/scenarios/%2s?transport=URI";
}