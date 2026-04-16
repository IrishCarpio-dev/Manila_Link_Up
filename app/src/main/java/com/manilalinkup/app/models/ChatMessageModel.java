package com.manilalinkup.app.models;

public class ChatMessageModel {
    String id;
    String senderUid;
    String text;
    String readAt;
    String createdAt;

    public ChatMessageModel() {}

    public String getId() { return id; }
    public String getSenderUid() { return senderUid; }
    public String getText() { return text; }
    public String getReadAt() { return readAt; }
    public String getCreatedAt() { return createdAt; }
}
