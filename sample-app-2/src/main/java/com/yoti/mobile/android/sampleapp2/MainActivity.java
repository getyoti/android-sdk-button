package com.yoti.mobile.android.sampleapp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yoti.mobile.android.sdk.YotiSDK;
import com.yoti.mobile.android.sdk.YotiSDKButton;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNotValidScenarioException;
import com.yoti.mobile.android.sdk.model.Scenario;
import com.yoti.sampleapp2.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final YotiSDKButton yotiSDKButton = findViewById(R.id.yoti_button);
        final ProgressBar progress = findViewById(R.id.progress);
        final TextView message = findViewById(R.id.text);

        createYoti();

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
    }


    private void createYoti() {
        Scenario scenario = null;
        try {
            scenario = new Scenario.Builder()
                    .setUseCaseId("yoti_btn_1")
                    .setClientSDKId("d28feaf4-d62d-40e3-88ae-d619e9a5b906")
                    .setScenarioId("60b8e997-4a5c-40b2-86e8-29c4521b7015")
                    .setCallbackAction("com.yoti.services.CALLBACK")
                    .create();
        } catch (YotiSDKNotValidScenarioException e) {
            e.printStackTrace();
        }

        YotiSDK.addScenario(scenario);
    }

}


