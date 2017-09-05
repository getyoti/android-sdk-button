package com.yoti.mobile.android.sdk;

import android.test.suitebuilder.annotation.SmallTest;

import com.yoti.mobile.android.sdk.exceptions.YotiSDKNotValidScenarioException;
import com.yoti.mobile.android.sdk.model.Scenario;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class ShareAttributeManagerTest {

    @Mock
    ShareAttributesContract.IShareAttributeBroadcastReceiver mMockIShareAttributeBroadcastReceiver;

    ShareAttributesContract.IShareAttributeManager mIShareAttributeManager;

    @Before
    public void setup() {
        mIShareAttributeManager = new ShareAttributeManager(mMockIShareAttributeBroadcastReceiver);
    }

    @Test
    public void testNullUseCaseId_thenNoInteraction() throws YotiSDKNotValidScenarioException {

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("scenario_a")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("action_123")
                .create();

        YotiSDK.addScenario(scenario);

        mIShareAttributeManager.onReceive(null, null, null, true, null, -1, null);

        verify(mMockIShareAttributeBroadcastReceiver, never()).receivedCallback(anyString(), anyString(), anyString(), anyString());
        verify(mMockIShareAttributeBroadcastReceiver, never()).errorCallback(anyString(), anyInt(), any(Throwable.class), any(byte[].class));
        verify(mMockIShareAttributeBroadcastReceiver, never()).successCallback(anyString(), any(byte[].class));
        verify(mMockIShareAttributeBroadcastReceiver, never()).startBackendCall(anyString());
        verify(mMockIShareAttributeBroadcastReceiver, never()).extractCallbackRoot(anyString());
        verify(mMockIShareAttributeBroadcastReceiver, never()).extractToken(anyString());
    }

    @Test
    public void testUnknownUseCaseId_thenNoInteraction() throws YotiSDKNotValidScenarioException {

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("scenario_a")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("action_123")
                .create();

        YotiSDK.addScenario(scenario);

        mIShareAttributeManager.onReceive("scenario_b", null, null, false, null, -1, null);

        verify(mMockIShareAttributeBroadcastReceiver, never()).receivedCallback(anyString(), anyString(), anyString(), anyString());
        verify(mMockIShareAttributeBroadcastReceiver, never()).errorCallback(anyString(), anyInt(), any(Throwable.class), any(byte[].class));
        verify(mMockIShareAttributeBroadcastReceiver, never()).successCallback(anyString(), any(byte[].class));
        verify(mMockIShareAttributeBroadcastReceiver, never()).startBackendCall(anyString());
        verify(mMockIShareAttributeBroadcastReceiver, never()).extractCallbackRoot(anyString());
        verify(mMockIShareAttributeBroadcastReceiver, never()).extractToken(anyString());
    }

    @Test
    public void testActionBackendCallbackSuccess_thenCallViewSuccessCallback() throws YotiSDKNotValidScenarioException {

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("scenario_a")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("action_123")
                .setBackendCallbackAction("action_backend_123")
                .create();

        YotiSDK.addScenario(scenario);

        mIShareAttributeManager.onReceive("scenario_a", "action_backend_123", null, true, new byte[]{}, -1, null);

        verify(mMockIShareAttributeBroadcastReceiver, never()).receivedCallback(anyString(), anyString(), anyString(), anyString());
        verify(mMockIShareAttributeBroadcastReceiver, never()).errorCallback(anyString(), anyInt(), any(Throwable.class), any(byte[].class));
        verify(mMockIShareAttributeBroadcastReceiver, times(1)).successCallback(anyString(), any(byte[].class));
        verify(mMockIShareAttributeBroadcastReceiver, never()).startBackendCall(anyString());
        verify(mMockIShareAttributeBroadcastReceiver, never()).extractCallbackRoot(anyString());
        verify(mMockIShareAttributeBroadcastReceiver, never()).extractToken(anyString());
    }

    @Test
    public void testActionBackendCallbackFailed_thenCallViewErrorCallback() throws YotiSDKNotValidScenarioException {

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("scenario_a")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("action_123")
                .setBackendCallbackAction("action_backend_123")
                .create();

        YotiSDK.addScenario(scenario);

        mIShareAttributeManager.onReceive("scenario_a", "action_backend_123", null, false, new byte[]{}, 500, null);

        verify(mMockIShareAttributeBroadcastReceiver, never()).receivedCallback(anyString(), anyString(), anyString(), anyString());
        verify(mMockIShareAttributeBroadcastReceiver, times(1)).errorCallback(anyString(), anyInt(), any(Throwable.class), any(byte[].class));
        verify(mMockIShareAttributeBroadcastReceiver, never()).successCallback(anyString(), any(byte[].class));
        verify(mMockIShareAttributeBroadcastReceiver, never()).startBackendCall(anyString());
        verify(mMockIShareAttributeBroadcastReceiver, never()).extractCallbackRoot(anyString());
        verify(mMockIShareAttributeBroadcastReceiver, never()).extractToken(anyString());
    }

    @Test
    public void testActionYotiAppCallbackProcessedByApp_thenNotStartSDKService() throws YotiSDKNotValidScenarioException {

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("scenario_a")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("action_123")
                .setBackendCallbackAction("action_backend_123")
                .create();

        YotiSDK.addScenario(scenario);

        when(mMockIShareAttributeBroadcastReceiver.receivedCallback(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        mIShareAttributeManager.onReceive("scenario_a", "action_123", null, false, null, -1, null);

        verify(mMockIShareAttributeBroadcastReceiver, never()).startBackendCall(anyString());
    }

    @Test
    public void testActionYotiAppCallbackProcessedByAppButNullBackendCallbackAction_thenNotStartSDKService() throws YotiSDKNotValidScenarioException {

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("scenario_a")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("action_123")
                .create();

        YotiSDK.addScenario(scenario);

        when(mMockIShareAttributeBroadcastReceiver.receivedCallback(anyString(), anyString(), anyString(), anyString())).thenReturn(false);

        mIShareAttributeManager.onReceive("scenario_a", "action_123", null, false, null, -1, null);

        verify(mMockIShareAttributeBroadcastReceiver, never()).startBackendCall(anyString());
    }

    @Test
    public void testActionYotiAppCallbackProcessedByAppButEmptyBackendCallbackAction_thenNotStartSDKService() throws YotiSDKNotValidScenarioException {

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("scenario_a")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("action_123")
                .setBackendCallbackAction("")
                .create();

        YotiSDK.addScenario(scenario);

        when(mMockIShareAttributeBroadcastReceiver.receivedCallback(anyString(), anyString(), anyString(), anyString())).thenReturn(false);

        mIShareAttributeManager.onReceive("scenario_a", "action_123", null, false, null, -1, null);

        verify(mMockIShareAttributeBroadcastReceiver, never()).startBackendCall(anyString());
    }

    @Test
    public void testActionYotiAppCallbackWithoutToken_thenThirdPartyAppNotified() throws YotiSDKNotValidScenarioException {

        Scenario scenario = new Scenario.Builder()
                .setUseCaseId("scenario_a")
                .setClientSDKId("123")
                .setScenarioId("123")
                .setCallbackAction("action_123")
                .setBackendCallbackAction("")
                .create();

        YotiSDK.addScenario(scenario);

        when(mMockIShareAttributeBroadcastReceiver.receivedCallback(anyString(), anyString(), anyString(), anyString())).thenReturn(false);

        when(mMockIShareAttributeBroadcastReceiver.extractCallbackRoot(anyString())).thenReturn("www.yoti.com");
        when(mMockIShareAttributeBroadcastReceiver.extractToken(anyString())).thenReturn(null);

        mIShareAttributeManager.onReceive("scenario_a", "action_123", "https://www.yoti.com", false, null, -1, null);

        verify(mMockIShareAttributeBroadcastReceiver).shareFailed(anyString());
    }
}
