package com.manilalinkup.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class NotificationItemModel {
    private String id;
    private String uid;
    private String type;
    private String title;
    private String body;
    private Map<String, String> data;
    @SerializedName(value = "readAt", alternate = {"read_at"})
    private String readAt;
    @SerializedName(value = "createdAt", alternate = {"created_at"})
    private String createdAt;

    public String getId() { return id; }
    public String getUid() { return uid; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public Map<String, String> getData() { return data; }
    public String getReadAt() { return readAt; }
    public String getCreatedAt() { return createdAt; }
    public boolean isRead() { return readAt != null; }

    public String getDataValue(String key) {
        return (data != null) ? data.get(key) : null;
    }
}
