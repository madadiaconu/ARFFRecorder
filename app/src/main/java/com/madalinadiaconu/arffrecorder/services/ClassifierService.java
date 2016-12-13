package com.madalinadiaconu.arffrecorder.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.madalinadiaconu.arffrecorder.util.FeatureExtractor;
import com.madalinadiaconu.arffrecorder.util.NoDataAvailableException;
import com.madalinadiaconu.arffrecorder.util.WekaClassifier;
import com.madalinadiaconu.arffrecorder.model.AccelerometerInfo;
import com.madalinadiaconu.arffrecorder.model.ActivityType;
import com.madalinadiaconu.arffrecorder.model.FeatureVector;
import com.madalinadiaconu.arffrecorder.model.SlidingWindow;

import java.util.LinkedList;

import de.greenrobot.event.EventBus;

/**
 * Created by Diaconu Madalina on 11.12.16.
 * Service used to classify current data
 */
public class ClassifierService extends IntentService implements SensorEventListener {

    private SensorManager sensorManager;
    private static boolean isOn = false;
    private LinkedList<SlidingWindow> slidingWindows;
    private int jumpSize;
    private int slidingWndowSize;

    public ClassifierService() {
        super(null);
    }

    public ClassifierService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        sensorManager  = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        slidingWindows = new LinkedList<>();
        jumpSize = 500;
        slidingWndowSize = 1000;
        slidingWindows.add(new SlidingWindow(slidingWndowSize));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isOn = true;
        return START_STICKY;         //If service is killed while starting, it restarts.
    }

    @Override
    public void onDestroy() {
        isOn = false;
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        AccelerometerInfo accelerometerInfo = new AccelerometerInfo(event.values[0],
                event.values[1],
                event.values[2],
                System.currentTimeMillis());
        try {
            handleOverlappingSlidingWindows();
            handleCompleteSegment();
        } catch (NoDataAvailableException ex) {
            ex.printStackTrace();
        }
        addInfoToSlidingWindows(accelerometerInfo);
    }

    private void handleOverlappingSlidingWindows() throws NoDataAvailableException {
        if (slidingWindows.getLast().passedSize(jumpSize)) {
            slidingWindows.add(new SlidingWindow(slidingWndowSize));
        }
    }

    private void handleCompleteSegment() throws NoDataAvailableException {
        if (slidingWindows.getFirst().isFull()) {
            FeatureVector featureVector = FeatureExtractor.getInstance().extractFeatures(slidingWindows.getFirst());
            ActivityType activityType = WekaClassifier.getInstance().classify(featureVector);
            EventBus.getDefault().post(activityType);
            slidingWindows.removeFirst();
        }
    }

    private void addInfoToSlidingWindows(AccelerometerInfo accelerometerInfo) {
        for (SlidingWindow slidingWindow: slidingWindows) {
            slidingWindow.addAcceletometerInfo(accelerometerInfo);
        }
    }

    public static boolean isOn() {
        return isOn;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
