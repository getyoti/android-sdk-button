package com.yoti.mobile.android.sdk;

enum ButtonTheme {
    THEME_YOTI(0),
    THEME_EASYID(1),
    THEME_PARTNERSHIP(2);

    private final int themeValue;

    ButtonTheme(final int value) {
        this.themeValue = value;
    }

    public int getValue() {
        return themeValue;
    }

    public static ButtonTheme fromValue(int themeValue) {
        ButtonTheme buttonTheme = THEME_YOTI;
        for (ButtonTheme theme : values()) {
            if (theme.themeValue == themeValue) {
                buttonTheme = theme;
                break;
            }
        }
        return buttonTheme;
    }
}