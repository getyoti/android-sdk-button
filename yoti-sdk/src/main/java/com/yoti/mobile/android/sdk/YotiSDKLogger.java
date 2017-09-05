package com.yoti.mobile.android.sdk;

import android.util.Log;

import static com.yoti.mobile.android.sdk.YotiSDKDefs.YOTI_SDK_TAG;

/**
 * Logger class for the Yoti SDK
 */

public class YotiSDKLogger {

    public static void error(String message) {
        if (YotiSDK.isSDKLoggingEnabled()) {
            Log.e(YOTI_SDK_TAG, message);
        }
    }

    public static void error(String message, Throwable cause) {
        if (YotiSDK.isSDKLoggingEnabled()) {
            Log.e(YOTI_SDK_TAG, message, cause);
        }
    }

    public static void warning(String message) {
        if (YotiSDK.isSDKLoggingEnabled()) {
            Log.w(YOTI_SDK_TAG, message);
        }
    }

    public static void debug(String message) {
        if (YotiSDK.isSDKLoggingEnabled()) {
            Log.d(YOTI_SDK_TAG, message);
        }
    }
}
