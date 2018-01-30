package com.yoti.mobile.android.sampleapp2.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yoti.mobile.android.sampleapp2.ProfileActivity;
import com.yoti.mobile.android.sampleapp2.model.Profile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static com.yoti.mobile.android.sampleapp2.ProfileActivity.ADDRESS_EXTRA;
import static com.yoti.mobile.android.sampleapp2.ProfileActivity.DOB_EXTRA;
import static com.yoti.mobile.android.sampleapp2.ProfileActivity.EMAIL_EXTRA;
import static com.yoti.mobile.android.sampleapp2.ProfileActivity.GENDER_EXTRA;
import static com.yoti.mobile.android.sampleapp2.ProfileActivity.IMAGE_EXTRA;
import static com.yoti.mobile.android.sampleapp2.ProfileActivity.MOBILE_EXTRA;
import static com.yoti.mobile.android.sampleapp2.ProfileActivity.NAME_EXTRA;

/**
 * An {@link IntentService} subclass for handling the call to the backend.
 * By sending the encrypted token, we will receive the profile with the user's data.
 */
public class CallbackIntentService extends IntentService {

    private static final String TAG = CallbackIntentService.class.getSimpleName();

    private static final String ACTION_RETRIEVE_PROFILE = "com.yoti.services.handleActionRetrieveProfile";
    private static final String CALLBACK_ROOT_EXTRA = "com.yoti.services.CALLBACK_ROOT_EXTRA";
    private static final String TOKEN_EXTRA = "com.yoti.services.TOKEN_EXTRA";
    private static final String FULL_URL_EXTRA = "com.yoti.services.FULL_URL_EXTRA";

    public CallbackIntentService() {
        super("CallbackIntentService");
    }

    /**
     * Starts this service to perform action Retrieve Profile with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRetrieveProfile(Context context, String useCaseId, String callbackRoot, String token, String fullUrl) {
        Intent intent = new Intent(context, CallbackIntentService.class);
        intent.setAction(ACTION_RETRIEVE_PROFILE);
        intent.putExtra(CALLBACK_ROOT_EXTRA, callbackRoot);
        intent.putExtra(TOKEN_EXTRA, token);
        intent.putExtra(FULL_URL_EXTRA, fullUrl);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RETRIEVE_PROFILE.equals(action)) {
                final String callbackRoot = intent.getStringExtra(CALLBACK_ROOT_EXTRA);
                final String token = intent.getStringExtra(TOKEN_EXTRA);
                final String fullUrl = intent.getStringExtra(FULL_URL_EXTRA);
                handleActionRetrieveProfile(callbackRoot, token, fullUrl);
            }
        }
    }

    /**
     * Handle action Retrieve profile in the provided background thread with the provided
     * parameters - accepting the Yoti callbackURL, token and fullURL (a combination of token and callback)
     */
    private void handleActionRetrieveProfile(String callbackUrl, String token, String fullUrl) {

        byte[] response = new byte[0];
        try {
            // This will be the Url to your backend.
            // If you are already using the callback Url specified in the settings of your
            // application (in dashboard) for your web SDK for example, you will have to define a
            // new endpoint in your backend that deals with json responses.
            URL url = new URL(fullUrl.replace("profile", "profile-json"));
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            response = getBytes(urlConnection);
        } catch (IOException e) {
            Log.e(TAG, "Error when calling our backend manually!", e);
        }

        Gson g = new GsonBuilder().create();
        Profile profile = g.fromJson(new String(response), Profile.class);

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(NAME_EXTRA, profile.getGivenNames() + " " + profile.getFamilyName());
        intent.putExtra(EMAIL_EXTRA, profile.getEmailAddress());
        intent.putExtra(IMAGE_EXTRA, profile.getSelfie());
        intent.putExtra(DOB_EXTRA, profile.getDateOfBirth());
        intent.putExtra(ADDRESS_EXTRA, profile.getPostalAddress());
        intent.putExtra(MOBILE_EXTRA, profile.getMobNum());
        intent.putExtra(GENDER_EXTRA, profile.getGender());
        startActivity(intent);

    }

    private byte[] getBytes(HttpsURLConnection urlConnection) throws IOException {
        byte[] response = null;

        try {
            InputStream is = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                is = urlConnection.getInputStream();

                byte[] byteChunk = new byte[4096];
                int n;

                while ((n = is.read(byteChunk)) > 0) {
                    baos.write(byteChunk, 0, n);
                }

                response = baos.toByteArray();
                is.close();

            } catch (IOException e) {
                if (is != null) {
                    is.close();
                }
            }

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;
    }
}
