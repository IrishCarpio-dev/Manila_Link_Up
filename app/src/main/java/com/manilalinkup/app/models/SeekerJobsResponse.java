package com.manilalinkup.app.models;

import java.util.List;

public class SeekerJobsResponse {
    private String message;
    private List<JobModel> data;
    private boolean hasMore;
    private NextCursor nextCursor;

    public String getMessage() { return message; }
    public List<JobModel> getData() { return data; }
    public boolean isHasMore() { return hasMore; }
    public NextCursor getNextCursor() { return nextCursor; }

    public static class NextCursor {
        private String expiresAt;
        private String createdAt;

        public String getExpiresAt() { return expiresAt; }
        public String getCreatedAt() { return createdAt; }
    }
}
