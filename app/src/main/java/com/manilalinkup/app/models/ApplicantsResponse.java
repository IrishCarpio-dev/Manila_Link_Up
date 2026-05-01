package com.manilalinkup.app.models;

import java.util.List;

public class ApplicantsResponse {
    private String message;
    private List<ApplicantModel> data;
    private boolean hasMore;
    private String nextCursor;

    public String getMessage() { return message; }
    public List<ApplicantModel> getData() { return data; }
    public boolean isHasMore() { return hasMore; }
    public String getNextCursor() { return nextCursor; }
}
