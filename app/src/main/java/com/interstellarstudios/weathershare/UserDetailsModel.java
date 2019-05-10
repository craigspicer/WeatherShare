package com.interstellarstudios.weathershare;

public class UserDetailsModel {

    private String userEmail;
    private String signUpDate;

    public UserDetailsModel() {
        //empty constructor needed
    }

    public UserDetailsModel(String userEmail, String signUpDate) {

        this.userEmail = userEmail;
        this.signUpDate = signUpDate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getSignUpDate() {
        return signUpDate;
    }
}