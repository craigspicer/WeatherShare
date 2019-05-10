package com.interstellarstudios.weathershare;

public class UserIdModel {

    private String userId;

    public UserIdModel() {
        //empty constructor needed
    }

    public UserIdModel(String userId) {

        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
