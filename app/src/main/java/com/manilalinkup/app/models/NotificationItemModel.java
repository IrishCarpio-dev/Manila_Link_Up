package com.manilalinkup.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class NotificationItemModel {
    private int id;
    private String type;
    private String title;
    private String message;
    @SerializedName("is_read")
    private boolean isRead;
    @SerializedName("created_at")
    private String createdAt;
    private Map<String, String> data;

    public int getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }
    public Map<String, String> getData() { return data; }

    public String getDataValue(String key) {
        return (data != null) ? data.get(key) : null;
    }
}
