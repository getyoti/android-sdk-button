package com.yoti.mobile.android.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.ResultReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yoti.mobile.android.sdk.exceptions.AppNotInstalledErrorCode;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKAppNotInstalledException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKMinVersionException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNoScenarioException;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKNotValidScenarioException;
import com.yoti.mobile.android.sdk.kernelSDK.KernelSDKIntentService;
import com.yoti.mobile.android.sdk.model.Scenario;

import java.util.HashMap;

import static com.yoti.mobile.android.sdk.ButtonTheme.THEME_YOTI;
import static com.yoti.mobile.android.sdk.exceptions.AppNotInstalledErrorCode.EASY_ID_APP_NOT_INSTALLED;
import static com.yoti.mobile.android.sdk.exceptions.AppNotInstalledErrorCode.PARTNERSHIP_APP_NOT_INSTALLED;
import static com.yoti.mobile.android.sdk.exceptions.AppNotInstalledErrorCode.YOTI_APP_NOT_INSTALLED;

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
     * @param handleNoAppInstalledError true if we want the SDK to handle the error and open a website to
     *                             invite the user to download Yoti from the Play Store
     * @throws YotiSDKException When the SDK is not able to start the scenario
     */
    /*package*/
    static void startScenario(final Context context, final String useCaseId,
                              final boolean handleNoAppInstalledError,
                              final ResultReceiver onYotiCalledResultReceiver,
                              final ButtonTheme sdkButtonTheme) throws YotiSDKException {

        YotiSDKLogger.debug("Starting scenario " + useCaseId);

        if (mInstance == null) {
            throw new YotiSDKException("SDK not initialised");
        }

        YotiSDKLogger.debug("Checking Yoti app is available");
        PackageInfo appPackageInfo = null;
        try {
            appPackageInfo = getAppPackageInfoBasedOnTheme(context.getPackageManager(), sdkButtonTheme);
            if (appPackageInfo.versionCode < YotiAppDefs.MIN_VERSION_YOTI_APP_REQUIRED) {
                throw new YotiSDKMinVersionException("The current Yoti app installed is not compatible with the SDK");
            }
        }
        catch (YotiSDKAppNotInstalledException cause) {
            if (handleNoAppInstalledError) {
                // We send the intent so it opens a website that invites the user to download Yoti
                YotiSDKLogger.debug("App not installed on the device.");
            }
            throw cause;
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
        KernelSDKIntentService.startActionStartScenario(context, useCaseId, onYotiCalledResultReceiver, getLaunchingAppScheme(appPackageInfo));
    }

    private static PackageInfo getAppPackageInfoBasedOnTheme(PackageManager packageManager, ButtonTheme buttonTheme) throws YotiSDKAppNotInstalledException {
        PackageInfo packageInfo = null;
        try {
            switch (buttonTheme) {
                case THEME_YOTI:
                case THEME_PARTNERSHIP:
                    if (checkAppInstalled(packageManager, YotiAppDefs.YOTI_APP_PACKAGE)) {
                        packageInfo = packageManager.getPackageInfo(YotiAppDefs.YOTI_APP_PACKAGE, 0);
                    } else if (checkAppInstalled(packageManager, YotiAppDefs.EASY_ID_APP_PACKAGE)) {
                        packageInfo = packageManager.getPackageInfo(YotiAppDefs.EASY_ID_APP_PACKAGE, 0);
                    } else {
                        AppNotInstalledErrorCode errorCode = buttonTheme == THEME_YOTI ? YOTI_APP_NOT_INSTALLED : PARTNERSHIP_APP_NOT_INSTALLED;
                        throw new YotiSDKAppNotInstalledException(errorCode, "Yoti app not installed");
                    }
                    break;
                case THEME_EASYID:
                    if (checkAppInstalled(packageManager, YotiAppDefs.EASY_ID_APP_PACKAGE)
                            && checkEasyAppWithSchemeAvailable(packageManager)) {
                        packageInfo = packageManager.getPackageInfo(YotiAppDefs.EASY_ID_APP_PACKAGE, 0);
                    } else {
                        throw new YotiSDKAppNotInstalledException(EASY_ID_APP_NOT_INSTALLED, "EasyId app not installed");
                    }
                    break;
            }
        }
        catch (NameNotFoundException e) {
            throw new YotiSDKAppNotInstalledException(YOTI_APP_NOT_INSTALLED, "Yoti app not installed");
        }
        return packageInfo;
    }

    private static boolean checkAppInstalled(PackageManager packageManager, String packageName) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    private static boolean checkEasyAppWithSchemeAvailable(@NonNull PackageManager packageManager) {
        Uri uri = Uri.parse(BuildConfig.EASY_ID_APP_SCHEME.concat(BuildConfig.EASY_ID_HOST_URL));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        return intent.resolveActivity(packageManager) != null;
    }

    private static String getLaunchingAppScheme(PackageInfo packageInfo) {
        if(packageInfo != null && packageInfo.packageName.equals(YotiAppDefs.EASY_ID_APP_PACKAGE)) {
            return YotiAppDefs.EASY_ID_APP_SCHEME;
        }
        else {
            return YotiAppDefs.YOTI_APP_SCHEME;
        }
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
