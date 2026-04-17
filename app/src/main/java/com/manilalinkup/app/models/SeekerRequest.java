package com.manilalinkup.app.models;

public class SeekerRequest {
    String firstName;
    String middleName;
    String lastName;
    String suffix;
    String email;
    String mobileNumber;
    String birthDate;

    public SeekerRequest(String firstName, String middleName, String lastName, String suffix, String email, String mobileNumber, String birthDate) {
        this.firstName = firstName;
        this.middleName = middleName.isEmpty() ? null : middleName;
        this.lastName = lastName;
        this.suffix = suffix.isEmpty() ? null : suffix;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.birthDate = birthDate;
    }
}
