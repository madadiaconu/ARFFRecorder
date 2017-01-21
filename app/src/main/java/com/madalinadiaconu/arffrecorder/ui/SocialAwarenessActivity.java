package com.madalinadiaconu.arffrecorder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.madalinadiaconu.arffrecorder.R;
import com.madalinadiaconu.arffrecorder.model.UserRoleInfo;
import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.RoomState;

import de.greenrobot.event.EventBus;

/**
 * Created by Diaconu Madalina on 21.01.17.
 * Activity meant to show the state of the room together with the number of listeners, speakers and users in transition
 */

public class SocialAwarenessActivity extends AppCompatActivity {

    private TextView listeners, speakers, transitionUsers;
    private ImageView roomState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socialawareness);

        listeners = (TextView) findViewById(R.id.nr_listeners);
        speakers = (TextView) findViewById(R.id.nr_speakers);
        transitionUsers = (TextView) findViewById(R.id.nr_transitionals);

        roomState = (ImageView) findViewById(R.id.room_status);

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SocialAwarenessActivity.this, ARFFRecorderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
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

    public void onEvent(RoomState state) {
        switch (state) {
            case empty:
                roomState.setImageResource(R.drawable.gempty);
                break;
            case lecture:
                roomState.setImageResource(R.drawable.glecture);
                break;
            case transition:
                roomState.setImageResource(R.drawable.gtransitional);
                break;
        }
    }

    public void onEvent(UserRoleInfo userRoleInfo) {
        listeners.setText(String.valueOf(userRoleInfo.getListeners()));
        speakers.setText(String.valueOf(userRoleInfo.getSpeakers()));
        transitionUsers.setText(String.valueOf(userRoleInfo.getTransitionUsers()));
    }
}
