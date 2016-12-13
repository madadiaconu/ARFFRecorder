package com.madalinadiaconu.arffrecorder.util;

import com.madalinadiaconu.arffrecorder.model.AccelerometerInfo;
import com.madalinadiaconu.arffrecorder.model.FeatureVector;
import com.madalinadiaconu.arffrecorder.model.SlidingWindow;

/**
 * Created by Diaconu Madalina on 11.12.16.
 * Util class used for extracting features from a sliding window
 */

public class FeatureExtractor {

    private static FeatureExtractor instance;

    private FeatureExtractor() {

    }

    public static FeatureExtractor getInstance() {
        if (instance == null) {
            instance = new FeatureExtractor();
        }
        return instance;
    }

    public FeatureVector extractFeatures(SlidingWindow slidingWindow) {
        return new FeatureVector(
                computeZMean(slidingWindow),
                computeXMean(slidingWindow),
                computeVarianceOfMagnitude(slidingWindow)
        );
    }

    private double computeZMean(SlidingWindow slidingWindow) {
        int sum = 0;
        for (AccelerometerInfo accelerometerInfo: slidingWindow.getData()) {
            sum += accelerometerInfo.getZ();
        }
        return (sum / slidingWindow.getData().size());
    }

    private double computeXMean(SlidingWindow slidingWindow) {
        int sum = 0;
        for (AccelerometerInfo accelerometerInfo: slidingWindow.getData()) {
            sum += accelerometerInfo.getX();
        }
        return (sum / slidingWindow.getData().size());
    }

    private double computeVarianceOfMagnitude(SlidingWindow slidingWindow) {
        double meanOfMagnitude = computeMeanOfMagnitude(slidingWindow);
        int sum = 0;
        for (AccelerometerInfo accelerometerInfo: slidingWindow.getData()) {
            sum += Math.pow(computeMagnitude(accelerometerInfo) - meanOfMagnitude,2);
        }
        return (sum / slidingWindow.getData().size());
    }

    private double computeMeanOfMagnitude(SlidingWindow slidingWindow) {
        int sum = 0;
        for (AccelerometerInfo accelerometerInfo: slidingWindow.getData()) {
            sum += computeMagnitude(accelerometerInfo);
        }
        return (sum / slidingWindow.getData().size());
    }

    private double computeMagnitude(AccelerometerInfo accelerometerInfo) {
        return Math.sqrt(Math.pow(accelerometerInfo.getX(),2)+Math.pow(accelerometerInfo.getY(),2)+Math.pow(accelerometerInfo.getZ(),2));
    }
}
