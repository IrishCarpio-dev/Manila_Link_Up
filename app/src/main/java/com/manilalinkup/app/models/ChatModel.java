package com.manilalinkup.app.models;

public class ChatModel {
    String id;
    String message;
    String uid;
    String readAt;
    long timestamp;

    public ChatModel() {}

    public ChatModel(String message, String uid, long timestamp) {
        this.message = message;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getReadAt() { return readAt; }
    public void setReadAt(String readAt) { this.readAt = readAt; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getFormattedTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }
}
