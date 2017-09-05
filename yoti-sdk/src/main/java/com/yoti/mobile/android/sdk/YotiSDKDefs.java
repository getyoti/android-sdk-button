package com.yoti.mobile.android.sdk;

public interface YotiSDKDefs {

    String YOTI_SDK_TAG = "Yoti Mobile SDK";

    String TOKEN_PARAM = "token";
    String CALLBACK_PARAM = "callback";
    String USE_CASE_ID_PARAM = "useCaseId";


    /* This should not be changed unless it is changed in the Yoti App */
    String INTENT_EXTRA_CALLBACK_URL = "com.yoti.mobile.android.app.connect.ui.confirm.mobile.INTENT_EXTRA_CALLBACK_URL";
    String INTENT_EXTRA_USE_CASE_ID = "com.yoti.mobile.android.app.connect.ui.confirm.mobile.INTENT_EXTRA_USE_CASE_ID";
    String INTENT_EXTRA_RESULT = "com.yoti.mobile.android.app.connect.ui.confirm.mobile.INTENT_EXTRA_RESULT";
    String INTENT_EXTRA_RESPONSE = "com.yoti.mobile.android.app.connect.ui.confirm.mobile.INTENT_EXTRA_RESPONSE";
    String INTENT_EXTRA_HTTP_ERROR_CODE = "com.yoti.mobile.android.app.connect.ui.confirm.mobile.INTENT_EXTRA_HTTP_ERROR_CODE";
    String INTENT_EXTRA_ERROR = "com.yoti.mobile.android.app.connect.ui.confirm.mobile.INTENT_EXTRA_ERROR";
}
