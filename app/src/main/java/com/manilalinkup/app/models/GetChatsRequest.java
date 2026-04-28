package com.manilalinkup.app.models;

public class GetChatsRequest {
    Integer limit;
    String startAfter;
    Boolean includeCompleted;

    public GetChatsRequest(Integer limit, String startAfter) {
        this.limit = limit;
        this.startAfter = startAfter;
    }

    public GetChatsRequest(Integer limit, String startAfter, Boolean includeCompleted) {
        this.limit = limit;
        this.startAfter = startAfter;
        this.includeCompleted = includeCompleted;
    }
}
