package com.yoti.mobile.android.sdk.kernelSDK;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.yoti.mobile.android.sdk.ReceiverActivity;
import com.yoti.mobile.android.sdk.YotiSDK;
import com.yoti.mobile.android.sdk.YotiSDKDefs;
import com.yoti.mobile.android.sdk.YotiSDKLogger;
import com.yoti.mobile.android.sdk.model.Scenario;

import java.io.IOException;

import static com.yoti.mobile.android.sdk.YotiAppDefs.YOTI_APP_PACKAGE;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.APP_ID_PARAM;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.APP_NAME_PARAM;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.CALLBACK_PARAM;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.QR_CODE_URL_HTTPS_SCHEME;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.INTENT_EXTRA_ERROR;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.INTENT_EXTRA_HTTP_ERROR_CODE;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.INTENT_EXTRA_RESPONSE;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.INTENT_EXTRA_RESULT;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.INTENT_EXTRA_USE_CASE_ID;
import static com.yoti.mobile.android.sdk.YotiSDKDefs.USE_CASE_ID_PARAM;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class KernelSDKIntentService extends IntentService {

    private static final String ACTION_BACKEND_CALL = "com.yoti.mobile.android.sdk.network.action.BACKEND_CALL";
    private static final String ACTION_START_SCENARIO = "com.yoti.mobile.android.sdk.network.action.START_SCENARIO";
    private static final String YOTI_CALLED_RESULT_RECEIVER = "com.yoti.mobile.android.sdk.network.action.YOTI_CALLED_RESULT_RECEIVER";
    public static final String YOTI_PENDING_INTENT_EXTRA = "com.yoti.mobile.android.sdk.network.extra.YOTI_PENDING_INTENT_EXTRA";

    private static final String EXTRA_USE_CASE_ID = "com.yoti.mobile.android.sdk.network.extra.USE_CASE_ID";
    private static final String EXTRA_APP_SCHEME = "com.yoti.mobile.android.sdk.network.extra.EXTRA_APP_SCHEME";
    private KernelSDK mKernelSDK;

    public KernelSDKIntentService() {
        super("KernelSDKIntentService");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mKernelSDK = new KernelSDK();
    }

    /**
     * Starts this service to perform a call to a third party backend
     *
     * @see IntentService
     */
    public static void startActionBackendCall(Context context, String useCaseId) {
        Intent intent = new Intent(context, KernelSDKIntentService.class);
        intent.setAction(ACTION_BACKEND_CALL);
        intent.putExtra(EXTRA_USE_CASE_ID, useCaseId);
        context.startService(intent);
    }

    /**
     * Starts this service to perform a call to the Yoti API to retrieve the scenario and start the Yoti App
     *
     * @see IntentService
     */
    public static void startActionStartScenario(Context context, String useCaseId, ResultReceiver resultReceiver, String appScheme) {
        Intent intent = new Intent(context, KernelSDKIntentService.class);
        intent.setAction(ACTION_START_SCENARIO);
        intent.putExtra(EXTRA_USE_CASE_ID, useCaseId);
        intent.putExtra(YOTI_CALLED_RESULT_RECEIVER, resultReceiver);
        intent.putExtra(EXTRA_APP_SCHEME, appScheme);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String useCaseId = intent.getStringExtra(EXTRA_USE_CASE_ID);
            final String appScheme = intent.getStringExtra(EXTRA_APP_SCHEME);
            if (ACTION_BACKEND_CALL.equals(action)) {
                handleActionBackendCall(useCaseId);
            } else if (ACTION_START_SCENARIO.equals(action)) {
                ResultReceiver resultReceiver = intent.getParcelableExtra(YOTI_CALLED_RESULT_RECEIVER);
                handleActionStartScenario(useCaseId, resultReceiver, appScheme);
            }
        }
    }

    private void handleActionStartScenario(String useCaseId, ResultReceiver resultReceiver, String appScheme) {

        Scenario currentScenario = YotiSDK.getScenario(useCaseId);

        if (currentScenario == null) {
            return;
        }

        String qrCodeUrl = null;
        try {
            qrCodeUrl = mKernelSDK.retrieveScenarioUri(currentScenario);
        } catch (IOException e) {
            YotiSDKLogger.error("Error while reaching Connect API", e);
        }

        if (TextUtils.isEmpty(qrCodeUrl)) {
            YotiSDKLogger.error("Error while retrieving the scenario from the Yoti API, please check your Internet connection and make sure your clientSDKId and scenarioId are correct.");
            return;
        }

        currentScenario.setQrCodeUrl(qrCodeUrl);

        // Retrieve the current app name
        ApplicationInfo applicationInfo = getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        String appName = stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : getString(stringId);

        String currentScenarioQrCodeUrl = currentScenario.getQrCodeUrl();
        Intent intent = null;
        if (currentScenarioQrCodeUrl.startsWith(YotiSDKDefs.QR_CODE_URL_HTTPS_SCHEME) && appScheme != null) {
            currentScenarioQrCodeUrl = currentScenarioQrCodeUrl.replaceFirst(QR_CODE_URL_HTTPS_SCHEME, appScheme);
        }
        else {
            intent = getPackageManager().getLaunchIntentForPackage(YOTI_APP_PACKAGE);
        }

        // Here we transform the QrCode url to a Uri to add extra parameter so the Yoti App can callback the Yoti SDK.
        Uri uri = Uri.parse(currentScenarioQrCodeUrl);
        uri = uri.buildUpon()
                .appendQueryParameter(CALLBACK_PARAM, currentScenario.getCallbackAction())
                .appendQueryParameter(USE_CASE_ID_PARAM, String.valueOf(useCaseId))
                .appendQueryParameter(APP_ID_PARAM, getPackageName())
                .appendQueryParameter(APP_NAME_PARAM, appName)
                .build();

        resultReceiver.send(0, null);

        if (intent == null) {
            // Yoti app is not installed, let's open the website
            intent = new Intent(Intent.ACTION_VIEW, uri);
        } else {
            intent.setData(uri);
        }

        int flags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        Intent wakeupIntent = new Intent(this, ReceiverActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, wakeupIntent, flags);


        intent.putExtra(YOTI_PENDING_INTENT_EXTRA, pendingIntent);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Call a third party backend after a successful share attributes via the Yoti App to get the value of the shared attributes.
     *
     * @param useCaseId id defined by the third party to handle different scenario.
     */
    private void handleActionBackendCall(String useCaseId) {
        final Scenario currentScenario = YotiSDK.getScenario(useCaseId);

        if (currentScenario == null) {
            return;
        }

        mKernelSDK.processShareResult(currentScenario, new ICallbackBackendListener() {

            @Override
            public void onSuccess(byte[] response) {
                Intent intent = new Intent(currentScenario.getCallbackBackendAction());
                intent.putExtra(INTENT_EXTRA_USE_CASE_ID, currentScenario.getUseCaseId());
                intent.putExtra(INTENT_EXTRA_RESULT, true);
                intent.putExtra(INTENT_EXTRA_RESPONSE, response);

                sendBroadcast(intent);
            }

            @Override
            public void onError(int httpErrorCode, Throwable error, byte[] response) {
                Intent intent = new Intent(currentScenario.getCallbackBackendAction());
                intent.putExtra(INTENT_EXTRA_USE_CASE_ID, currentScenario.getUseCaseId());
                intent.putExtra(INTENT_EXTRA_RESULT, false);
                intent.putExtra(INTENT_EXTRA_RESPONSE, response);
                intent.putExtra(INTENT_EXTRA_HTTP_ERROR_CODE, httpErrorCode);
                intent.putExtra(INTENT_EXTRA_ERROR, error);

                sendBroadcast(intent);
            }
        });
    }
}
