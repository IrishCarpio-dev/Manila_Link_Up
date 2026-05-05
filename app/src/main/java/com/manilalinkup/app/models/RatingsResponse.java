package com.manilalinkup.app.models;

import java.util.List;

public class RatingsResponse {
    private String message;
    private List<RatingModel> data;
    private boolean hasMore;
    private String nextCursor;

    public String getMessage() { return message; }
    public List<RatingModel> getData() { return data; }
    public boolean isHasMore() { return hasMore; }
    public String getNextCursor() { return nextCursor; }
}
