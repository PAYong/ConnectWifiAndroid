package com.example.android.connectwifi;

public class Buildings {

    String name, address, user_id, building_id;

    public Buildings(){
    }

    public Buildings(String name, String address, String user_id, String building_id){
        this.name = name;
        this.address = address;
        this.user_id = user_id;
        this.building_id = building_id;
    }

    public String getBuildingName(){ return name;
    }

    public String getBuildingAddress(){
        return address;
    }

    public String getBuildingUser_id(){
        return user_id;
    }

    public String getBuilding_id(){
        return building_id;
    }

    public void setBuildingName(String name){ this.name = name;}

    public void setBuildingAddress(String address){
        this.address = address;
    }

    public void setBuildingUser_id(String user_id){
        this.user_id = user_id;
    }

    public void setBuilding_id(String building_id){
        this.building_id = building_id;
    }

}
