package com.yoti.mobile.android.sdk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.test.suitebuilder.annotation.SmallTest;

import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKMinVersionException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNoYotiAppException;
import com.yoti.mobile.android.sdk.model.Scenario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class YotiSDKTest {

    @Mock
    Context mMockContext;

    @Mock
    PackageManager mMockPackageManager;

    @Mock
    PackageInfo mMockYotiAppPackageInfo;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {

        Mockito.when(mMockContext.getPackageManager()).thenReturn(mMockPackageManager);

        try {
            Mockito.when(mMockPackageManager.getPackageInfo(YotiAppDefs.YOTI_APP_PACKAGE, 0)).thenReturn(mMockYotiAppPackageInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mMockYotiAppPackageInfo.versionCode = YotiAppDefs.MIN_VERSION_YOTI_APP_REQUIRED + 1;

        YotiSDK.clear();
    }

    @Test
    public void testStartScenario_withNotInitiatedSDK() throws Throwable {
        exception.expect(YotiSDKException.class);
        exception.expectMessage("SDK not initialised");

        YotiSDK.startScenario(mMockContext, "a_scenario", true);
    }

    @Test
    public void testStartScenario_withNotScenariosSDK() throws Throwable {
        exception.expect(YotiSDKException.class);
        exception.expectMessage("No scenario available for use case id : a_scenario");

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("b_scenario")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("123")
                .setBackendCallbackAction("123")
                .create();

        YotiSDK.addScenario(scenario);
        YotiSDK.startScenario(mMockContext, "a_scenario", true);
    }

    @Test
    public void testStartScenario_withWrongScenario() throws Throwable {
        exception.expect(YotiSDKException.class);
        exception.expectMessage("No scenario available for use case id : a_scenario");

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("b_scenario")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("123")
                .setBackendCallbackAction("123")
                .create();

        YotiSDK.addScenario(scenario);
        YotiSDK.startScenario(mMockContext, "a_scenario", true);
    }

    @Test
    public void testStartScenario_withNotValidScenario() throws Throwable {
        exception.expect(YotiSDKException.class);
        exception.expectMessage("The scenario b_scenario is not valid");

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("b_scenario")
                .create();

        YotiSDK.addScenario(scenario);
        YotiSDK.startScenario(mMockContext, "b_scenario", true);
    }

    @Test
    public void testStartScenario_withWrongYotiAppVersion() throws Throwable {
        mMockYotiAppPackageInfo.versionCode = -1;

        exception.expect(YotiSDKMinVersionException.class);
        exception.expectMessage("The current Yoti app installed is not compatible with the SDK");

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("a_scenario")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("123")
                .setBackendCallbackAction("123")
                .create();


        YotiSDK.addScenario(scenario);
        YotiSDK.startScenario(mMockContext, "a_scenario", true);
    }

    @Test
    public void testStartScenario_withNoYotiApp() throws Throwable {

        Mockito.when(mMockPackageManager.getPackageInfo(YotiAppDefs.YOTI_APP_PACKAGE, 0)).thenThrow(PackageManager.NameNotFoundException.class);

        mMockYotiAppPackageInfo.versionCode = -1;

        exception.expect(YotiSDKNoYotiAppException.class);

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("a_scenario")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("123")
                .setBackendCallbackAction("123")
                .create();


        YotiSDK.addScenario(scenario);
        YotiSDK.startScenario(mMockContext, "a_scenario", false);
    }

    @Test
    public void testStartScenario_whenHandlingNoYotiApp() throws Throwable {

        Mockito.when(mMockPackageManager.getPackageInfo(YotiAppDefs.YOTI_APP_PACKAGE, 0)).thenThrow(PackageManager.NameNotFoundException.class);

        mMockYotiAppPackageInfo.versionCode = -1;

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("a_scenario")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("123")
                .setBackendCallbackAction("123")
                .create();


        YotiSDK.addScenario(scenario);
        YotiSDK.startScenario(mMockContext, "a_scenario", true);
    }

    @Test
    public void testStartScenario_withoutHandlingNoYotiApp() throws Throwable {

        Mockito.when(mMockPackageManager.getPackageInfo(YotiAppDefs.YOTI_APP_PACKAGE, 0)).thenThrow(PackageManager.NameNotFoundException.class);

        mMockYotiAppPackageInfo.versionCode = -1;

        exception.expect(YotiSDKNoYotiAppException.class);

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("a_scenario")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("123")
                .setBackendCallbackAction("123")
                .create();


        YotiSDK.addScenario(scenario);
        YotiSDK.startScenario(mMockContext, "a_scenario", false);
    }
}
