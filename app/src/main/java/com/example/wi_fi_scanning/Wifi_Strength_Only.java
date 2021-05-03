package com.example.wi_fi_scanning;

public class Wifi_Strength_Only {
    private String SSID;
    private String BSSID;
    private Float RSSI;
    private String Location_ID;
    private long time_stamp;
    private String device_ID;

    public Wifi_Strength_Only(){

    }
    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public Float getRSSI() {
        return RSSI;
    }

    public void setRSSI(Float RSSI) {
        this.RSSI = RSSI;
    }

    public String getLocation_ID() {
        return Location_ID;
    }

    public void setLocation_ID(String location_ID) {
        Location_ID = location_ID;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getDevice_ID() {
        return device_ID;
    }

    public void setDevice_ID(String device_ID) {
        this.device_ID = device_ID;
    }
}
