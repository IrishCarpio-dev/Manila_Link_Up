package com.manilalinkup.app.models;

public class RegisterDeviceRequest {
    String fcmToken;
    String platform;

    public RegisterDeviceRequest(String fcmToken, String platform) {
        this.fcmToken = fcmToken;
        this.platform = platform;
    }
}
