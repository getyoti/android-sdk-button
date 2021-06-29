package com.yoti.mobile.android.sdk;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//TODO: Need to handle the layout based on selected them
public class YotiButtonContainer extends RelativeLayout {

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

    public void init(Context context,AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.yoti_sdk_button_layout, this, true);
    }

    public void setText(String text){
        //Nothing to do here
    }

    @Override
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
    }
}
