package com.yoti.mobile.android.sdk;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKAppNotInstalledException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNoYotiAppException;

/**
 * Custom {@link AppCompatButton} which will start its associated {@link
 * com.yoti.mobile.android.sdk.model.Scenario} when clicked.
 */
public final class YotiSDKButton extends YotiButtonContainer {

    private String mUseCaseId;
    private OnYotiButtonClickListener mOnYotiButtonClickListener;
    private OnYotiAppNotInstalledListener mOnYotiAppNotInstalledListener;
    private OnAppNotInstalledListener mOnAppNotInstalledListener;
    private OnYotiCalledListener mOnYotiCalledListener;
    private OnAppCalledListener mOnAppCalledListener;

    private ResultReceiver mYotiCallResultReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (mOnYotiCalledListener != null) {
                mOnYotiCalledListener.onYotiCalled();
            }

            if (mOnAppCalledListener != null) {
                mOnAppCalledListener.onAppCalled();
            }
        }
    };

    public YotiSDKButton(Context context) {
        super(context);
        init(null);
    }

    public YotiSDKButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public YotiSDKButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.YotiSDKButton,
                    0, 0);

            try {
                mUseCaseId = a.getString(R.styleable.YotiSDKButton_useCaseId);
            } finally {
                a.recycle();
            }
        }

        setLeftRigthPadding();
    }

    public void setUseCaseId(String useCaseId) {
        mUseCaseId = useCaseId;
    }
    
    private void setLeftRigthPadding(){
        int paddingDp = 8;
        float density = getResources().getDisplayMetrics().density;
        int paddingPixel = (int)(paddingDp * density);
        setPadding(paddingPixel,0,paddingPixel,0);
    }

    public void setOnYotiButtonClickListener(@Nullable OnYotiButtonClickListener l) {
        mOnYotiButtonClickListener = l;
    }

    @Deprecated
    public void setOnYotiScenarioListener(@Nullable OnYotiButtonClickListener l) {
        YotiSDKLogger.warning("The method 'setOnYotiScenarioListener' is now deprecated. Please use 'setOnYotiButtonClickListener' instead.");
        mOnYotiButtonClickListener = l;
    }

    @Deprecated
    public void setOnYotiAppNotInstalledListener(@Nullable OnYotiAppNotInstalledListener listener) {
        YotiSDKLogger.warning("The method 'setOnYotiAppNotInstalledListener' is now deprecated. Please use 'setOnAppNotInstalledListener' instead.");
        mOnYotiAppNotInstalledListener = listener;
    }

    /**
     * Listener to notify if the app is not installed based on the selected theme
     *
     * Theme_Yoti / Theme_Partnership - Either Yoti or EasyId app should be installed
     * Theme_EasyId - EasyId app should be installed
     *
     * @param listener will notify when the app is not installed by providing the exception and
     * the respective app url to navigate the user to browser with url to download the app
     */
    public void setOnAppNotInstalledListener(@Nullable OnAppNotInstalledListener listener) {
        mOnAppNotInstalledListener = listener;
    }

    public void setOnYotiCalledListener(@Nullable OnYotiCalledListener listener) {
        YotiSDKLogger.warning("The method 'setOnYotiCalledListener' is now deprecated. Please use 'setOnAppCalledListener' instead.");
        mOnYotiCalledListener = listener;
    }

    public void setOnAppCalledListener(@Nullable OnAppCalledListener listener) {
        mOnAppCalledListener = listener;
    }

    @Override
    protected void onSdkButtonClicked() {

        if (mOnYotiButtonClickListener != null) {
            mOnYotiButtonClickListener.onStartScenario();
        }

        try {
            YotiSDK.startScenario(getContext(),
                    mUseCaseId,
                    mOnYotiAppNotInstalledListener == null || mOnAppNotInstalledListener == null,
                    mYotiCallResultReceiver,
                    getSdkButtonTheme());
        } catch (YotiSDKException cause) {

            YotiSDKLogger.error(cause.getMessage(), cause);

            if (mOnAppNotInstalledListener != null && cause instanceof YotiSDKAppNotInstalledException) {
                String appUrl = getAppUrl();
                YotiSDKLogger.warning("App not installed, open the browser to get the app - " + appUrl);
                mOnAppNotInstalledListener.onAppNotInstalled((YotiSDKAppNotInstalledException) cause, appUrl);
            } else if (mOnYotiAppNotInstalledListener != null && cause instanceof YotiSDKAppNotInstalledException) {
                mOnYotiAppNotInstalledListener.onYotiAppNotInstalledError(new YotiSDKNoYotiAppException("App not installed"));
            } else if (mOnYotiButtonClickListener != null) {
                mOnYotiButtonClickListener.onStartScenarioError(cause);
            }
        }
    }

    private String getAppUrl() {
        String appUrl;
        switch (getSdkButtonTheme()) {
            case THEME_YOTI:
                appUrl = YotiAppDefs.YOTI_APP_URL;
                break;
            case THEME_EASYID:
                appUrl = YotiAppDefs.EASY_ID_APP_URL;
                break;
            default:
                appUrl = YotiAppDefs.PARTNERSHIP_APP_URL;
                break;
        }
        return appUrl;
    }

    public interface OnYotiButtonClickListener {
        void onStartScenario();

        void onStartScenarioError(YotiSDKException cause);
    }

    public interface OnYotiAppNotInstalledListener {
        void onYotiAppNotInstalledError(YotiSDKNoYotiAppException cause);
    }

    public interface OnAppNotInstalledListener {
        void onAppNotInstalled(YotiSDKAppNotInstalledException cause, String appURL);
    }

    public interface OnYotiCalledListener {
        void onYotiCalled();
    }

    public interface OnAppCalledListener {
        void onAppCalled();
    }
}
