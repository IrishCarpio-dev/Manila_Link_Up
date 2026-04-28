package com.manilalinkup.app.models;

public class NotificationsModel {
    private final int imageType;
    private final String notifTitle;
    private final String descriptionNotif;
    private final String notifTimeStamp;
    private final boolean isRead;
    private final String type;
    private final String notifId;

    public NotificationsModel(int imageType, String notifTitle, String descriptionNotif,
                              String notifTimeStamp, boolean isRead, String type, String notifId) {
        this.imageType = imageType;
        this.notifTitle = notifTitle;
        this.descriptionNotif = descriptionNotif;
        this.notifTimeStamp = notifTimeStamp;
        this.isRead = isRead;
        this.type = type;
        this.notifId = notifId;
    }

    public NotificationsModel(int imageType, String notifTitle, String descriptionNotif, String notifTimeStamp) {
        this(imageType, notifTitle, descriptionNotif, notifTimeStamp, true, null, null);
    }

    public int getType()           { return imageType; }
    public String getTitle()       { return notifTitle; }
    public String getDescription() { return descriptionNotif; }
    public String getTimestamp()   { return notifTimeStamp; }
    public boolean isRead()        { return isRead; }
    public String getNotifType()   { return type; }
    public String getNotifId()     { return notifId; }
}
