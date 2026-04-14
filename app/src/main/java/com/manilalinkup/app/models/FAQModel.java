package com.manilalinkup.app.models;

public class FAQModel {
    private String question;
    private String answer;

    public FAQModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
}