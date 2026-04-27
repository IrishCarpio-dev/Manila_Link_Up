package com.manilalinkup.app.models;

public class ArchiveJobModel {
    String id;
    String title;
    String deletedAt;
    String filledAt;
    String expiresAt;
    HiredApplication hiredApplication;

    public ArchiveJobModel() {}

    public String getJobId() { return id; }
    public String getJobTitle() { return title; }

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
