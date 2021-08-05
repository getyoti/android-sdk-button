package com.yoti.mobile.android.sampleapp2;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yoti.mobile.android.sdk.YotiSDKButton;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;
import com.yoti.sampleapp2.R;

import static com.yoti.mobile.android.sampleapp2.ProfileActivity.BACKEND_DATA_ERROR_EXTRA;
import static com.yoti.mobile.android.sampleapp2.ProfileActivity.PROFILE_EXTRA;

public class MainActivity extends AppCompatActivity {

    public static final String LOADING_STATUS = "com.yoti.mobile.android.sampleapp2.LOADING_STATUS";

    private YotiSDKButton yotiSDKButton;
    private ProgressBar progress;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yotiSDKButton = findViewById(R.id.yoti_button);
        progress = findViewById(R.id.progress);
        message = findViewById(R.id.text);

        processExtraData(getIntent());

        yotiSDKButton.setOnYotiButtonClickListener(new YotiSDKButton.OnYotiButtonClickListener() {
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        processExtraData(intent);
    }

    private void processExtraData(Intent intent) {

        if (intent.getBooleanExtra(LOADING_STATUS, false)) {
            //Set up UI loading status
            yotiSDKButton.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            message.setText(R.string.loc_loading_message);

        } else if (intent.getBooleanExtra(PROFILE_EXTRA, false)) {
            //Start activity that presents the profile
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            profileIntent.putExtras(intent.getExtras());
            startActivity(profileIntent);

            // Restore UI
            yotiSDKButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
            message.setText("");

        } else if (intent.getBooleanExtra(BACKEND_DATA_ERROR_EXTRA, false)) {
            yotiSDKButton.setVisibility(View.GONE);
            progress.setVisibility(View.GONE);
            message.setText(R.string.loc_error_processing_backend_response);
        }
    }
}
