package com.example.android.connectwifi;

public class ConfigurationFile{

    private String configSSID;
    private String configPassword;
    private String configEndpoint;

    private String configEndpoints;

    public ConfigurationFile(){
        this.configSSID = null;
        this.configPassword = null;
        this.configEndpoint = null;
        this.configEndpoints = null;
    }

    public void setConfigSSID(String configSSID){
        this.configSSID = configSSID;
    }

    public void setConfigPassword(String configPassword){
        this.configPassword = configPassword;
    }

    public void setConfigEndpoint(String configEndpoint){
        this.configEndpoint = configEndpoint;

    }
    public String getConfigSSID(){
        return configSSID;
    }

    public String getConfigPassword(){
        return configPassword;
    }

    public  String getConfigEndpoint(){
        return configEndpoint;

    }

    public String getConfigEndpoints() {
        return configEndpoints;
    }

    public void setConfigEndpoints(String configEndpoints) {
        this.configEndpoints = configEndpoints;
    }

}
