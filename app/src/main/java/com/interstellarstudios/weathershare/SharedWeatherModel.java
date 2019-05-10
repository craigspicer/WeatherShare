package com.interstellarstudios.weathershare;

public class SharedWeatherModel {

    private String location;

    public SharedWeatherModel() {
        //empty constructor needed
    }

    public SharedWeatherModel(String location) {

        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
