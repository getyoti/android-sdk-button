package com.yoti.mobile.android.sdk.exceptions;

public class YotiSDKAppNotInstalledException extends YotiSDKException {

    AppType notInstalledApp;

    public YotiSDKAppNotInstalledException(AppType notInstalledApp, String message) {
        super(message);
        this.notInstalledApp = notInstalledApp;
    }
}


