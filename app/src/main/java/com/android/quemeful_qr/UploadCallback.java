package com.android.quemeful_qr;

public interface UploadCallback {
    void onSuccess(String downloadUrl);
    void onFailure(Exception e);
}
