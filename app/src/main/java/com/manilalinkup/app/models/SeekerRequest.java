package com.manilalinkup.app.models;

public class SeekerRequest {
    private String uid;
    String firstName;
    String middleName;
    String lastName;
    String suffix;
    String email;
    String mobileNumber;
    String birthDate;

    public SeekerRequest(String uid, String firstName, String middleName, String lastName, String suffix, String email, String mobileNumber, String birthDate) {
        this.uid = uid;
        this.firstName = firstName;
        this.middleName = middleName.isEmpty() ? null : middleName;
        this.lastName = lastName;
        this.suffix = suffix.isEmpty() ? null : suffix;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.birthDate = birthDate;
    }
}
