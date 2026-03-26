package com.manilalinkup.app;

public class EmployerNotificationsModel {
    int imageType; // "message", "reminder", or "applicant"
    String notifTitle;
    String descriptionNotif;
    String notifTimeStamp;

    public EmployerNotificationsModel(int imageType, String notifTitle, String descriptionNotif, String notifTimeStamp) {
        this.imageType = imageType;
        this.notifTitle = notifTitle;
        this.descriptionNotif = descriptionNotif;
        this.notifTimeStamp = notifTimeStamp;
    }
    public int getType() {
        return imageType;
    }

    public String getTitle() {
        return notifTitle;
    }

    public String getDescription() {
        return descriptionNotif;
    }

    public String getTimestamp() {
        return notifTimeStamp;
    }
}
