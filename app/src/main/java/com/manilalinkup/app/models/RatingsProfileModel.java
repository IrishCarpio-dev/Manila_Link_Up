package com.manilalinkup.app.models;

public class RatingsProfileModel {

    String ratingMessage;
    String raterName;
    float ratingScore;
    public RatingsProfileModel(String ratingMessage, String raterName, float ratingScore) {
        this.ratingMessage = ratingMessage;
        this.raterName = raterName;
        this.ratingScore = ratingScore;
    }

    public String getRatingMessage() { return ratingMessage; }

    public String getRaterName() { return raterName; }

    public float getRatingScore() { return ratingScore; }
}
