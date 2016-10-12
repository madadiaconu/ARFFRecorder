package com.madalinadiaconu.arffrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class ARFFRecorderActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arffrecorder);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.fab);
        final Intent serviceIntent = new Intent(ARFFRecorderActivity.this, ARFFRecorderService.class);

        if (toggleButton != null) {
//
//            if (startService(serviceIntent) != null) {
//                toggleButton.setChecked(true);
//            } else {
//                stopService(serviceIntent);
//            }

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
}
