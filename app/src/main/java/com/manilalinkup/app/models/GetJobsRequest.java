package com.manilalinkup.app.models;

public class GetJobsRequest {
    private Integer limit, minSalary, maxSalary;
    private String employer, startAfter;

    public GetJobsRequest(
        Integer limit,
        String startAfter,
        Integer minSalary,
        Integer maxSalary,
        String employer
    ) {
        this.limit = limit;
        this.startAfter = startAfter;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.employer = employer;
    }
}
