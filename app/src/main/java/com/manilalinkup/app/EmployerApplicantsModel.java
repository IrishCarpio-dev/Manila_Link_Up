package com.manilalinkup.app;

public class EmployerApplicantsModel {
    int profilePhoto;
    String firstname;
    String lastname;
    int age;
    String location;

    public EmployerApplicantsModel(int profilePhoto, String firstname, String lastname, int age, String location) {
        this.profilePhoto = profilePhoto;
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.location = location;
    }

    public int getProfilePhoto() {
        return profilePhoto;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getAge() {
        return age;
    }

    public String getLocation() {
        return location;
    }
}
