package com.manilalinkup.app.models;

public class ApplicationModel {
    String id;
    Integer status;
    String updatedAt;

    public ApplicationModel(String id, Integer status, String updatedAt) {
        this.id = id;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public Integer getStatus() { return status; }
    public String getUpdatedAt() { return updatedAt; }
}
