package com.manilalinkup.app.models;

import java.util.Map;

public class NotifyChatRequest {
    String chatId;
    String title;
    String body;
    Map<String, String> data;

    public NotifyChatRequest(String chatId, String title, String body, Map<String, String> data) {
        this.chatId = chatId;
        this.title = title;
        this.body = body;
        this.data = data;
    }
}
