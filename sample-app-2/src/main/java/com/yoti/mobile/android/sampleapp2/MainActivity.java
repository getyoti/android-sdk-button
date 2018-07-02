package com.yoti.mobile.android.sampleapp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yoti.mobile.android.sdk.YotiSDKButton;
import com.yoti.mobile.android.sdk.exceptions.YotiSDKException;
import com.yoti.sampleapp2.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final YotiSDKButton yotiSDKButton = findViewById(R.id.yoti_button);
        final ProgressBar progress = findViewById(R.id.progress);
        final TextView message = findViewById(R.id.text);

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

}


