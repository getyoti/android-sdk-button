package com.yoti.mobile.android.sdk.sampleapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yoti.mobile.android.sdk.YotiSDKButton;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private YotiSDKButton mYotiSDKButton;
    private ProgressBar mProgress;
    private TextView mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mYotiSDKButton = findViewById(R.id.button);
        mProgress = findViewById(R.id.progress);
        mMessage = findViewById(R.id.text);

        mYotiSDKButton.setOnYotiButtonClickListener(new YotiSDKButton.OnYotiButtonClickListener() {
            @Override
            public void onStartScenario() {
                mYotiSDKButton.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
                mMessage.setText(null);
            }

            @Override
            public void onStartScenarioError(YotiSDKException cause) {
                mYotiSDKButton.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
                mMessage.setText(R.string.loc_error_unknow);
            }
        });

        mYotiSDKButton.setOnAppNotInstalledListener((cause, appURL) -> {
            // App is not installed, need to call browser intent with app download link
            mYotiSDKButton.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            launchAppUrl(appURL);
        });

        mYotiSDKButton.setOnAppCalledListener(new YotiSDKButton.OnAppCalledListener() {
            @Override
            public void onAppCalled() {
                // Restore the original state
                mYotiSDKButton.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.GONE);
            }
        });
    }

    // required app is not installed, launch app url to download the app
    private void launchAppUrl(String redirectURL) {
        Uri webpage = Uri.parse(redirectURL);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData(intent);
    }

    private void processExtraData(Intent intent) {
        if (intent.hasExtra(ShareAttributesResultBroadcastReceiver.EXTRA_CANCELLED_BY_USER)) {
            mYotiSDKButton.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            mMessage.setText(R.string.loc_error_not_completed_on_yoti);
        }

        if (intent.hasExtra(ShareAttributesResultBroadcastReceiver.EXTRA_IS_FAILED)) {
            mYotiSDKButton.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
            mMessage.setText(R.string.loc_error_unknow);
        }

        if (intent.hasExtra(ShareAttributesResultBroadcastReceiver.EXTRA_RESPONSE)) {
            String response = getIntent().getStringExtra(ShareAttributesResultBroadcastReceiver.EXTRA_RESPONSE);
            if (response != null) {
                mMessage.setText(R.string.loc_success_status);
            }
        }

        if (intent.hasExtra(ShareAttributesResultBroadcastReceiver.EXTRA_LOADING)) {
            mMessage.setText(R.string.loc_loading_status);
        }
    }
}
