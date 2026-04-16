package com.manilalinkup.app.models;

public class UnregisterDeviceRequest {
    String fcmToken;

    public UnregisterDeviceRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
