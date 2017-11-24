package com.example.liyang.androidtouristapp;

import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Li Yang on 24/11/2017.
 */

public class Pojo {
    private final String id;
    private final String name;
    private final String location;
    private final String route;
    private final String address;
    private final String imageName;
    private boolean isSelected;

    public Pojo(String id,String name, String location,String route,String address,
                String imageName) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.route = route;
        this.address = address;
        this.imageName = imageName;
    }

    public boolean isSelected(){
        return isSelected;
    }
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getId(){
        return id;
    }
    public String getName() {
        return name;
    }
    public String getLocation() {
        return location;
    }
    public String getRoute(){
        return route;
    }
    public String getAddress(){
        return address;
    }
    public String getImageName() {
        return imageName;
    }
}

