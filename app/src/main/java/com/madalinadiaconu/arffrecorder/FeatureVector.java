package com.madalinadiaconu.arffrecorder;

/**
 * Created by Diaconu Madalina on 11.12.16.
 * Model class representing the chosen feature vector
 */

public class FeatureVector {

    private double zMean;
    private double xMean;
    private double absVar;

    public FeatureVector(double zMean, double xMean, double absVar) {
        this.zMean = zMean;
        this.xMean = xMean;
        this.absVar = absVar;
    }

    public double getzMean() {
        return zMean;
    }

    public double getxMean() {
        return xMean;
    }

    public double getAbsVar() {
        return absVar;
    }
}
