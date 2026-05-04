package com.manilalinkup.app.models;

import java.util.List;

public class ChatsResponse {
    private String message;
    private List<ChatListItemModel> data;
    private boolean hasMore;
    private String nextCursor;

    public String getMessage() { return message; }
    public List<ChatListItemModel> getData() { return data; }
    public boolean isHasMore() { return hasMore; }
    public String getNextCursor() { return nextCursor; }
}
