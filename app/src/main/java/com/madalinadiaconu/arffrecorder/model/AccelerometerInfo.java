package com.madalinadiaconu.arffrecorder.model;

/**
 * Created by Diaconu Madalina on 13.10.2016.
 * Class holding the data provided by the accelerometer
 */
public class AccelerometerInfo {

    private long timestamp;
    private double x;
    private double y;
    private double z;

    public AccelerometerInfo(double x, double y, double z, long timestamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[ x = %.2f, y = %.2f, z = %.2f ]", x, y, z);
    }
}
