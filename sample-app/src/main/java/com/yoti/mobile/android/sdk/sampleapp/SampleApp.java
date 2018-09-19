package com.yoti.mobile.android.sdk.sampleapp;

import android.app.Application;
import android.util.Log;

import com.yoti.mobile.android.sdk.YotiSDK;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNotValidScenarioException;
import com.yoti.mobile.android.sdk.model.Scenario;

import static android.content.ContentValues.TAG;

public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Scenario scenario = new Scenario.Builder()
                    .setUseCaseId("get_user_phone_1")
                    .setClientSDKId("d10b19d3-fa50-48ab-bd8c-f5a099205e6c")
                    .setScenarioId("17807359-a933-4b77-baa2-3c2fdb5608f2")
                    .setCallbackAction("com.test.app.YOTI_CALLBACK")
                    .setBackendCallbackAction("com.test.app.BACKEND_CALLBACK")
                    .create();

            YotiSDK.addScenario(scenario);

            YotiSDK.enableSDKLogging(true);

        } catch (YotiSDKNotValidScenarioException e) {
            Log.e(TAG, "Invalid scenario!!", e);
        }
    }
}
