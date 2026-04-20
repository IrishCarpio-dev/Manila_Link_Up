package com.manilalinkup.app.models;

public class SubmitRatingRequest {
    String applicationId;
    int score;
    String comment;

    public SubmitRatingRequest(String applicationId, int score, String comment) {
        this.applicationId = applicationId;
        this.score = score;
        this.comment = comment;
    }
}
