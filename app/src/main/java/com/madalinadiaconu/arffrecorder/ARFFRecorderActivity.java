package com.madalinadiaconu.arffrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import de.greenrobot.event.EventBus;

/**
 * Created by Diaconu Madalina on 12.10.2016.
 */
public class ARFFRecorderActivity extends AppCompatActivity{

    private TextView xCoordinate, yCoordinate, zCoordinate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arffrecorder);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        xCoordinate = (TextView) findViewById(R.id.x_coordinate);
        yCoordinate = (TextView) findViewById(R.id.y_coordinate);
        zCoordinate = (TextView) findViewById(R.id.z_coordinate);

        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.fab);
        final Intent serviceIntent = new Intent(ARFFRecorderActivity.this, ARFFRecorderService.class);

        if (toggleButton != null) {
            toggleButton.setChecked(ARFFRecorderService.isOn());
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        startService(serviceIntent);
                    } else {
                        stopService(serviceIntent);
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(AccelerometerInfo event){
        xCoordinate.setText(String.valueOf(event.getX()));
        yCoordinate.setText(String.valueOf(event.getY()));
        zCoordinate.setText(String.valueOf(event.getZ()));
    }
}
