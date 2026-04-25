package com.manilalinkup.app.models;

public class GetMessagesRequest {
    String chatId;
    Integer limit;
    String startAfter;
    String newerThan;

    public GetMessagesRequest(String chatId, Integer limit, String startAfter) {
        this.chatId = chatId;
        this.limit = limit;
        this.startAfter = startAfter;
    }

    public GetMessagesRequest(String chatId, Integer limit, String startAfter, String newerThan) {
        this.chatId = chatId;
        this.limit = limit;
        this.startAfter = startAfter;
        this.newerThan = newerThan;
    }
}
