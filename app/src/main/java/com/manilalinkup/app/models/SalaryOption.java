package com.manilalinkup.app.models;

public class SalaryOption {
    private String displayText;
    private int value;

    public SalaryOption(String displayText, int value) {
        this.displayText = displayText;
        this.value = value;
    }

    public String getDisplayText() { return displayText; }
    public int getValue() { return value; }

}
