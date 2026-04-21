package com.manilalinkup.app.models;

public class SendMessageRequest {
    String chatId;
    String text;

    public SendMessageRequest(String chatId, String text) {
        this.chatId = chatId;
        this.text = text;
    }
}
