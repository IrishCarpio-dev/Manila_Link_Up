package com.manilalinkup.app.models;

public class GetCompletedJobsRequest {
    Integer limit;
    String startAfter;

    public GetCompletedJobsRequest(Integer limit, String startAfter) {
        this.limit = limit;
        this.startAfter = startAfter;
    }
}
