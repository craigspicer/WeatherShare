package com.interstellarstudios.weathershare;

public class FavouriteLocationModel {

    private String favouriteLocation1;
    private String favouriteLocation2;
    private String favouriteLocation3;
    private String favouriteLocation4;
    private String favouriteLocation5;

    public FavouriteLocationModel() {
        //empty constructor needed
    }

    public FavouriteLocationModel(String favouriteLocation1, String favouriteLocation2, String favouriteLocation3, String favouriteLocation4, String favouriteLocation5) {

        this.favouriteLocation1 = favouriteLocation1;
        this.favouriteLocation2 = favouriteLocation2;
        this.favouriteLocation3 = favouriteLocation3;
        this.favouriteLocation4 = favouriteLocation4;
        this.favouriteLocation5 = favouriteLocation5;
    }

    public String getFavouriteLocation1() {
        return favouriteLocation1;
    }

    public String getFavouriteLocation2() {
        return favouriteLocation2;
    }

    public String getFavouriteLocation3() {
        return favouriteLocation3;
    }

    public String getFavouriteLocation4() {
        return favouriteLocation4;
    }

    public String getFavouriteLocation5() {
        return favouriteLocation5;
    }
}