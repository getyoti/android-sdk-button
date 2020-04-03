package com.yoti.mobile.android.sdk.sampleapp;


import android.content.Intent;

import com.yoti.mobile.android.sdk.AbstractShareAttributesBroadcastReceiver;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Example of a broadcast receiver implementing the {@link AbstractShareAttributesBroadcastReceiver}
 * In this example the call to the third party backend will be made by the Yoti Mobile SDK.
 */
public class ShareAttributesResultBroadcastReceiver extends AbstractShareAttributesBroadcastReceiver {

    public static final String EXTRA_USE_CASE_ID = "com.yoti.mobile.android.sdk.EXTRA_USE_CASE_ID";
    public static final String EXTRA_CANCELLED_BY_USER = "com.yoti.mobile.android.sdk.EXTRA_CANCELLED_BY_USER";
    public static final String EXTRA_IS_FAILED = "com.yoti.mobile.android.sdk.EXTRA_IS_FAILED";
    public static final String EXTRA_RESPONSE = "com.yoti.mobile.android.sdk.EXTRA_RESPONSE";
    public static final String EXTRA_LOADING = "com.yoti.mobile.android.sdk.EXTRA_LOADING";

    @Override
    public boolean onCallbackReceived(String useCaseId, String callbackRoot, String token, String fullUrl) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(EXTRA_LOADING, true);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        return false;
    }

    @Override
    public void onShareFailed(String useCaseId) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(EXTRA_USE_CASE_ID, useCaseId);
        intent.putExtra(EXTRA_CANCELLED_BY_USER, true);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    public void onCallbackSuccess(String useCaseId, byte[] response) {
        if (response != null) {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra(EXTRA_RESPONSE, new String(response));
            intent.putExtra(EXTRA_USE_CASE_ID, useCaseId);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void onCallbackError(String useCaseId, int httpErrorCode, Throwable error, byte[] response) {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(EXTRA_IS_FAILED, true);
        intent.putExtra(EXTRA_USE_CASE_ID, useCaseId);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
