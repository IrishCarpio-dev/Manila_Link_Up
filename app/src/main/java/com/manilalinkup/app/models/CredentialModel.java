package com.manilalinkup.app.models;

import android.net.Uri;

public class CredentialModel {
    private String fileName;
    private Uri fileUri;
    private String status;

    public CredentialModel(String fileName, Uri fileUri) {
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.status = "";
    }

    public CredentialModel(String fileName, String status) {
        this.fileName = fileName;
        this.status = status;
    }

    public String getFileName() { return fileName; }
    public Uri getFileUri() { return fileUri; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}