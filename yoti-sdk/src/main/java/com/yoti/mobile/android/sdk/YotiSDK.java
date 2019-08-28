package com.yoti.mobile.android.sdk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.ResultReceiver;
import androidx.annotation.Nullable;

import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKMinVersionException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNoScenarioException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNoYotiAppException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNotValidScenarioException;
import com.yoti.mobile.android.sdk.kernelSDK.KernelSDKIntentService;
import com.yoti.mobile.android.sdk.model.Scenario;

import java.util.HashMap;

/**
 * Singleton to manage the different Scenarios defined by a third party app.
 * <p>
 * This class is initialised when you add your first Scenario.
 * <p>
 * A Scenario will be started when a click event is detected on a YotiSDKButton,
 * you have defined a use case Id for the view in the xml. We are using this use case Id to start
 * the appropriate Scenario.
 * <p>
 * To use the YotiSDK define a Scenario and add it, the rest is handled.
 */

public class YotiSDK {

    private static YotiSDK mInstance;

    private HashMap<String, Scenario> mScenarios;

    private boolean mIsLoggingEnabled = false;

    private YotiSDK() {
        mScenarios = new HashMap<>();
    }

    /**
     * This method allows you to add a Scenario
     * and will also initialise the Singleton if it's the first Scenario you add.
     *
     * @param scenario
     */
    public static void addScenario(Scenario scenario) {

        if (mInstance == null) {
            mInstance = new YotiSDK();
        }

        if (mInstance.mScenarios.containsKey(scenario.getUseCaseId())) {
            mInstance.mScenarios.remove(scenario.getUseCaseId());
        }

        mInstance.mScenarios.put(scenario.getUseCaseId(), scenario);
    }

    /**
     * Return a Scenario for the given use case Id
     *
     * @param useCaseId
     * @return
     */
    @Nullable
    public static Scenario getScenario(String useCaseId) {
        if (mInstance == null) {
            return null;
        }
        return mInstance.mScenarios.get(useCaseId);
    }

    /**
     * This method will start a Scenario for the specified use case Id.
     * <p>
     * When a Scenario is started we check :
     * - if the Yoti app is installed on the device
     * - if the version of the Yoti app is compatible
     * - if the use case id is related to a Scenario
     * - if the Scenario is valid
     * <p>
     * If we satisfy all the check, we start the Scenario by :
     * - Calling the Yoti connect API to retrieve the Uri linked to the Scenario you defined in the Yoti Dashboard
     * - Sending the Uri to the Yoti app so the user can do the sharing attribute flow
     *
     * @param context
     * @param useCaseId
     * @param handleNoYotiAppError true if we want the SDK to handle the error and open a website to
     *                             invite the user to download Yoti from the Play Store
     * @throws YotiSDKException
     */
    /*package*/
    static void startScenario(final Context context, final String useCaseId,
                              final boolean handleNoYotiAppError,
                              final ResultReceiver onYotiCalledResultReceiver) throws YotiSDKException {

        YotiSDKLogger.debug("Starting scenario " + useCaseId);

        if (mInstance == null) {
            throw new YotiSDKException("SDK not initialised");
        }

        YotiSDKLogger.debug("Checking Yoti app is available");
        try {
            PackageInfo yotiAppInfo = context.getPackageManager().getPackageInfo(YotiAppDefs.YOTI_APP_PACKAGE, 0);
            if (yotiAppInfo.versionCode < YotiAppDefs.MIN_VERSION_YOTI_APP_REQUIRED) {
                throw new YotiSDKMinVersionException("The current Yoti app installed is not compatible with the SDK");
            }
        } catch (PackageManager.NameNotFoundException ex) {
            if (handleNoYotiAppError) {
                // We send the intent so it opens a website that invites the user to download Yoti
                YotiSDKLogger.debug("No Yoti app installed on the device.");
            } else {
                throw new YotiSDKNoYotiAppException("No Yoti app installed on the device");
            }
        }

        YotiSDKLogger.debug("Retrieving scenario " + useCaseId);
        final Scenario currentScenario = getScenario(useCaseId);

        if (currentScenario == null) {
            throw new YotiSDKNoScenarioException("No scenario available for use case id : " + useCaseId);
        }

        if (!currentScenario.isValid()) {
            throw new YotiSDKNotValidScenarioException("The scenario " + useCaseId + " is not valid");
        }

        YotiSDKLogger.debug("Started scenario " + useCaseId);
        KernelSDKIntentService.startActionStartScenario(context, useCaseId, onYotiCalledResultReceiver);
    }

    /**
     * Clear the Singleton
     */
    public static void clear() {
        if (mInstance != null && mInstance.mScenarios != null) {
            mInstance.mScenarios.clear();
            mInstance = null;
        }
    }

    /**
     * Enable or disable logging
     *
     * @param enable
     */
    public static void enableSDKLogging(boolean enable) {
        if (mInstance == null) {
            mInstance = new YotiSDK();
        }

        mInstance.mIsLoggingEnabled = enable;
    }

    /*package*/
    static boolean isSDKLoggingEnabled() {
        return mInstance != null && mInstance.mIsLoggingEnabled;
    }
}
