package com.yoti.mobile.android.sdk;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

//TODO: Need to handle the layout based on selected them
public class YotiButtonContainer extends RelativeLayout {

    private static final int DISPLAY_YOTI_UK = 0;
    private static final int DISPLAY_YOTI_GLOBAL = 1;
    private static final int DISPLAY_EASY_ID = 2;
    private static final int DISPLAY_PARTNERSHIP = 3;

    public YotiButtonContainer(@NonNull final Context context) {
        super(context);
        init(context, null);
    }

    public YotiButtonContainer(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public YotiButtonContainer(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.yoti_sdk_button_layout, this, true);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.YotiSDKButton, 0, 0);
        ButtonTheme theme = ButtonTheme.fromValue(typedArray.getInt(R.styleable.YotiSDKButton_buttonTheme, ButtonTheme.THEME_YOTI.getValue()));
        setButtonTheme(theme);
    }

    private void setButtonTheme(ButtonTheme theme) {
        ViewFlipper viewFlipper = findViewById(R.id.sdkButtonRootLayout);
        switch (theme) {
            case THEME_YOTI:
                applyYotiTheme();
                break;
            case THEME_EASYID:
                viewFlipper.setDisplayedChild(DISPLAY_EASY_ID);
                break;
            case THEME_PARTNERSHIP:
                viewFlipper.setDisplayedChild(DISPLAY_PARTNERSHIP);
                break;
        }
    }

    private void applyYotiTheme() {
        ViewFlipper viewFlipper = findViewById(R.id.sdkButtonRootLayout);
        if (Locale.getDefault().getISO3Country().equalsIgnoreCase(Locale.UK.getISO3Country())) {
            viewFlipper.setDisplayedChild(DISPLAY_YOTI_UK);
        } else {
            viewFlipper.setDisplayedChild(DISPLAY_YOTI_GLOBAL);
        }
    }

    @Override
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
    }
}
