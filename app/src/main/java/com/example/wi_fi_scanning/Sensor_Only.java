package com.example.wi_fi_scanning;

import java.util.HashMap;

public class Sensor_Only {
    private String Location_ID;
    private long time_stamp;
    private String device_ID;
    private HashMap Acceleration;
    private HashMap Linear_Acceleration;
    private HashMap Orientation;
    private HashMap Gravity;
    private HashMap Magnetic;
    private HashMap Gyroscope;

    /*private Float x_acce, y_acce, z_acce;
    private Float x_lin_acc, y_lin_acc, z_lin_acc;
    private Float x_ori, y_ori, z_ori;
    private Float x_grav, y_grav, z_grav;
    private Float x_magnet, y_magnet, z_magnet;
    private Float x_gyro, y_gyro, z_gyro;*/

    public Sensor_Only(){

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

    public HashMap getAcceleration() {
        return Acceleration;
    }

    public void setAcceleration(HashMap acceleration) {
        Acceleration = acceleration;
    }

    public HashMap getLinear_Acceleration() {
        return Linear_Acceleration;
    }

    public void setLinear_Acceleration(HashMap linear_Acceleration) {
        Linear_Acceleration = linear_Acceleration;
    }

    public HashMap getOrientation() {
        return Orientation;
    }

    public void setOrientation(HashMap orientation) {
        Orientation = orientation;
    }

    public HashMap getGravity() {
        return Gravity;
    }

    public void setGravity(HashMap gravity) {
        Gravity = gravity;
    }

    public HashMap getMagnetic() {
        return Magnetic;
    }

    public void setMagnetic(HashMap magnetic) {
        Magnetic = magnetic;
    }

    public HashMap getGyroscope() {
        return Gyroscope;
    }

    public void setGyroscope(HashMap gyroscope) {
        Gyroscope = gyroscope;
    }
}
