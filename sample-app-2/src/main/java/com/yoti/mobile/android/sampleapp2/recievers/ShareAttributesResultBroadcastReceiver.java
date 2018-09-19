package com.yoti.mobile.android.sampleapp2.recievers;


import android.content.Context;
import android.content.Intent;

import com.yoti.mobile.android.sampleapp2.MainActivity;
import com.yoti.mobile.android.sampleapp2.services.CallbackIntentService;
import com.yoti.mobile.android.sdk.AbstractShareAttributesBroadcastReceiver;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Example of a broadcast receiver implementing the {@link AbstractShareAttributesBroadcastReceiver}
 * In this example the call to the third party backend will be made manually.
 */
public class ShareAttributesResultBroadcastReceiver extends AbstractShareAttributesBroadcastReceiver {

    public static final String EXTRA_USE_CASE_ID = "com.yoti.mobile.android.sdk.EXTRA_USE_CASE_ID";
    public static final String EXTRA_CANCELLED_BY_USER = "com.yoti.mobile.android.sdk.EXTRA_CANCELLED_BY_USER";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //Start our activity so the app comes back to the foreground
        Intent myActivityIntent = new Intent(mContext, MainActivity.class);
        myActivityIntent.putExtra(MainActivity.LOADING_STATUS, true);
        mContext.startActivity(myActivityIntent);
    }

    @Override
    public boolean onCallbackReceived(String useCaseId, String callbackRoot, String token, String fullUrl) {
        CallbackIntentService.startActionRetrieveProfile(this.mContext, useCaseId, callbackRoot, token, fullUrl);

        return true;
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

    }

    @Override
    public void onCallbackError(String useCaseId, int httpErrorCode, Throwable error, byte[] response) {

    }
}