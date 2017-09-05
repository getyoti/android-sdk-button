package com.yoti.mobile.android.sdk.sampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yoti.mobile.android.sdk.YotiSDKButton;
import com.yoti.mobile.android.sdk.YotiSDK;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNotValidScenarioException;
import com.yoti.mobile.android.sdk.model.CustomCertificate;
import com.yoti.mobile.android.sdk.model.Scenario;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupYotiShareScenario();

        final YotiSDKButton yotiSDKButton = (YotiSDKButton) findViewById(R.id.button);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
        final TextView message = (TextView)findViewById(R.id.text);

        yotiSDKButton.setOnYotiScenarioListener(new YotiSDKButton.OnYotiButtonClickListener() {
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

    private void setupYotiShareScenario() {
        CustomCertificate customCertificate = new CustomCertificate();
        customCertificate.setCertificateResourceId(R.raw.certificate);
        customCertificate.setAlias("test");
        customCertificate.setPassword("test123");
        customCertificate.setStoreName("TEST");

        try {
            Scenario scenario = new Scenario.Builder()
                    .setUseCaseId("get_user_phone_1")
                    .setClientSDKId("111f9ff2-baf9-48c6-8b44-bebf98a78c82")
                    .setScenarioId("563aa47f-840c-4de9-979a-18196fb77b0d")
                    .setCallbackAction("com.test.app.YOTI_CALLBACK")
                    .setBackendCallbackAction("com.test.app.BACKEND_CALLBACK")
                    .setCustomCertificate(customCertificate)
                    .create();

            YotiSDK.addScenario(scenario);

            YotiSDK.enableSDKLogging(true);

        } catch (YotiSDKNotValidScenarioException e) {
            e.printStackTrace();
        }

    }
}
