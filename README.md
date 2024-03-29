# Yoti Android SDK
[![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/getyoti/android-sdk-button?label=latest%20release)](https://github.com/getyoti/android-sdk-button/releases) [![Publish Release](https://github.com/getyoti/android-sdk-button/workflows/Publish%20Release/badge.svg)](https://github.com/getyoti/android-sdk-button/actions?query=workflow%3A%22Publish+Release%22)

The mobile SDK purpose is to provide 3rd party applications the ability to request attributes from a Yoti user while leveraging the Yoti mobile App. It is an interaction between a 3rd Party app and Yoti app facilitated by a very lightweight SDKs.
This repo contains the tools and step by step instructions so that your users can share their identity details with your application in a secure and trusted way.


## Table of Contents

* [References](#references) 
* [Requirements](#requirements)
* [Installing the SDK](#installing-the-sdk)
* [Configuration](#configuration)
* [Running the Example](#running-the-example)
* [API Coverage](#api-coverage)
* [Support](#support)
## References

* [Base64 data](https://en.wikipedia.org/wiki/Base64)

## Requirements

* You will need to have the Yoti app on your phone
* You will need to ensure the minimum version of your android is 4.0.3 or above.


## Installing the SDK

There are three sections to complete installing the mobile SDK:

1) First please follow our Yoti dashboard process. You will need to create an organisation [here](https://www.yoti.com/dashboard/login-organisations). After organisation creation you will need to create a Yoti application. If you are testing or using yoti for personal use please go straight to creating an application [here](https://www.yoti.com/dashboard/login).

The application process will generate keys for you. Please keep your sdk id and scenario id safe for the mobile integration.
For more information please follow our developer page instructions located [here](https://www.yoti.com/developers).

2) Installing the web SDK. Please browse through our github page and initialise the web sdk in your web backend.

For more information please follow our developer page instructions located [here](https://www.yoti.com/developers).

3) Installing the Mobile SDK

Make sure you have mavenCentral repository added in your allProjects entry in your root build.gradle:

```gradle
allprojects {
    repositories {
        mavenCentral()
        ...
    }
```

Please start by adding dependencies in the "build.grade" file inside your application module directory:

```gradle
dependencies {
    compile(com.yoti.mobile.android.sdk:yoti-button-sdk:$yotiButtonVersion)
}
```
OR if you are using a more recent version of gradle (>= 3.x):
```gradle
dependencies {
    implementation(com.yoti.mobile.android.sdk:yoti-button-sdk:$yotiButtonVersion)
}
```
where `$yotiButtonVersion` references the latest [release](https://github.com/getyoti/android-sdk-button/releases)

[See this code in one of our sample apps](./sample-app/build.gradle)

After syncing, go to your layout file where you wish the Yoti button to appear add the below config:

```xml
xmlns:yoti="http://schemas.android.com/apk/res-auto"
<com.yoti.mobile.android.sdk.YotiSDKButton
android:id="@+id/my_id"
android:layout_height="wrap_content"
android:layout_width="wrap_content"
android:text="My Yoti Button"
yoti:useCaseId="YOUR_USE_CASE_ID"
yoti:buttonTheme="<Theme_Yoti | Theme_EasyId | Theme_Partnership>"/>
```

In case ```yoti:buttonTheme``` is not provided, then ```Theme_Partnership``` is considered as a default theme.
 
When the theme is applied as ```Theme_Yoti``` or ```Theme_Partnership```
* User can share attribute either through Yoti or EasyId app

When the theme is applied as ```Theme_EasyId```
* User can share attribute only through EasyId app

[See this code in one of our sample apps](./sample-app/src/main/res/layout/activity_main.xml)

Alternatively, you can set the button's useCaseId with:
```java
YotiSDKButton.setUseCaseId("YOUR_USE_CASE_ID");
```

The client end of the integration is now complete.

## Configuration

Add the below configuration to your manifest:

```xml
<receiver android:name=".MyBroadcastReceiver" android:exported="false">
    <intent-filter>
        <action android:name="YOUR_CALLBACK_ACTION"/>
        <action android:name="YOUR_BACKEND_CALLBACK_ACTION"/>
    </intent-filter>
</receiver>
```
[See this code in one of our sample apps](./sample-app/src/main/AndroidManifest.xml)

This broadcast receiver was used to receive the communication back from the Yoti App. That's not
the case in recent versions of the SDK and it is only used internally within your app. Therefore we
strongly recommend you to declare your receiver with the option exported="false".

Please note there are two call back options:

1) You handle the callback to your backend
2) Let the SDK manage this for you

If you choose to let the SDK handle the callback URL, it will start an IntentService, do the network call and callback the BroadcastReceiver using the MY_BACKEND_CALLBACK_ACTION action.
To be able to listen to those actions you will need to:

* Create a BroadcastReceiver that extends AbstractShareAttributesBroadcastReceiver

* Define the callback action for the Yoti app and if necessary the callback action for the IntentService.

The AbstractYotiBroadcastReceiver will handle the logic regarding those actions, that's why your BroadcastReceiver will have to extend it. The first action defined (MY_CALLBACK_ACTION) will be used by the Yoti app to give you the result of the attributes sharing with your app.
We have included both config below :

```java
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
[See this code in one of our sample apps](./sample-app/src/main/java/com/yoti/mobile/android/sdk/sampleapp/ShareAttributesResultBroadcastReceiver.java)

Once you have received the intent from the Yoti app you should start one of your activities so that
your app goes back to the foreground and the user can continue with the flow within your app.

[Check our Sample-app2 to see an example of how this can be done.](./sample-app-2/src/main/java/com/yoti/mobile/android/sampleapp2/recievers/ShareAttributesResultBroadcastReceiver.java)


You will now need to specify your Client SDK ID and Scenario ID from your application dashboard.
The SDK can be initialised like this:


```java
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

It is very important that this initialisation is done in the onCreate method fo your Application.
[See this code in one of our sample apps](./sample-app/src/main/java/com/yoti/mobile/android/sdk/sampleapp/SampleApp.java)

In order to set a listener for the events on the Yoti button you can specify one this way:

```java
yotiSDKButton.setOnYotiButtonListener(new YotiSDKButton.OnYotiButtonClickListener() {
    @Override
    public void onStartScenario() {
        yotiSDKButton.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        message.setText(null); 
    }

    @Override
    public void onStartScenarioError(YotiSDKException cause){
        yotiSDKButton.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        message.setText(R.string.loc_error_unknown); }
    });
```

There is also a listener that you can set to be notified when the intent has been sent to the Yoti app.
When this happens you would probably want to restore your state.

Attention: This listener is now deprecated.
```java
        yotiSDKButton.setOnYotiCalledListener(new YotiSDKButton.OnYotiCalledListener() {
            @Override
            public void onYotiCalled() {
                // Restore the original state
            }
        }); 
```
Please use the below listener to get notified when the intent has been sent to app based on the specified theme:
```java
        yotiSDKButton.setOnAppCalledListener(new YotiSDKButton.OnAppCalledListener() {
            @Override
            public void onAppCalled() {
                // Restore the original state
            }
        }); 
```

[See this code in one of our sample apps](./sample-app/src/main/java/com/yoti/mobile/android/sdk/sampleapp/MainActivity.java)

You can activate a verbose mode for the SDK by using this method :
```java
YotiSDK.enableSDKLogging(true);
```

If the Yoti app is not installed in the user's phone by default the SDK will send and intent to
open a website that invites the user to download the Yoti app.
Alternatively, an error listener can be set up so you can deal with this situation by yourself.

Attention: This listener is now deprecated.
```java
        yotiSDKButton.setOnYotiAppNotInstalledListener(new YotiSDKButton.OnYotiAppNotInstalledListener() {
            @Override
            public void onYotiAppNotInstalledError(YotiSDKNoYotiAppException cause) {
                // Do whatever you want here
            }
        });
```
Please use the below listener to determine whether the Yoti/EasyId app is not installed:
```java
        mYotiSDKButton.setOnAppNotInstalledListener(new OnAppNotInstalledListener() {
            @Override
            public void onAppNotInstalled(final YotiSDKAppNotInstalledException cause, final String appURL) {
                // The exception will provide info on which app is not installed
                // appURL is the app's play store URL, launch the browser intent to allow user to download the app and proceed
            }
        });
```
[See this code in one of our sample apps](./sample-app/src/main/java/com/yoti/mobile/android/sdk/sampleapp/MainActivity.java)

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
For any questions or support please email [clientsupport@yoti.com](mailto:clientsupport@yoti.com). Please provide the following to get you up and working as quickly as possible:

* Software version on the phone
* Language of backend SDK
* Screenshot of error

Once we have answered your question we may contact you again to discuss Yoti products and services. If you’d prefer us not to do this, please let us know when you e-mail.
