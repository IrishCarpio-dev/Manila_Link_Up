package com.manilalinkup.app.models;

import com.google.gson.annotations.SerializedName;

public class GetSeekerJobsRequest {
    @SerializedName("mode")
    private String mode;

    @SerializedName("limit")
    private Integer limit;

    @SerializedName("startAfter")
    private String startAfter;

    @SerializedName("startAfterCreatedAt")
    private String startAfterCreatedAt;

    public GetSeekerJobsRequest(String mode, Integer limit, String startAfter, String startAfterCreatedAt) {
        this.mode = mode;
        this.limit = limit;
        this.startAfter = startAfter;
        this.startAfterCreatedAt = startAfterCreatedAt;
    }
}
