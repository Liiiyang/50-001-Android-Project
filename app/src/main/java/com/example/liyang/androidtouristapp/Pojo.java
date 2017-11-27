package com.example.liyang.androidtouristapp;

import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Li Yang on 24/11/2017.
 */

public class Pojo {
    private final String id;
    private final String name;
    private final String location;
    //private final String route;
    private final String address;
    private final String adultfee;
    private final String childfee;
    private final String imageName;
    private boolean isSelected;
    HashMap<String,Integer> bus;
    HashMap<String,Integer> walk;
    HashMap<String,Integer> taxi;
    public Pojo(String id,String name, String location,String address,String adultfee,String childfee,
                String imageName,HashMap<String,Integer> walkEdgeList,HashMap<String,Integer> busEdgeList,HashMap<String,Integer> taxiEdgeList) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.address = address;
        this.adultfee = adultfee;
        this.childfee = childfee;
        this.imageName = imageName;
        this.walk=walkEdgeList;
        this.bus=busEdgeList;
        this.taxi=taxiEdgeList;
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
    public String getAddress(){
        return address;
    }
    public String getAdultfee(){
        return adultfee;
    }
    public String getChildfee(){
        return childfee;
    }
    public String getImageName() {
        return imageName;
    }

}

