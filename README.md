# yoti-sdk-button-mobile-android
The mobile SDK purpose is to provide 3rd party application the ability to request attributes from a Yoti user while leveraging the Yoti mobile App.
It is an interaction between a 3rd Party app and Yoti app facilitated by a very lightweight SDKs.

## How to use
A Yoti application will have to be defined, the documentation is available [here](https://www.yoti.com/developers/documentation/#how-to-set-up-an-application)


Add this dependency in the "build.grade" file inside your application module directory.

```javascript
dependencies {
    compile(group: 'com.yoti.mobile.android.sdk', name: 'yoti-button-sdk', version: '0.0.3', classifier: 'release', ext: 'aar'){
            transitive = true
        }
```


The SDK need to be initialised with at least a Scenario which contain the client SDK Id, the Scenario Id and the callback action (the callback action will be use to call back the broadcast receiver of the app) and a unique useCaseId.

```javascript
Scenario scenario = new Scenario.Builder()
        .setUseCaseId(MY_USE_CASE_ID)
        .setClientSDKId(clientSDKId)
        .setScenarioId(scenarioId)
        .setCallbackAction(MY_CALLBACK_ACTION)
        .setBackendCallbackAction(MY_BACKEND_CALLBACK_ACTION) // Optional if you choose to make the call to your backend yourself
        .setCustomCertificate(customCertificate) // Optionnal used only if your backend has a self signed certificate for example
        .create();

YotiSDK.addScenario(scenario);
```

The SDK provide a custom Button you can use in your layout, do not forget to set the useCaseId, it's the link with the Scenario you defined earlier.

```xml
<com.yoti.mobile.android.sdk.YotiSDKButton
        android:id="@+id/my_id"
        android:height="wrap_content"
        android:width="wrap_content"
        android:text="My Yoti Button"
        yoti:useCaseId=MY_USE_CASE_ID/>
```

The SDK uses a BroadcastReceiver to get the callback URL from the Yoti app. With this callback URL, you will be able to access the value of the shared attributes. 
The SDK let you the choice to do the call to your backend or let the SDK can do it for you. 
If you choose to let the SDK do it for you, it will start an IntentService, do the network call and callback the BroadcastReceiver using the MY_BACKEND_CALLBACK_ACTION action.
To be able to listen to those actions you will need to create a BroadcastReceiver, define the callback action for the Yoti app and if necessary the callback action for the IntentService. 
The AbstractYotiBroadcastReceiver will handle the logic regarding those actions, that's why your BroadcastReceiver will have to extend it. 
 The first action defined (MY_CALLBACK_ACTION) will be used by the Yoti app to give you the result of the attributes sharing with your app. This is mandatory.
 The second action (MY_BACKEND_CALLBACK_ACTION) will be used by the SDK if you choose to let it do the call to your backend to retrieve the attributes shared. This is optional

```xml
<receiver android:name=".MyBroadcastReceiver">
    <intent-filter>
        <action android:name="MY_CALLBACK_ACTION"/>
        <action android:name="MY_BACKEND_CALLBACK_ACTION"/>
    </intent-filter>
</receiver>
```


```javascript
public class MyBroadcastReceiver extends AbstractYotiBroadcastReceiver {

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

You can activate a verbose mode for the SDK by using this method :
```javascript
YotiSDK.enableSDKLogging(true);
```