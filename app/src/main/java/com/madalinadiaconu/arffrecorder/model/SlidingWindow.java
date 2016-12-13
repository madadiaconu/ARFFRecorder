package com.madalinadiaconu.arffrecorder.model;

import com.madalinadiaconu.arffrecorder.util.NoDataAvailableException;

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
        try {
            if (!isFull()) {
                this.data.add(accelerometerInfo);
            }
        } catch (NoDataAvailableException ex) {
            this.data.add(accelerometerInfo);
        }
    }

    public boolean passedSize (int s) throws NoDataAvailableException {
        if (data.size() > 0)
            return (data.getLast().getTimestamp() - data.getFirst().getTimestamp()) >= s;
        throw new NoDataAvailableException();
    }

    public boolean isFull() throws NoDataAvailableException {
        return passedSize(size);
    }

    public long getLastTimestamp() throws NoDataAvailableException {
        if (data.size() > 0)
            return data.getLast().getTimestamp();
        throw new NoDataAvailableException();
    }

    public LinkedList<AccelerometerInfo> getData() {
        return data;
    }
}
