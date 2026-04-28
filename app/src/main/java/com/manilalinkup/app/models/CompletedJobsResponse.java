package com.manilalinkup.app.models;

import java.util.List;

public class CompletedJobsResponse {
    private String message;
    private List<AppliedJobModel> data;
    private boolean hasMore;
    private String nextCursor;

    public String getMessage() { return message; }
    public List<AppliedJobModel> getData() { return data; }
    public boolean isHasMore() { return hasMore; }
    public String getNextCursor() { return nextCursor; }
}
