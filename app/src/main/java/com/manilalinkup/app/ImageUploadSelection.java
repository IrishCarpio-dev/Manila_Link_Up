package com.manilalinkup.app;

public enum ImageUploadSelection {
    PROFILE(0), COVER(1), CLEARANCE(2), ID(3);

    private final int code;


    // Constructor must be private or package-private
    ImageUploadSelection(int code) {
        this.code = code;
    }

    public int getCode() { return code; }
}
