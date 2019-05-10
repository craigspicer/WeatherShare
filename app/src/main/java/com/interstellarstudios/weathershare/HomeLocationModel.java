package com.interstellarstudios.weathershare;

public class HomeLocationModel {

    private String homeLocation;

    public HomeLocationModel() {
        //empty constructor needed
    }

    public HomeLocationModel(String homeLocation) {

        this.homeLocation = homeLocation;
    }

    public String getHomeLocation() {
        return homeLocation;
    }
}
