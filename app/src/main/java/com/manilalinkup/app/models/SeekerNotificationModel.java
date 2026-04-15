package com.manilalinkup.app.models;

import androidx.annotation.NonNull;

/**
 * Model class for Seeker Notifications.
 * Suppressing "unused" because these are often accessed dynamically by adapters or databases.
 */
@SuppressWarnings("unused")
public class SeekerNotificationModel {
    private int imageType;
    private String notifTitle;
    private String descriptionNotif;
    private String notifTimeStamp;

    // Required empty constructor for Firebase/JSON parsing
    public SeekerNotificationModel() {
    }

    public SeekerNotificationModel(int imageType, String notifTitle, String descriptionNotif, String notifTimeStamp) {
        this.imageType = imageType;
        this.notifTitle = notifTitle;
        this.descriptionNotif = descriptionNotif;
        this.notifTimeStamp = notifTimeStamp;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public String getNotifTitle() {
        return notifTitle;
    }

    public void setNotifTitle(String notifTitle) {
        this.notifTitle = notifTitle;
    }

    public String getDescriptionNotif() {
        return descriptionNotif;
    }

    public void setDescriptionNotif(String descriptionNotif) {
        this.descriptionNotif = descriptionNotif;
    }

    public String getNotifTimeStamp() {
        return notifTimeStamp;
    }

    public void setNotifTimeStamp(String notifTimeStamp) {
        this.notifTimeStamp = notifTimeStamp;
    }

    // Overriding toString helps with debugging in Logcat
    @NonNull
    @Override
    public String toString() {
        return "Notification: " + notifTitle + " at " + notifTimeStamp;
    }
}