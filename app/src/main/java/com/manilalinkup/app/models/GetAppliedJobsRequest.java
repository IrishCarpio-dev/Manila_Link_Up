package com.manilalinkup.app.models;

public class GetAppliedJobsRequest {
    Integer limit;
    String startAfter;
    Integer status;

    public GetAppliedJobsRequest(Integer limit, String startAfter, Integer status) {
        this.limit = limit;
        this.startAfter = startAfter;
        this.status = status;
    }
}
