package com.manilalinkup.app.models;

import android.net.Uri;

public class CredentialModel {
    private String fileName;
    private Uri fileUri;

    public CredentialModel(String fileName, Uri fileUri) {
        this.fileName = fileName;
        this.fileUri = fileUri;
    }

    public String getFileName() { return fileName; }
    public Uri getFileUri() { return fileUri; }
}