package com.yoti.mobile.android.sdk;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

import com.yoti.mobile.android.commons.ui.widget.YotiButton;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;

/**
 * Custom {@link AppCompatButton} which will start its associated {@link com.yoti.mobile.android.sdk.model.Scenario} when clicked.
 */
public class YotiSDKButton extends YotiButton implements View.OnClickListener {

    private String mUseCaseId;
    private OnYotiButtonClickListener mOnYotiButtonClickListener;

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

        setOnClickListener(this);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(this);
    }

    public void setOnYotiScenarioListener(@Nullable OnYotiButtonClickListener l) {
        mOnYotiButtonClickListener = l;
    }

    @Override
    public void onClick(View v) {

        if (mOnYotiButtonClickListener != null) {
            mOnYotiButtonClickListener.onStartScenario();
        }

        try {
            YotiSDK.startScenario(getContext(), mUseCaseId);
        } catch (YotiSDKException cause) {

            YotiSDKLogger.error(cause.getMessage(), cause);

            if (mOnYotiButtonClickListener != null) {
                mOnYotiButtonClickListener.onStartScenarioError(cause);
            }
        }
    }

    public interface OnYotiButtonClickListener {
        void onStartScenario();

        void onStartScenarioError(YotiSDKException cause);
    }
}
