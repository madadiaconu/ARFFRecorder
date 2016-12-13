package com.madalinadiaconu.arffrecorder.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.madalinadiaconu.arffrecorder.App;
import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.ClassLabel;
import com.madalinadiaconu.arffrecorder.util.ARFFFileWriter;
import com.madalinadiaconu.arffrecorder.services.ARFFRecorderService;
import com.madalinadiaconu.arffrecorder.model.AccelerometerInfo;
import com.madalinadiaconu.arffrecorder.model.ActivityType;
import com.madalinadiaconu.arffrecorder.services.ClassifierService;
import com.madalinadiaconu.arffrecorder.util.NotificationHandler;
import com.madalinadiaconu.arffrecorder.R;

import de.greenrobot.event.EventBus;

/**
 * Created by Diaconu Madalina on 12.10.2016.
 * Activity used to display the data from the accelerometer
 */
public class ARFFRecorderActivity extends AppCompatActivity{

    private TextView xCoordinate, yCoordinate, zCoordinate, currentActivity;

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
        currentActivity = (TextView) findViewById(R.id.curr_activity);

        SwitchCompat switchButton = (SwitchCompat) findViewById(R.id.fab);
        final Intent recorderServiceIntent = new Intent(ARFFRecorderActivity.this, ARFFRecorderService.class);
        final Intent classifierServiceIntent = new Intent(ARFFRecorderActivity.this, ClassifierService.class);

        switchButton.setChecked(ARFFRecorderService.isOn());
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(recorderServiceIntent);
                    startService(classifierServiceIntent);
                    ARFFFileWriter.getInstance().startRecording();
                } else {
                    stopService(recorderServiceIntent);
                    stopService(classifierServiceIntent);
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

    public void onEvent(ActivityType activityType) {
        currentActivity.setText(activityType.name());
        App.getCoordinatorClient().setCurrentActivity(ClassLabel.valueOf(activityType.name().toLowerCase()));
    }
}
