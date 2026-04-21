package com.manilalinkup.app.models;

public class GetChatsRequest {
    Integer limit;
    String startAfter;

    public GetChatsRequest(Integer limit, String startAfter) {
        this.limit = limit;
        this.startAfter = startAfter;
    }
}
