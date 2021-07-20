package com.yoti.mobile.android.sdk.exceptions;

public class YotiSDKAppNotInstalledException extends YotiSDKException {

    AppNotInstalledErrorCode errorCode;

    public YotiSDKAppNotInstalledException(AppNotInstalledErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}


