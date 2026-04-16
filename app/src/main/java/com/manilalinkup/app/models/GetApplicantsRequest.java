package com.manilalinkup.app.models;

public class GetApplicantsRequest {
    String jobId;
    Integer limit;
    String startAfter;
    Integer status;

    public GetApplicantsRequest(String jobId, Integer limit, String startAfter, Integer status) {
        this.jobId = jobId;
        this.limit = limit;
        this.startAfter = startAfter;
        this.status = status;
    }
}
