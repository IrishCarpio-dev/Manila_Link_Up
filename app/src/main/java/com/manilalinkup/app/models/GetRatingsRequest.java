package com.manilalinkup.app.models;

public class GetRatingsRequest {
    String userUid;
    Integer limit;
    String startAfter;

    public GetRatingsRequest(String userUid, Integer limit, String startAfter) {
        this.userUid = userUid;
        this.limit = limit;
        this.startAfter = startAfter;
    }
}
