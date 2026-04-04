package com.manilalinkup.app;

public class EmployerAllRatingsModel {
    String uid;
    String profilePhoto;
    String firstname;
    String lastname;
    float ratingScore;
    long timeStamp;
    String ratingMessage;

    public EmployerAllRatingsModel() {
    }

    public EmployerAllRatingsModel(String uid, String profilePhoto, String firstname, String lastname, float ratingScore, long timeStamp, String ratingMessage) {
        this.uid = uid;
        this.profilePhoto = profilePhoto;
        this.firstname = firstname;
        this.lastname = lastname;
        this.ratingScore = ratingScore;
        this.timeStamp = timeStamp;
        this.ratingMessage = ratingMessage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public float getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(float ratingScore) {
        this.ratingScore = ratingScore;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getRatingMessage() {
        return ratingMessage;
    }

    public void setRatingMessage(String ratingMessage) {
        this.ratingMessage = ratingMessage;
    }

    public String getFullName(){
        return firstname + " " + lastname;
    }

    public String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timeStamp));
    }
}
