package com.yoti.mobile.android.sdk.sampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.yoti.mobile.android.sdk.YotiSDK;
import com.yoti.mobile.android.sdk.YotiSDKButton;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNoYotiAppException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNotValidScenarioException;
import com.yoti.mobile.android.sdk.model.Scenario;
import com.yoti.mobile.android.sdk.sampleapp3.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private YotiSDKButton mYotiSDKButton;
    private View mStatusContainer;
    private TextView mStatusHeader;
    private TextView mStatusMessage;
    private TextView mScenarioEntry;
    private TextView mSdkEntry;
    private TextView mButtonTextEntry;
    private TextView mUseCaseEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        YotiSDK.enableSDKLogging(true);

        mYotiSDKButton = findViewById(R.id.button);
        mStatusContainer = findViewById(R.id.resultContainer);
        mStatusHeader = findViewById(R.id.resultHeader);
        mStatusMessage = findViewById(R.id.resultStatus);
        mScenarioEntry = findViewById(R.id.scenarioIdText);
        mSdkEntry = findViewById(R.id.sdkIdText);
        mButtonTextEntry = findViewById(R.id.buttonLabelText);
        mUseCaseEntry = findViewById(R.id.useCaseIdText);
        createScenario();

        mSdkEntry.addTextChangedListener(scenarioUpdateListener);
        mScenarioEntry.addTextChangedListener(scenarioUpdateListener);
        mUseCaseEntry.addTextChangedListener(scenarioUpdateListener);
        mButtonTextEntry.addTextChangedListener(buttonTextListener);

        mYotiSDKButton.setOnYotiButtonClickListener(new YotiSDKButton.OnYotiButtonClickListener() {
            @Override
            public void onStartScenario() {
                mYotiSDKButton.setVisibility(View.GONE);
                showStatus(true, R.string.result_status_startScenario);
            }

            @Override
            public void onStartScenarioError(YotiSDKException cause) {
                mYotiSDKButton.setVisibility(View.VISIBLE);
                showStatus(false, R.string.result_status_startScenarioError);
            }
        });

        mYotiSDKButton.setOnYotiAppNotInstalledListener(new YotiSDKButton.OnYotiAppNotInstalledListener() {
            @Override
            public void onYotiAppNotInstalledError(YotiSDKNoYotiAppException cause) {
                //The Yoti app is not installed, let's deal with it
                mYotiSDKButton.setVisibility(View.VISIBLE);
                showStatus(false, R.string.result_status_appNotInstalled);
            }
        });

        mYotiSDKButton.setOnYotiCalledListener(new YotiSDKButton.OnYotiCalledListener() {
            @Override
            public void onYotiCalled() {
                // Restore the original state
                mYotiSDKButton.setVisibility(View.VISIBLE);
                showStatus(true, R.string.result_status_openYoti);
            }
        });
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
            showStatus(false, R.string.result_status_cancel);
        }

        if (intent.hasExtra(ShareAttributesResultBroadcastReceiver.EXTRA_IS_FAILED)) {
            mYotiSDKButton.setVisibility(View.VISIBLE);
            showStatus(false, R.string.result_status_fail);
        }

        if (intent.hasExtra(ShareAttributesResultBroadcastReceiver.EXTRA_RESPONSE)) {
            String response = getIntent().getStringExtra(ShareAttributesResultBroadcastReceiver.EXTRA_RESPONSE);
            if (response != null) {
                showStatus(true, R.string.result_status_success);
            }
        }

        if (intent.hasExtra(ShareAttributesResultBroadcastReceiver.EXTRA_LOADING)) {
            showStatus(true, R.string.result_status_loading);
        }
    }

    private void createScenario() {
        hideStatus();
        mYotiSDKButton.setVisibility(View.INVISIBLE);

        String sdkId = mSdkEntry.getText().toString();
        String scenarioId = mScenarioEntry.getText().toString();
        String useCaseId = mUseCaseEntry.getText().toString();

        if (TextUtils.isEmpty(sdkId) || TextUtils.isEmpty(scenarioId) || TextUtils.isEmpty(useCaseId)) {
            return;
        }

        Scenario scenario = null;

        try {
            scenario = new Scenario.Builder()
                    .setUseCaseId(useCaseId)
                    .setClientSDKId(sdkId)
                    .setScenarioId(scenarioId)
                    .setCallbackAction("com.test.app.YOTI_CALLBACK")
                    .setBackendCallbackAction("com.test.app.BACKEND_CALLBACK")
                    .create();
        } catch (YotiSDKNotValidScenarioException e) {
            e.printStackTrace();
        }

        YotiSDK.addScenario(scenario);
        mYotiSDKButton.setUseCaseId(useCaseId);
        mYotiSDKButton.setVisibility(View.VISIBLE);
    }

    private void showStatus(boolean success, @StringRes int message) {
        mStatusContainer.setVisibility(View.VISIBLE);
        if (success) {
            mStatusContainer.setBackgroundColor(getResources().getColor(R.color.yoti_green));
            mStatusHeader.setText(R.string.result_header_success);
        }else {
            mStatusContainer.setBackgroundColor(getResources().getColor(R.color.yoti_red));
            mStatusHeader.setText(R.string.result_header_error);
        }

        mStatusMessage.setText(message);
    }

    private void hideStatus() {
        mStatusContainer.setVisibility(View.INVISIBLE);
    }

    private TextWatcher scenarioUpdateListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

        }

        @Override
        public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

        }

        @Override
        public void afterTextChanged(final Editable s) {
            createScenario();
        }
    };

    private TextWatcher buttonTextListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        }

        @Override
        public void afterTextChanged(final Editable s) {

        }
    };
}
