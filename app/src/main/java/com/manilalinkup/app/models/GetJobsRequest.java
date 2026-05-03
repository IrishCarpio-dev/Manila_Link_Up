package com.manilalinkup.app.models;

public class GetJobsRequest {
    private Integer limit;
    private Double minSalary, maxSalary;
    private String employer, startAfter, startAfterExpiresAt, sortBy, sortDirection;

    public GetJobsRequest(
        Integer limit,
        String startAfter,
        String startAfterExpiresAt,
        Double minSalary,
        Double maxSalary,
        String employer,
        String sortBy,
        String sortDirection
    ) {
        this.limit = limit;
        this.startAfter = startAfter;
        this.startAfterExpiresAt = startAfterExpiresAt;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.employer = employer;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
}
