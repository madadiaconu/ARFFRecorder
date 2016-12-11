package com.madalinadiaconu.arffrecorder;

import java.util.LinkedList;

/**
 * Created by Diaconu Madalina on 11.12.16.
 * Class modelling the data in a sliding window
 */

public class SlidingWindow {

    private int size;
    private LinkedList<AccelerometerInfo> data;

    public SlidingWindow(int size) {
        this.data = new LinkedList<>();
        this.size = size;
    }

    public void addAcceletometerInfo(AccelerometerInfo accelerometerInfo) {
        if (!isFull()) {
            this.data.add(accelerometerInfo);
        }
    }

    public boolean isFull() {
        return ((data.getLast().getTimestamp() - data.getFirst().getTimestamp()) >= size);
    }

    public long getLastTimestamp() {
        return data.getLast().getTimestamp();
    }

    public LinkedList<AccelerometerInfo> getData() {
        return data;
    }
}
