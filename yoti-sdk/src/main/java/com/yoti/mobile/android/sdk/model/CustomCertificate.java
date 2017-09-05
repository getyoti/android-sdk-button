package com.yoti.mobile.android.sdk.model;

import android.support.annotation.RawRes;
import android.text.TextUtils;

/**
 * Define all the necessary elements for importing a custom certificate.
 */

public class CustomCertificate {

    @RawRes
    private int certificateResourceId;
    private String storeName;
    private String password;
    private String alias;

    public int getCertificateResourceId() {
        return certificateResourceId;
    }

    public void setCertificateResourceId(int certificateResourceId) {
        this.certificateResourceId = certificateResourceId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(storeName) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(alias) && certificateResourceId != 0;
    }
}
