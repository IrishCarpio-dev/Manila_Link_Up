package com.manilalinkup.app.utilities;

public enum ImageUploadSelection {
    PROFILE(0), COVER(1), CLEARANCE(2), ID(3);

    private final int code;

    ImageUploadSelection(int code) {
        this.code = code;
    }

    public int getCode() { return code; }
}
