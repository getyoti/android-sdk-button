package com.yoti.mobile.android.sdk.model;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yoti.mobile.android.sdk.exceptions.YotiSDKNotValidScenarioException;

/**
 * This Object will hold all the necessary attributes to successfully perform a sharing of attributes
 * according to what has been defined when you created your application and scenario on the Yoti dashboard.
 */

public class Scenario {

    private String useCaseId;
    private String clientSDKId;
    private String scenarioId;
    private String callbackAction;
    private String qrCodeUrl;
    private String callbackBackendAction;
    private CustomCertificate customCertificate;
    private String callbackBackendUrl;

    public String getUseCaseId() {
        return useCaseId;
    }

    public void setUseCaseId(String useCaseId) {
        this.useCaseId = useCaseId;
    }

    public String getClientSDKId() {
        return clientSDKId;
    }

    public void setClientSDKId(String sdkId) {
        this.clientSDKId = sdkId;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getCallbackAction() {
        return callbackAction;
    }

    public void setCallbackAction(String callbackAction) {
        this.callbackAction = callbackAction;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public String getCallbackBackendAction() {
        return callbackBackendAction;
    }

    public void setCallbackBackendAction(String callbackBackendAction) {
        this.callbackBackendAction = callbackBackendAction;
    }

    public CustomCertificate getCustomCertificate() {
        return customCertificate;
    }

    public void setCustomCertificate(CustomCertificate customCertificate) {
        this.customCertificate = customCertificate;
    }

    public String getCallbackBackendUrl() {
        return callbackBackendUrl;
    }

    public void setCallbackBackendUrl(String callbackBackendUrl) {
        this.callbackBackendUrl = callbackBackendUrl;
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(this.useCaseId)
                && !TextUtils.isEmpty(this.clientSDKId)
                && !TextUtils.isEmpty(this.scenarioId)
                && !TextUtils.isEmpty(this.callbackAction);
    }

    public static class Builder {

        private final Scenario mScenario;

        public Builder() {
            this.mScenario = new Scenario();
        }

        public Builder setUseCaseId(@NonNull String id) throws YotiSDKNotValidScenarioException {

            if (TextUtils.isEmpty(id)) {
                throw new YotiSDKNotValidScenarioException("Use case id cannot be null");
            }

            mScenario.setUseCaseId(id);
            return this;
        }

        public Builder setClientSDKId(String id) {
            mScenario.setClientSDKId(id);
            return this;
        }

        public Builder setScenarioId(String id) {
            mScenario.setScenarioId(id);
            return this;
        }

        public Builder setBackendCallbackAction(String action) {
            mScenario.setCallbackBackendAction(action);
            return this;
        }

        public Builder setCallbackAction(String action) {
            mScenario.setCallbackAction(action);
            return this;
        }

        public Builder setCustomCertificate(CustomCertificate customCertificate) {
            if (customCertificate.isValid()) {
                mScenario.setCustomCertificate(customCertificate);
            }
            return this;
        }

        public Scenario create() {
            return mScenario;
        }
    }
}
