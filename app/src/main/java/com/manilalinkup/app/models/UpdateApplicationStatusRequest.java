package com.manilalinkup.app.models;

public class UpdateApplicationStatusRequest {
    String applicationId;
    Integer status;

    public UpdateApplicationStatusRequest(String applicationId, Integer status) {
        this.applicationId = applicationId;
        this.status = status;
    }
}
