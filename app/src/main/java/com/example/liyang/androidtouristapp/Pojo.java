package com.example.liyang.androidtouristapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by Kenny on 12/2/2017.
 */
class Pojo implements Parcelable {
    private final String id;
    private final String name;
    private final String longitude;
    private final String latitude;
    //private final String route;
    private final String address;
    private final String adultfee;
    private final String childfee;
    private final String imageName;
    private final String snippet;
    private boolean isSelected;
    HashMap<String,Integer> bus;
    HashMap<String,Integer> walk;
    HashMap<String,Integer> taxi;
    HashMap<String,Integer> busPrice;
    HashMap<String,Integer> taxiPrice;
    public Pojo(String id,String name,String latitude, String longitude,String address,String adultfee,String childfee,
                String imageName,String snippet,HashMap<String,Integer> walkEdgeList,HashMap<String,Integer> busEdgeList,HashMap<String,Integer> taxiEdgeList,HashMap<String,Integer> busPrice,HashMap<String,Integer>taxiPrice) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
        this.adultfee = adultfee;
        this.childfee = childfee;
        this.imageName = imageName;
        this.snippet = snippet;
        this.walk=walkEdgeList;
        this.bus=busEdgeList;
        this.taxi=taxiEdgeList;
        this.busPrice=busPrice;
        this.taxiPrice=taxiPrice;

    }

    protected Pojo(Parcel in) {
        id = in.readString();
        name = in.readString();
        longitude = in.readString();
        latitude = in.readString();
        address = in.readString();
        adultfee = in.readString();
        childfee = in.readString();
        imageName = in.readString();
        snippet = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeString(address);
        dest.writeString(adultfee);
        dest.writeString(childfee);
        dest.writeString(imageName);
        dest.writeString(snippet);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pojo> CREATOR = new Creator<Pojo>() {
        @Override
        public Pojo createFromParcel(Parcel in) {
            return new Pojo(in);
        }

        @Override
        public Pojo[] newArray(int size) {
            return new Pojo[size];
        }
    };

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
    public String getLongitude() {
        return longitude;
    }
    public String getLatitude() { return latitude;}
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
    public String getSnippet() {return snippet;}
    public HashMap<String, Integer> getBus() {
        return bus;
    }

    public HashMap<String, Integer> getWalk() {
        return walk;
    }

    public HashMap<String, Integer> getTaxi() {
        return taxi;
    }

    public HashMap<String, Integer> getBusPrice() {
        return busPrice;
    }

    public HashMap<String, Integer> getTaxiPrice() {
        return taxiPrice;
    }


}
