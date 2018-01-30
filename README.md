# Yoti Android SDK
The mobile SDK purpose is to provide 3rd party applications the ability to request attributes from a Yoti user while leveraging the Yoti mobile App. It is an interaction between a 3rd Party app and Yoti app facilitated by a very lightweight SDKs.
This repo contains the tools and step by step instructions so that your users can share their identity details with your application in a secure and trusted way.


## Table of Contents

1) [References](#references) 
2) [Requirements](#requirements)
3) [Installing the SDK](#installing-the-sDK)
4) [Profile Retrieval](#profile-retrieval)
5) [Handling Users](#handling-users)
6) [Running the Example](#running-the-example)
7) [API Coverage](#api-coverage)
8) [Support](#support)
## References

* [Base64 data](https://en.wikipedia.org/wiki/Base64)

## Requirements

* You will need to have the Yoti app on your phone
* You will need to ensure the minimum version of your android is 2.2.3 or above.


## Installing the SDK

There are three sections to complete installing the mobile SDK:

1) First please follow our Yoti dashboard process. You will need to create an organisation [here](https://www.yoti.com/dashboard/login-organisations). After organisation creation you will need to create a Yoti application. If you are testing or using yoti for personal use please go straight to creating an application [here](https://www.yoti.com/dashboard/login).

The application process will generate keys for you. Please keep your sdk id and scenario id safe for the mobile integration.
For more information please follow our developer page instructions located [here](https://www.yoti.com/developers).

2) Installing the web SDK. Please browse through our github page and initialise the web sdk in your web backend.

For more information please follow our developer page instructions located [here](https://www.yoti.com/developers).

3) Installing the Mobile SDK

Please start by adding dependencies in the "build.grade" file inside your application module directory:

```javascript
dependencies {
    compile(group: 'com.yoti.mobile.android.sdk', name: 'yoti-button-sdk', version: '0.0.5', classifier: 'release', ext: 'aar'){
            transitive = true
        }
```

After syncing, go to your layout file where you wish the Yoti button to appear add the below config:

```xml
xmlns:yoti="http://schemas.android.com/apk/res-auto"
<com.yoti.mobile.android.sdk.YotiSDKButton
android:id="@+id/my_id"
android:layout_height="wrap_content"
android:layout_width="wrap_content"
android:text="My Yoti Button"
yoti:useCaseId="YOUR_USE_CASE_ID"/>
```


The client end of the integration is now complete.

## Configuration

Add the below configuration to your manifest:

```xml
<receiver android:name=".MyBroadcastReceiver">
    <intent-filter>
        <action android:name="YOUR_CALLBACK_ACTION"/>
        <action android:name="YOUR_BACKEND_CALLBACK_ACTION"/>
    </intent-filter>
</receiver>
```

Adding this broadcast receiver class, this acts as a listener for Yoti to get the callback URL from the Yoti app. Please note there are two call back options:

1) You handle the callback to your backend
2) Let the SDK manage this for you

If you choose to let the SDK handle the callback URL, it will start an IntentService, do the network call and callback the BroadcastReceiver using the MY_BACKEND_CALLBACK_ACTION action.
To be able to listen to those actions you will need to:

* Create a BroadcastReceiver that extends AbstractShareAttributesBroadcastReceiver

* Define the callback action for the Yoti app and if necessary the callback action for the IntentService.

The AbstractYotiBroadcastReceiver will handle the logic regarding those actions, that's why your BroadcastReceiver will have to extend it. The first action defined (MY_CALLBACK_ACTION) will be used by the Yoti app to give you the result of the attributes sharing with your app.
We have included both config below :

```javascript
 @Override
    public boolean onCallbackReceived(String useCaseId, String callbackRoot, String token, String fullUrl) {
        // triggered by MY_CALLBACK_ACTION
        // If you decide to call the callback url via your own Service and handle the result, set isProcessed at true.
        // Otherwise, set isProcessed at false and the Yoti SDK will make the call
        return isProcessed;
    }

    @Override
    public void onShareFailed(String useCaseId) {
        // Share process not completed on the Yoti App by the user
    }

    @Override
    public void onCallbackSuccess(String useCaseId, byte[] response) {
       // triggered by MY_BACKEND_CALLBACK_ACTION depending on the result
    }

    @Override
    public void onCallbackError(String useCaseId, int httpErrorCode, Throwable error, byte[] response) {
       // triggered by MY_BACKEND_CALLBACK_ACTION depending on the result
    }
}
```

