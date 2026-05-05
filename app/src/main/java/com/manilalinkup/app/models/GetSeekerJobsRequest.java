package com.manilalinkup.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetSeekerJobsRequest {
    @SerializedName("mode") private String mode;
    @SerializedName("limit") private Integer limit;
    @SerializedName("sortBy") private String sortBy;
    @SerializedName("order") private String order;
    @SerializedName("filterBy") private List<String> filterBy;
    @SerializedName("location") private String location;
    @SerializedName("tags") private List<String> tags;
    @SerializedName("startAfter") private String startAfter;
    @SerializedName("startAfterCreatedAt") private String startAfterCreatedAt;
    @SerializedName("startAfterSalary") private Double startAfterSalary;
    @SerializedName("startAfterOffset") private Integer startAfterOffset;

    public GetSeekerJobsRequest(String mode, Integer limit, String startAfter, String startAfterCreatedAt) {
        this.mode = mode;
        this.limit = limit;
        this.startAfter = startAfter;
        this.startAfterCreatedAt = startAfterCreatedAt;
    }

    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public void setOrder(String order) { this.order = order; }
    public void setFilterBy(List<String> filterBy) { this.filterBy = filterBy; }
    public void setLocation(String location) { this.location = location; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public void setStartAfterCreatedAt(String startAfterCreatedAt) { this.startAfterCreatedAt = startAfterCreatedAt; }
    public void setStartAfterSalary(Double startAfterSalary) { this.startAfterSalary = startAfterSalary; }
    public void setStartAfterOffset(Integer startAfterOffset) { this.startAfterOffset = startAfterOffset; }
}
