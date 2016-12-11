package com.madalinadiaconu.arffrecorder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        if (slidingWindows.getFirst().isFull()) {
            classifyInstance(extractFeatures(slidingWindows.getFirst()));
            slidingWindows.removeFirst();
        }
        if ((accelerometerInfo.getTimestamp() - slidingWindows.getLast().getLastTimestamp()) >= jumpSize) {
            slidingWindows.add(new SlidingWindow(slidingWndowSize));
        }
        for (SlidingWindow slidingWindow: slidingWindows) {
            slidingWindow.addAcceletometerInfo(accelerometerInfo);
        }
    }

    private void classifyInstance(List<Float> features) {

    }

    private List<Float> extractFeatures(SlidingWindow slidingWindow) {
        return new ArrayList<>();
    }

    public static boolean isOn() {
        return isOn;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}