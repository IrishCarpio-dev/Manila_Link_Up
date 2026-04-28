package com.manilalinkup.app.models;

public class GetNotificationsRequest {
    private Integer limit;
    private String startAfter;
    private Boolean unreadOnly;

    public GetNotificationsRequest() {}

    public GetNotificationsRequest(Integer limit, String startAfter, Boolean unreadOnly) {
        this.limit = limit;
        this.startAfter = startAfter;
        this.unreadOnly = unreadOnly;
    }
}
