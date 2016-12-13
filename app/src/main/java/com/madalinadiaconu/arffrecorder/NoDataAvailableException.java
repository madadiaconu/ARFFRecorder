package com.madalinadiaconu.arffrecorder;

/**
 * Created by Madalina Diaconu on 13.12.16.
 * Exception thrown when there is not enough data in a sliding window
 */

public class NoDataAvailableException extends Exception {
    @Override
    public String getMessage() {
        return "There is no data available.";
    }
}