You will now need to specify your Client SDK ID and Scenario ID ready from your application dashboard.
The SDK can be initialised like this:


```javascript
Scenario scenario = null;
try {
    scenario = new Scenario.Builder()
    .setUseCaseId("YOUR_USE_CASE_ID") //this is determined in your xml layout file
    .setClientSDKId("YOUR_SDK_ID") //please get this from yoti dashboard
    .setScenarioId("YOUR_SCENARIO_ID") //please get this from yoti dashboard
    .setCallbackAction("MY_CALLBACK_ACTION") //from your broadcast receiver config allowing the Yoti app to send you back the encrypted token.
    .setBackendCallbackAction(MY_BACKEND_CALLBACK_ACTION) // Optional if you choose to make the call to your backend yourself
    .create();
} catch (YotiSDKNotValidScenarioException e) {
    Log.e(TAG, "Invalid scenario!!", e);
}

YotiSDK.addScenario(scenario);
```


You can activate a verbose mode for the SDK by using this method :
```javascript
YotiSDK.enableSDKLogging(true);
```

If the Yoti app is not installed in the user's phone by default the SDK will send and intent to
open a website that invites the user to download the Yoti app.
Alternatively, an error listener can be set up so you can deal with this situation by yourself.

```javascript
        yotiSDKButton.setOnYotiAppNotInstalledListener(new YotiSDKButton.OnYotiAppNotInstalledListener() {
            @Override
            public void onYotiAppNotInstalledError(YotiSDKNoYotiAppException cause) {
                // Do whatever you want here
            }
        });
```

## Profile Retrieval

Once the profile has been retrieved, you can present that data in the app.

To get the profile details you could do something like this:

```javascript
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
```

And then display that data as you would normally do:

```javascript
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
         TextView nationalityTextView = findViewById(R.id.text_view_nationality);

         String imageString = getIntent().getStringExtra("image");
         byte[] decodedString = Base64.decode(imageString.substring(imageString.indexOf(",") + 1), Base64.DEFAULT);
         Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
         profile.setImageBitmap(decodedByte);
         
         nameTextView.setText(getIntent().getStringExtra("name"));
         emailTextView.setText(getIntent().getStringExtra("email"));
         addressTextView.setText(getIntent().getStringExtra("address"));
         mobileTextView.setText(getIntent().getStringExtra("mobile"));
         genderTextView.setText(getIntent().getStringExtra("gender"));
         dobTextView.setText(getIntent().getStringExtra("dob"));
         nationalityTextView.setText(getIntent().getStringExtra("nationality"));
     }
}
```

## Handling Users

The Web SDK will handle the user storage. When you retrieve the user profile, you receive a user ID generated by Yoti exclusively for your application. This means that if the same individual logs into another app, Yoti will assign her/him a different ID. You can use this ID to verify whether (for your application) the retrieved profile identifies a new or an existing user. Please see relevant github pages for more information.


## Running the Example

In this repository we provide two sample apps.

* Sample-app

This is an example of an application that let the SDK make the call to the backend automatically.

Please feel free to download it and use in your Android Studio.
You will need to replace the keys to view the demo successfully.

* Sample-app-2

This is an example of an application that handle the call to the backend manually.

Please note that this example won't work if you try to run it as the backend is not live.


## API Coverage

In order to get the user's information, the backend SDK will decrypt the token in the callback URL and will retrieve you the following data:

```
* Activity Details

    User Id .getUserId()
    Profile .getUserProfile()
        Photo
        Given Names
        Family Name
        Mobile Number
        Email address
        Date of Birth
        Address
        Gender
```

## Support
For any questions or support please email [sdksupport@yoti.com](mailto:sdksupport@yoti.com). Please provide the following to get you up and working as quickly as possible:

* Software version on the phone
* Language of backend SDK
* Screenshot of error

Once we have answered your question we may contact you again to discuss Yoti products and services. If you’d prefer us not to do this, please let us know when you e-mail.
