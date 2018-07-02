package com.yoti.mobile.android.sampleapp2;

import android.app.Application;

import com.yoti.mobile.android.sdk.YotiSDK;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNotValidScenarioException;
import com.yoti.mobile.android.sdk.model.Scenario;

public class SampleApp2 extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

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
