package com.yoti.mobile.android.sdk;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//TODO: Need to handle the layout based on selected them
public class YotiButtonContainer extends RelativeLayout {

    private static final int DISPLAY_YOTI_UK = 0;
    private static final int DISPLAY_YOTI_GLOBAL = 1;
    private static final int DISPLAY_EASY_ID = 2;
    private static final int DISPLAY_PARTNERSHIP = 3;

    public YotiButtonContainer(@NonNull final Context context) {
        super(context);
        init(context,null);
    }

    public YotiButtonContainer(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public YotiButtonContainer(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
       LayoutInflater.from(context).inflate(R.layout.yoti_sdk_button_layout, this, true);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.YotiSDKButton, 0, 0);
        ButtonTheme theme = ButtonTheme.fromValue(typedArray.getInt(R.styleable.YotiSDKButton_buttonTheme, ButtonTheme.THEME_YOTI_UK.getValue()));
        setButtonTheme(theme);
    }

    private void setButtonTheme(ButtonTheme theme) {
        ViewFlipper viewFlipper = findViewById(R.id.sdkButtonRootLayout);
        switch (theme) {
            case THEME_YOTI_UK:
                viewFlipper.setDisplayedChild(DISPLAY_YOTI_UK);
                break;
            case THEME_YOTI_GLOBAL:
                viewFlipper.setDisplayedChild(DISPLAY_YOTI_GLOBAL);
                break;
            case THEME_EASYID:
                viewFlipper.setDisplayedChild(DISPLAY_EASY_ID);
                break;
            case THEME_PARTNERSHIP:
                handleDarkThemeForPartnership();
                viewFlipper.setDisplayedChild(DISPLAY_PARTNERSHIP);
                break;
        }
    }

    private void handleDarkThemeForPartnership() {
        ViewFlipper viewFlipper = findViewById(R.id.sdkButtonRootLayout);
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                break;
        }
    }

    public void setText(String text){
        //Nothing to do here
    }

    @Override
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
    }
}
