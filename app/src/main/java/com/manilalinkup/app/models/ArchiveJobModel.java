package com.manilalinkup.app.models;

public class ArchiveJobModel {
    String id;
    String title;
    String description;
    String location;
    Double salary;
    String duration;
    String deletedAt;
    String filledAt;
    String expiresAt;
    Boolean isRateEnabled;
    HiredApplication hiredApplication;
    java.util.List<String> tags;

    public ArchiveJobModel() {}

    public String getJobId() { return id; }
    public String getJobTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public Double getSalary() { return salary; }
    public String getDuration() { return duration; }
    public java.util.List<String> getTags() { return tags; }
    public Boolean isRateEnabled() { return isRateEnabled; }

    public String getApplicationId() {
        return hiredApplication != null ? hiredApplication.getId() : null;
    }
    public Integer getApplicationStatus() {
        return hiredApplication != null ? hiredApplication.getStatus() : null;
    }
    public boolean isEmployerCompleted() {
        return hiredApplication != null && hiredApplication.getEmployerCompletedAt() != null;
    }
    public String getSeekerName() {
        if (hiredApplication == null || hiredApplication.getSeeker() == null) return null;
        String first = hiredApplication.getSeeker().getFirstName() != null ? hiredApplication.getSeeker().getFirstName() : "";
        String last = hiredApplication.getSeeker().getLastName() != null ? hiredApplication.getSeeker().getLastName() : "";
        return (first + " " + last).trim();
    }

    public String getStatusText() {
        if (hiredApplication != null) {
            Integer status = hiredApplication.getStatus();
            if (Integer.valueOf(6).equals(status)) return "COMPLETED";
            return "HIRED";
        }
        if (deletedAt != null) return "ARCHIVED";
        return "EXPIRED";
    }

    public String getInsightText() {
        if (hiredApplication != null && hiredApplication.getSeeker() != null) {
            String first = hiredApplication.getSeeker().getFirstName() != null ? hiredApplication.getSeeker().getFirstName() : "";
            String last = hiredApplication.getSeeker().getLastName() != null ? hiredApplication.getSeeker().getLastName() : "";
            String name = (first + " " + last).trim();
            Integer status = hiredApplication.getStatus();
            return Integer.valueOf(6).equals(status) ? "Completed by: " + name : "In progress with: " + name;
        }
        if (deletedAt != null) return "Manually archived";
        return "Job has expired";
    }

    public static class HiredApplication {
        String id;
        Integer status;
        String employerCompletedAt;
        String seekerCompletedAt;
        SeekerName seeker;

        public String getId() { return id; }
        public Integer getStatus() { return status; }
        public String getEmployerCompletedAt() { return employerCompletedAt; }
        public String getSeekerCompletedAt() { return seekerCompletedAt; }
        public SeekerName getSeeker() { return seeker; }

        public static class SeekerName {
            String firstName;
            String lastName;
            public String getFirstName() { return firstName; }
            public String getLastName() { return lastName; }
        }
    }
}
