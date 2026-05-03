package com.manilalinkup.app.models;

public class GetCompletedJobsRequest {
    String uid;
    Integer limit;
    String startAfter;

    public GetCompletedJobsRequest(String uid, Integer limit, String startAfter) {
        this.uid = uid;
        this.limit = limit;
        this.startAfter = startAfter;
    }
}
