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
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNoYotiAppException;

/**
 * Custom {@link AppCompatButton} which will start its associated {@link
 * com.yoti.mobile.android.sdk.model.Scenario} when clicked.
 */
public final class YotiSDKButton extends YotiButtonContainer {

    private String mUseCaseId;
    private OnYotiButtonClickListener mOnYotiButtonClickListener;
    private OnYotiAppNotInstalledListener mOnYotiAppNotInstalledListener;
    private OnYotiCalledListener mOnYotiCalledListener;

    private ResultReceiver mYotiCallResultReceiver = new ResultReceiver(new Handler()) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (mOnYotiCalledListener != null) {
                mOnYotiCalledListener.onYotiCalled();
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

    public void setOnYotiAppNotInstalledListener(@Nullable OnYotiAppNotInstalledListener listener) {
        mOnYotiAppNotInstalledListener = listener;
    }

    public void setOnYotiCalledListener(@Nullable OnYotiCalledListener listener) {
        mOnYotiCalledListener = listener;
    }

    @Override
    protected void onSdkButtonClicked() {

        if (mOnYotiButtonClickListener != null) {
            mOnYotiButtonClickListener.onStartScenario();
        }

        try {
            YotiSDK.startScenario(getContext(), mUseCaseId, mOnYotiAppNotInstalledListener == null, mYotiCallResultReceiver);
        } catch (YotiSDKException cause) {

            YotiSDKLogger.error(cause.getMessage(), cause);

            if (mOnYotiAppNotInstalledListener != null && cause instanceof YotiSDKNoYotiAppException) {
                mOnYotiAppNotInstalledListener.onYotiAppNotInstalledError((YotiSDKNoYotiAppException) cause);
            } else if (mOnYotiButtonClickListener != null) {
                mOnYotiButtonClickListener.onStartScenarioError(cause);
            }
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
    }

    public interface OnYotiButtonClickListener {
        void onStartScenario();

        void onStartScenarioError(YotiSDKException cause);
    }

    public interface OnYotiAppNotInstalledListener {
        void onYotiAppNotInstalledError(YotiSDKNoYotiAppException cause);
    }

    public interface OnYotiCalledListener {
        void onYotiCalled();
    }
}
