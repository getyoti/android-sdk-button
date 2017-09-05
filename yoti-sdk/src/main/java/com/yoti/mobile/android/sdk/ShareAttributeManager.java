package com.yoti.mobile.android.sdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yoti.mobile.android.sdk.model.Scenario;

/**
 * This class host the logic of handling the different actions received by a {@link ShareAttributesContract.IShareAttributeBroadcastReceiver}
 */
public class ShareAttributeManager implements ShareAttributesContract.IShareAttributeManager {

    private ShareAttributesContract.IShareAttributeBroadcastReceiver mShareAttributeBroadcastReceiver;

    public ShareAttributeManager(ShareAttributesContract.IShareAttributeBroadcastReceiver shareAttributeBroadcastReceiver) {
        mShareAttributeBroadcastReceiver = shareAttributeBroadcastReceiver;
    }

    /**
     * Method called by a {@link ShareAttributesContract.IShareAttributeBroadcastReceiver} when it receive a new Intent.
     *
     * @param useCaseId The current use case Id
     * @param action The Action which trigerred the IShareAttributeBroadcastReceiver
     * @param callbackUrl The url to access the value of the shared attributes (it will be the url to access your backend)
     * @param isSuccess True if the SDK managed to successfully call your backend with the callback url
     * @param response The response of your backend after calling the callback url
     * @param errorCode The error code returned by your backend in case of an error when calling the callback url
     * @param cause The throwable created while trying to call your backend when calling the callback url
     */
    @Override
    public void onReceive(@Nullable String useCaseId, @NonNull String action, @Nullable String callbackUrl,
                          boolean isSuccess, @Nullable byte[] response, int errorCode, @Nullable Throwable cause) {

        Scenario currentScenario = YotiSDK.getScenario(useCaseId);

        if (currentScenario == null) {
            YotiSDKLogger.error("No Scenario available for the provided use case id");
            return;
        }

        if (action.equals(currentScenario.getCallbackAction())) {
            handleYotiAppCallback(useCaseId, callbackUrl, currentScenario);

        } else if (action.equals(currentScenario.getCallbackBackendAction())) {
            if (isSuccess) {
                mShareAttributeBroadcastReceiver.successCallback(useCaseId, response);
            } else {
                mShareAttributeBroadcastReceiver.errorCallback(useCaseId, errorCode, cause, response);
            }
        }
    }

    /**
     * This method is triggered by the {@link AbstractShareAttributesBroadcastReceiver} after receiving the Action from the Yoti App with the callback url.
     * First we check if the call to the callback url is processed by the inheriting class, if it's not we start an IntentService to do it.
     *
     * @param useCaseId The current use case Id
     * @param callbackUrl The url to access the value of the shared attributes (it will be the url to access your backend)
     * @param currentScenario the scenario link to the current use case Id
     */
    private void handleYotiAppCallback(String useCaseId, String callbackUrl, Scenario currentScenario) {
        currentScenario.setCallbackBackendUrl(callbackUrl);

        String callbackRoot = mShareAttributeBroadcastReceiver.extractCallbackRoot(callbackUrl);
        String token = mShareAttributeBroadcastReceiver.extractToken(callbackUrl);

        if (TextUtils.isEmpty(token)) {
            mShareAttributeBroadcastReceiver.shareFailed(useCaseId);
        }

        boolean isProcessed = mShareAttributeBroadcastReceiver.receivedCallback(useCaseId, callbackRoot, token, callbackUrl);

        if (!isProcessed) {

            if (TextUtils.isEmpty(currentScenario.getCallbackBackendAction())) {
                YotiSDKLogger.error("The scenario " + currentScenario.getUseCaseId() + " does not have a callback backend action defined therefore the SDK cannot perform the action");
                return;
            }

            mShareAttributeBroadcastReceiver.startBackendCall(useCaseId);
        }
    }

}
