package com.manilalinkup.app.models;

public class ExperienceModel {
    private String title, company, duration;
    public ExperienceModel(String title, String company, String duration) {
        this.title = title; this.company = company; this.duration = duration;
    }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getDuration() { return duration; }
}