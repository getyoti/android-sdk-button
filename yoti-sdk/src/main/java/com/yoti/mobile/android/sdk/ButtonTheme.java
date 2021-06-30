package com.yoti.mobile.android.sdk;

enum ButtonTheme {
    THEME_YOTI_UK(0),
    THEME_YOTI_GLOBAL(1),
    THEME_EASYID(2),
    THEME_PARTNERSHIP(3);

    private final int themeValue;

    ButtonTheme(final int value) {
        this.themeValue = value;
    }

    public int getValue() {
        return themeValue;
    }

    public static ButtonTheme fromValue(int themeValue) {
        ButtonTheme buttonTheme = THEME_YOTI_UK;
        for(ButtonTheme theme: values()) {
            if(theme.themeValue == themeValue) {
                buttonTheme = theme;
                break;
            }
        }
        return buttonTheme;
    }
}