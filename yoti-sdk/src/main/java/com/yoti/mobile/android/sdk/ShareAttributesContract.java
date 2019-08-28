package com.yoti.mobile.android.sdk;


import androidx.annotation.Nullable;

public interface ShareAttributesContract {

    interface IShareAttributeBroadcastReceiver {

        void startBackendCall(String useCaseId);

        boolean receivedCallback(String useCaseId, String callbackRoot, String token, String fullUrl);

        void successCallback(String useCaseId, byte[] response);

        void errorCallback(String useCaseId, int httpErrorCode, Throwable error, byte[] response);

        String extractCallbackRoot(String callbackUrl);

        String extractToken(String callbackUrl);

        void shareFailed(String useCaseId);
    }

    interface IShareAttributeManager {

        void onReceive(@Nullable String useCaseId, @Nullable String action, @Nullable String callbackUrl, boolean isSuccess, @Nullable byte[] response, int errorCode, @Nullable Throwable cause);

    }


}
