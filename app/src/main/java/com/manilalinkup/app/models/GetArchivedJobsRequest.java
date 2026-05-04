package com.manilalinkup.app.models;

public class GetArchivedJobsRequest {
    Integer limit;
    String status;

    public GetArchivedJobsRequest(Integer limit, String status) {
        this.limit = limit;
        this.status = status;
    }
}
