package com.manilalinkup.app.models;

import java.util.List;

public class JobListResponse {
    private String message;
    private List<JobModel> data;
    private boolean hasMore;
    private NextCursor nextCursor;

    public String getMessage() { return message; }
    public List<JobModel> getData() { return data; }
    public boolean isHasMore() { return hasMore; }
    public NextCursor getNextCursor() { return nextCursor; }

    public static class NextCursor {
        private String primary;
        private String expiresAt;

        public String getPrimary() { return primary; }
        public String getExpiresAt() { return expiresAt; }
    }
}
