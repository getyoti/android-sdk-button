package com.yoti.mobile.android.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ReceiverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(getIntent().getAction());
        intent.setPackage(getIntent().getPackage());
        intent.putExtras(getIntent().getExtras());

        sendBroadcast(intent);

        finish();
    }
}
