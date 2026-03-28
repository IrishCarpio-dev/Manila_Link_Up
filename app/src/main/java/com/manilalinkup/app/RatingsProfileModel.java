package com.manilalinkup.app;

public class RatingsProfileModel {

    String ratingMessage;
    String raterName;
    float ratingScore;
    public RatingsProfileModel(String ratingMessage, String raterName, float ratingScore) {
        this.ratingMessage = ratingMessage;
        this.raterName = raterName;
        this.ratingScore = ratingScore;
    }

}
