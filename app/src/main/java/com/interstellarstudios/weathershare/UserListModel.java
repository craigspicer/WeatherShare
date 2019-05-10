package com.interstellarstudios.weathershare;

public class UserListModel {

    private String userId;

    public UserListModel() {
        //empty constructor needed
    }

    public UserListModel(String userId) {

        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
