package com.yoti.mobile.android.sampleapp2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.yoti.sampleapp2.R;

public class ProfileActivity extends AppCompatActivity {

    public static final String NAME_EXTRA = "com.yoti.services.NAME_EXTRA";
    public static final String EMAIL_EXTRA = "com.yoti.services.EMAIL_EXTRA";
    public static final String IMAGE_EXTRA = "com.yoti.services.IMAGE_EXTRA";
    public static final String DOB_EXTRA = "com.yoti.services.DOB_EXTRA";
    public static final String ADDRESS_EXTRA = "com.yoti.services.ADDRESS_EXTRA";
    public static final String MOBILE_EXTRA = "com.yoti.services.MOBILE_EXTRA";
    public static final String GENDER_EXTRA = "com.yoti.services.GENDER_EXTRA";
    public static final String PROFILE_EXTRA = "com.yoti.services.PROFILE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView profile = findViewById(R.id.img_profile);
        TextView nameTextView = findViewById(R.id.text_view_name);
        TextView emailTextView = findViewById(R.id.text_view_email);
        TextView addressTextView = findViewById(R.id.text_view_address);
        TextView mobileTextView = findViewById(R.id.text_view_mobile);
        TextView genderTextView = findViewById(R.id.text_view_gender);
        TextView dobTextView = findViewById(R.id.text_view_dob);

        String imageString = getIntent().getStringExtra(IMAGE_EXTRA);
        byte[] decodedString = Base64.decode(imageString.substring(imageString.indexOf(",") + 1), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        profile.setImageBitmap(decodedByte);
        nameTextView.setText(getIntent().getStringExtra(NAME_EXTRA));
        emailTextView.setText(getIntent().getStringExtra(EMAIL_EXTRA));
        addressTextView.setText(getIntent().getStringExtra(ADDRESS_EXTRA));
        mobileTextView.setText(getIntent().getStringExtra(MOBILE_EXTRA));
        genderTextView.setText(getIntent().getStringExtra(GENDER_EXTRA));
        dobTextView.setText(getIntent().getStringExtra(DOB_EXTRA));
    }
}
