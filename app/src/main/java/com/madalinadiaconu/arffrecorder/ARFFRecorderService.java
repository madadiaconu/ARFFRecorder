package com.madalinadiaconu.arffrecorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.os.Process;

import de.greenrobot.event.EventBus;


/**
 * Created by Diaconu Madalina on 12.10.2016.
 */
public class ARFFRecorderService extends Service  implements SensorEventListener {

    private SensorManager sensorManager;
    private static boolean isOn = false;

    public ARFFRecorderService() {
    }

    @Override
    public void onCreate() {
        HandlerThread handlerthread = new HandlerThread("MyThread", Process.THREAD_PRIORITY_BACKGROUND);
        handlerthread.start();

        sensorManager  = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("zzz", "MyService Started.");
        //If service is killed while starting, it restarts.
        isOn = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isOn = false;
        sensorManager.unregisterListener(this);

        Log.d("zzz", "MyService is Completed or stopped.");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            EventBus.getDefault().post(
                    new AccelerometerInfo(
                            event.values[0],
                            event.values[1],
                            event.values[2]));

            //TODO Write values in a file
        }
    }

    public static boolean isOn() {
        return isOn;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
