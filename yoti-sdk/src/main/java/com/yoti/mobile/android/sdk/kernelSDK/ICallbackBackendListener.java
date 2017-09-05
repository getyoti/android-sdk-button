package com.yoti.mobile.android.sdk.kernelSDK;

public interface ICallbackBackendListener {

    void onSuccess(byte[] response);

    void onError(int httpErrorCode, Throwable error, byte[] response);
}
