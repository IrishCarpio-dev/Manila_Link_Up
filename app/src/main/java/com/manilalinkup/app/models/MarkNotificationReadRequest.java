package com.manilalinkup.app.models;

public class MarkNotificationReadRequest {
    private String notificationId;
    private Boolean all;

    public MarkNotificationReadRequest(boolean all) {
        this.all = all;
    }

    public MarkNotificationReadRequest(String notificationId) {
        this.notificationId = notificationId;
    }
}
