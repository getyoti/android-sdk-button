package com.yoti.mobile.android.sdk.exceptions;

public enum AppType {
    YOTI_APP("Yoti"),
    EASY_ID_APP("EasyId"),
    PARTNERSHIP_APP("Yoti / EasyId");

    private final String appName;

    AppType(final String value) {
        this.appName = value;
    }

    public String getValue() {
        return appName;
    }
}
