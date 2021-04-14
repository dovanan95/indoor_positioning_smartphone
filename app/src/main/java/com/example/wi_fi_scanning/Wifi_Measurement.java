package com.example.wi_fi_scanning;

public class Wifi_Measurement {
    private String SSID;
    private String BSSID;
    private Float RSSI;
    private String Location_ID;
    private long time_stamp;

    public Wifi_Measurement() {

    }

    public String getSSID() {
        return SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public Float getRSSI() {
        return RSSI;
    }

    public String getLocation_ID() {
        return Location_ID;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public void setRSSI(Float RSSI) {
        this.RSSI = RSSI;
    }

    public void setLocation_ID(String location_ID) {
        Location_ID = location_ID;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }
}
