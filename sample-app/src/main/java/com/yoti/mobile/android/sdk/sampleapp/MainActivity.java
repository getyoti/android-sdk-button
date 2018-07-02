package com.yoti.mobile.android.sdk.sampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yoti.mobile.android.sdk.YotiSDKButton;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNoYotiAppException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final YotiSDKButton yotiSDKButton = (YotiSDKButton) findViewById(R.id.button);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
        final TextView message = (TextView)findViewById(R.id.text);

        yotiSDKButton.setOnYotiButtonClickListener(new YotiSDKButton.OnYotiButtonClickListener() {
            @Override
            public void onStartScenario() {
                yotiSDKButton.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                message.setText(null);
            }

            @Override
            public void onStartScenarioError(YotiSDKException cause) {
                yotiSDKButton.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                message.setText(R.string.loc_error_unknow);
            }
        });

        yotiSDKButton.setOnYotiAppNotInstalledListener(new YotiSDKButton.OnYotiAppNotInstalledListener() {
            @Override
            public void onYotiAppNotInstalledError(YotiSDKNoYotiAppException cause) {
                //The Yoti app is not installed, let's deal with it
            }
        });

        if (getIntent().hasExtra(ShareAttributesResultBroadcastReceiver.EXTRA_CANCELLED_BY_USER)) {
            yotiSDKButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            message.setText(R.string.loc_error_not_completed_on_yoti);
        }

        if (getIntent().hasExtra(ShareAttributesResultBroadcastReceiver.EXTRA_IS_FAILED)) {
            yotiSDKButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            message.setText(R.string.loc_error_unknow);
        }

        if (getIntent().hasExtra(ShareAttributesResultBroadcastReceiver.EXTRA_RESPONSE)) {
            String phone = getIntent().getStringExtra(ShareAttributesResultBroadcastReceiver.EXTRA_RESPONSE);
            message.setText(String.format(getString(R.string.loc_phone_number), phone));
        }
    }

}
