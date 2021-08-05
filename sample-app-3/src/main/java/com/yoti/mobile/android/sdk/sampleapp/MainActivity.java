package com.yoti.mobile.android.sdk.sampleapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.yoti.mobile.android.sdk.YotiSDK;
import com.yoti.mobile.android.sdk.YotiSDKButton;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNotValidScenarioException;
import com.yoti.mobile.android.sdk.model.Scenario;
import com.yoti.mobile.android.sdk.sampleapp3.R;

import static com.yoti.mobile.android.sdk.sampleapp.ShareAttributesResultBroadcastReceiver.EXTRA_FULL_URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int YOTI_SDK_BUTTON = 0;
    private static final int EASY_ID_SDK_BUTTON = 1;
    private static final int PARTNERSHIP_SDK_BUTTON = 2;

    private YotiSDKButton mYotiSDKButton;
    private View mStatusContainer;
    private TextView mStatusHeader;
    private TextView mStatusMessage;
    private TextView mScenarioEntry;
    private TextView mSdkEntry;
    private TextView mButtonTextEntry;
    private TextView mUseCaseEntry;
    private ViewFlipper mSdkButtonViewFlipper;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        YotiSDK.enableSDKLogging(true);

        mStatusContainer = findViewById(R.id.resultContainer);
        mStatusHeader = findViewById(R.id.resultHeader);
        mStatusMessage = findViewById(R.id.resultStatus);
        mScenarioEntry = findViewById(R.id.scenarioIdText);
        mSdkEntry = findViewById(R.id.sdkIdText);
        mButtonTextEntry = findViewById(R.id.buttonLabelText);
        mUseCaseEntry = findViewById(R.id.useCaseIdText);

        mSdkEntry.addTextChangedListener(scenarioUpdateListener);
        mScenarioEntry.addTextChangedListener(scenarioUpdateListener);
        mUseCaseEntry.addTextChangedListener(scenarioUpdateListener);
        mButtonTextEntry.addTextChangedListener(buttonTextListener);

        mSdkButtonViewFlipper = findViewById(R.id.sdkButtonViewFlipper);
        handleSDKButtonThemeSelection(PARTNERSHIP_SDK_BUTTON);

        RadioGroup buttonSelectionRadioGroup = findViewById(R.id.buttonSelectionRadioGroup);
        buttonSelectionRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.yotiRadioButton:
                    handleSDKButtonThemeSelection(YOTI_SDK_BUTTON);
                break;
                case R.id.easyIdRadioButton:
                    handleSDKButtonThemeSelection(EASY_ID_SDK_BUTTON);
                    break;
                default:
                    handleSDKButtonThemeSelection(PARTNERSHIP_SDK_BUTTON);
                    break;
            }
        });
    }

    private void handleSDKButtonThemeSelection(int selection) {
        switch (selection) {
            case YOTI_SDK_BUTTON:
                YotiSDKButton yotiSdkButton = findViewById(R.id.yotiSDKButton);
                mSdkButtonViewFlipper.setDisplayedChild(YOTI_SDK_BUTTON);
                initSDKButton(yotiSdkButton);
                break;
            case EASY_ID_SDK_BUTTON:
                YotiSDKButton easyIdSdkButton = findViewById(R.id.easyIdSDKButton);
                mSdkButtonViewFlipper.setDisplayedChild(EASY_ID_SDK_BUTTON);
                initSDKButton(easyIdSdkButton);
                break;
            case PARTNERSHIP_SDK_BUTTON:
                YotiSDKButton partnershipSDKButton = findViewById(R.id.partnershipSDKButton);
                mSdkButtonViewFlipper.setDisplayedChild(PARTNERSHIP_SDK_BUTTON);
                initSDKButton(partnershipSDKButton);
                break;
        }
    }

    private void initSDKButton(YotiSDKButton yotiSDKButton) {
        mYotiSDKButton = yotiSDKButton;
        createScenario();

        yotiSDKButton.setOnYotiButtonClickListener(new YotiSDKButton.OnYotiButtonClickListener() {
            @Override
            public void onStartScenario() {
                yotiSDKButton.setVisibility(View.GONE);
                showStatus(true, R.string.result_status_startScenario);
            }

            @Override
            public void onStartScenarioError(YotiSDKException cause) {
                yotiSDKButton.setVisibility(View.VISIBLE);
                showStatus(false, R.string.result_status_startScenarioError);
            }
        });

        yotiSDKButton.setOnAppNotInstalledListener((cause, appURL) -> {
            yotiSDKButton.setVisibility(View.VISIBLE);
            showStatus(false, R.string.result_status_appNotInstalled);
            launchAppUrl(appURL);
        });

        mYotiSDKButton.setOnAppCalledListener(new YotiSDKButton.OnAppCalledListener() {
            @Override
            public void onAppCalled() {
                // Restore the original state
                mYotiSDKButton.setVisibility(View.VISIBLE);
                showStatus(true, R.string.result_status_openYoti);
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
            showStatus(true, R.string.result_status_loading, "\n\n FULL_URL - "+intent.getStringExtra(EXTRA_FULL_URL));
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

    private void showStatus(boolean success, @StringRes int messageResId, String messageText) {
        showStatus(success, messageResId);

        mStatusMessage.setText(String.format("%s %s", getString(messageResId), messageText));
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
