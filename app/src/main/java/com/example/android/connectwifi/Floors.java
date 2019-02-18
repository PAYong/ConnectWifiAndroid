package com.example.android.connectwifi;

public class Floors {
    String name, config, floorBuilding_id, floor_id, building_name, building_id;

    public Floors(){
    }

    public Floors(String name, String config, String floorBuilding_id, String floor_id, String building_name, String building_id) {
        this.name = name;
        this.config = config;
        this.floorBuilding_id = floorBuilding_id;
        this.floor_id = floor_id;
        this.building_name = building_name;
        this.building_id = building_id;
    }

    public String getFloorName() {
        return name;
    }

    public void setFloorName(String floorName) {
        this.name = floorName;
    }

    public String getFloorConfig() {
        return config;
    }

    public void setFloorConfig(String floorConfig) {
        this.config = floorConfig;
    }

    public String getFloorBuilding_id() {
        return floorBuilding_id;
    }

    public void setFloorBuilding_id(String floorBuilding_id) {
        this.floorBuilding_id = floorBuilding_id;
    }

    public String getFloor_id(){
        return floor_id;
    }

    public void setFloor_id(String floor_id){
        this.floor_id = floor_id;
    }

    public String getBuilding_name(){
        return  building_name;
    }

    public void setBuilding_name(String building_name){
        this.building_name = building_name;
    }

    public String getBuilding_id(){
        return this.building_id;
    }

    public void setBuilding_id(String building_id){
        this.building_id = building_id;
    }
}
