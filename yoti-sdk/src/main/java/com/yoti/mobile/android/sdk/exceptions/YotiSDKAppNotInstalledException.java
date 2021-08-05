package com.yoti.mobile.android.sdk.exceptions;

public class YotiSDKAppNotInstalledException extends YotiSDKException {

    AppType unavailableApp;

    public YotiSDKAppNotInstalledException(AppType unavailableApp, String message) {
        super(message);
        this.unavailableApp = unavailableApp;
    }
}


