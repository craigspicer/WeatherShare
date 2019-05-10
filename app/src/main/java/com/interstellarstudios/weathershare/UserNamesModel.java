package com.interstellarstudios.weathershare;

public class UserNamesModel {

    private String firstName;
    private String lastName;

    public UserNamesModel() {
        //empty constructor needed
    }

    public UserNamesModel(String firstName, String lastName) {

        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
