package com.manilalinkup.app;

public class SeekerChatModel {

    int employerImage;
    String employerName;
    String messagePreview;
    String messageTimeStamp;

    public SeekerChatModel(int employerImage, String employerName, String messagePreview, String messageTimeStamp) {
        this.employerImage = employerImage;
        this.employerName = employerName;
        this.messagePreview = messagePreview;
        this.messageTimeStamp = messageTimeStamp;
    }

    public int getEmployerImage() {
        return employerImage;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getMessagePreview() {
        return messagePreview;
    }

    public String getMessageTimeStamp() {
        return messageTimeStamp;
    }
}
