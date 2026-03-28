package com.manilalinkup.app;

public class EmployerChatModel {

    int seekerImage;
    String seekerName;
    String messagePreview;
    String messageTimeStamp;

    public EmployerChatModel(int seekerImage, String seekerName, String messagePreview, String messageTimeStamp) {
        this.seekerImage = seekerImage;
        this.seekerName = seekerName;
        this.messagePreview = messagePreview;
        this.messageTimeStamp = messageTimeStamp;
    }

    public int getSeekerImage() {
        return seekerImage;
    }

    public String getSeekerName() {
        return seekerName;
    }

    public String getMessagePreview() {
        return messagePreview;
    }

    public String getMessageTimeStamp() {
        return messageTimeStamp;
    }

}
