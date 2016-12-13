package com.madalinadiaconu.arffrecorder.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.madalinadiaconu.arffrecorder.model.AccelerometerInfo;

import de.greenrobot.event.EventBus;


/**
 * Created by Diaconu Madalina on 12.10.2016.
 * Service used to listening for accelerometer changes
 */
public class ARFFRecorderService extends IntentService implements SensorEventListener {

    private SensorManager sensorManager;
    private static boolean isOn = false;

    public ARFFRecorderService(){
        super(null);
    }

    public ARFFRecorderService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        sensorManager  = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
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
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            EventBus.getDefault().post(
                    new AccelerometerInfo(
                            event.values[0],
                            event.values[1],
                            event.values[2],
                            System.currentTimeMillis()));
        }
    }

    public static boolean isOn() {
        return isOn;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
