package com.yoti.mobile.android.sdk.kernelSDK;

/**
 * Environment constants
 */
public class EnvironmentConfiguration {

    static final String YOTI_PUBLIC_KEYSTORE_PWD = "W?9zvT%a(KU#L&V~";
    static final String YOTI_PUBLIC_KEYSTORE_ALIAS = "sdk_prod";
    final static String YOTI_PUBLIC_KEYSTORE = "yoti.sdk.keystore";

    private final static String HOST_CONNECT_API = "https://api.yoti.com";
    private final static String CONNECT_API_PORT = "443";

    static final String GET_QR_CODE_URL = HOST_CONNECT_API + ":" + CONNECT_API_PORT + "/api/v1/sessions/apps/%1s/scenarios/%2s?transport=URI";
}