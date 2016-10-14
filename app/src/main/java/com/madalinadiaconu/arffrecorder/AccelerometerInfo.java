package com.madalinadiaconu.arffrecorder;

/**
 * Created by Diaconu Madalina on 13.10.2016.
 */
public class AccelerometerInfo {

    private double x;
    private double y;
    private double z;

    public AccelerometerInfo(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
}
