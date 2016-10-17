package com.madalinadiaconu.arffrecorder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

/**
 * Created by Diaconu Madalina on 12.10.2016.
 * Activity used to display the data from the accelerometer
 */
public class ARFFRecorderActivity extends AppCompatActivity{

    private TextView xCoordinate, yCoordinate, zCoordinate;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arffrecorder);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_title);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        xCoordinate = (TextView) findViewById(R.id.x_coordinate);
        yCoordinate = (TextView) findViewById(R.id.y_coordinate);
        zCoordinate = (TextView) findViewById(R.id.z_coordinate);

        SwitchCompat switchButton = (SwitchCompat) findViewById(R.id.fab);
        final Intent serviceIntent = new Intent(ARFFRecorderActivity.this, ARFFRecorderService.class);

        switchButton.setChecked(ARFFRecorderService.isOn());
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(serviceIntent);
                    ARFFFileWriter.getInstance().startRecording();
                } else {
                    stopService(serviceIntent);
                    ARFFFileWriter.getInstance().stopRecording();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        NotificationHandler.unregisterToAccelerometerChanges();
        NotificationHandler.removeNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        NotificationHandler.registerToAccelerometerChanges();
    }

    public void onEvent(AccelerometerInfo event){
        xCoordinate.setText(String.format("x = %.2f", event.getX()));
        yCoordinate.setText(String.format("y = %.2f", event.getY()));
        zCoordinate.setText(String.format("z = %.2f", event.getZ()));
    }
}
