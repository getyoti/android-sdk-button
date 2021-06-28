package com.yoti.mobile.android.sdk

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.yoti.mobile.android.sdk.R.layout

//TODO: Need to handle the layout based on selected them
open class YotiButtonContainer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, layout.yoti_sdk_button_layout, this)
    }
}