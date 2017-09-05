package com.yoti.mobile.android.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.yoti.mobile.android.sdk.kernelSDK.KernelSDKIntentService;

import java.net.ConnectException;

import static com.yoti.mobile.android.sdk.YotiSDKDefs.INTENT_EXTRA_CALLBACK_URL;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.INTENT_EXTRA_ERROR;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.INTENT_EXTRA_HTTP_ERROR_CODE;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.INTENT_EXTRA_RESPONSE;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.INTENT_EXTRA_RESULT;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.INTENT_EXTRA_USE_CASE_ID;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.TOKEN_PARAM;

/**
 * The AbstractShareAttributesBroadcastReceiver is called by the Yoti App after
 * a share attribute process started by a third party app using this SDK. It will also be called
 * if the third party app didn't choose to make the call to their backend and let the SDK to it.
 */
public abstract class AbstractShareAttributesBroadcastReceiver extends BroadcastReceiver
        implements ShareAttributesContract.IShareAttributeBroadcastReceiver {

    protected Context mContext;
    private ShareAttributesContract.IShareAttributeManager mShareAttributeManager;
    private Uri mUri;

    public AbstractShareAttributesBroadcastReceiver() {
        super();
        mShareAttributeManager = new ShareAttributeManager(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;

        String action = intent.getAction();

        YotiSDKLogger.debug("Received intent with Action : " + action);

        String useCaseId = intent.getStringExtra(INTENT_EXTRA_USE_CASE_ID);
        String callbackUrl = intent.getStringExtra(INTENT_EXTRA_CALLBACK_URL);
        boolean isSuccess = intent.getBooleanExtra(INTENT_EXTRA_RESULT, false);
        byte[] response = intent.getByteArrayExtra(INTENT_EXTRA_RESPONSE);
        int statusCode = intent.getIntExtra(INTENT_EXTRA_HTTP_ERROR_CODE, -1);
        Throwable cause = (Throwable) intent.getSerializableExtra(INTENT_EXTRA_ERROR);

        if (mUri == null && callbackUrl != null) {
            mUri = Uri.parse(callbackUrl);
        }
        mShareAttributeManager.onReceive(useCaseId, action, callbackUrl, isSuccess, response, statusCode, cause);
    }

    /**
     * @param callbackRoot the root of the callback url
     * @param token        the token to access the shared attributes
     * @param fullUrl      the full url
     * @return a boolean is the call has been processed.
     */
    @Override
    public boolean receivedCallback(String useCaseId, String callbackRoot, String token, String fullUrl) {
        YotiSDKLogger.debug("Received callback : " + fullUrl);
        return onCallbackReceived(useCaseId, callbackRoot, token, fullUrl);
    }

    /**
     * Start the IntentService to call the callback url
     *
     * @param useCaseId the related use case id
     */
    @Override
    public void startBackendCall(String useCaseId) {
        YotiSDKLogger.debug("Calling third party backend for useCaseId : " + useCaseId);
        KernelSDKIntentService.startActionBackendCall(mContext, useCaseId);
    }

    /**
     * Called after a successful call to the callback url
     *
     * @param response the response obtain for calling the callback url
     */
    @Override
    public void successCallback(String useCaseId, byte[] response) {
        YotiSDKLogger.debug("Third party backend call successful");
        onCallbackSuccess(useCaseId, response);
    }

    /**
     * Called after a failed call to the callback url
     *
     * @param httpErrorCode The error code returned by your backend in case of an error when calling the callback url
     * @param error         The throwable created while trying to call your backend when calling the callback url
     * @param response      The response of your backend after calling the callback url
     */
    @Override
    public void errorCallback(String useCaseId, int httpErrorCode, Throwable error, byte[] response) {
        if (error instanceof ConnectException) {
            YotiSDKLogger.error("Third party backend call failed. Is your server started and listening?", error);
        } else {
            YotiSDKLogger.error("Third party backend call failed", error);
        }

        onCallbackError(useCaseId, httpErrorCode, error, response);
    }

    /**
     * @param callbackUrl
     * @return return the base url of the given url
     */
    @Override
    public String extractCallbackRoot(String callbackUrl) {
        return mUri.getScheme() + "://" + mUri.getHost();
    }

    /**
     * @param callbackUrl
     * @return the token query parameter
     */
    @Override
    public String extractToken(String callbackUrl) {
        return mUri.getQueryParameter(TOKEN_PARAM);
    }

    @Override
    public void shareFailed(String useCaseId) {
        YotiSDKLogger.error("The share attribute process as not been completed on the Yoti App");
        onShareFailed(useCaseId);
    }

    /**
     * This method will be called after a user successfully accepted to share the request attribute
     *
     * @param useCaseId    The related UseCaseId you defined for the scenarios.
     * @param callbackRoot the root url of the result of the Yoti Share
     * @param token        the token contained in the url of the result of the Yoti Share
     * @param fullUrl      The full url which allows you to retrieve the shared attributes via your backend
     * @return true if the call to your backend has been processed
     */
    public abstract boolean onCallbackReceived(String useCaseId, String callbackRoot, String token, String fullUrl);

    /**
     * This method will be called if a user didn't complete the share process on the Yoti App
     *
     * @param useCaseId The related UseCaseId you defined for the scenarios.
     */
    public abstract void onShareFailed(String useCaseId);

    /**
     * This method will be called if you decided to let the SDK make the call to your backend to retrieve the shared attributes
     * and if the call is successful
     *
     * @param useCaseId the related UseCaseId you defined for the scenarios.
     * @param response  the response from your backend
     */
    public abstract void onCallbackSuccess(String useCaseId, byte[] response);

    /**
     * This method will be called if you decided to let the SDK make the call to your backend to retrieve the shared attributes
     * and if the call is unsuccessful
     *
     * @param useCaseId     the related UseCaseId you defined for the scenarios.
     * @param httpErrorCode the status code from your backend
     * @param error         the error from your backend
     * @param response      the response from your backend
     */
    public abstract void onCallbackError(String useCaseId, int httpErrorCode, Throwable error, byte[] response);
}
