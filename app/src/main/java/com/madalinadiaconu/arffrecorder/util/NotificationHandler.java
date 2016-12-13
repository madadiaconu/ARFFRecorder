package com.madalinadiaconu.arffrecorder.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.madalinadiaconu.arffrecorder.App;
import com.madalinadiaconu.arffrecorder.R;
import com.madalinadiaconu.arffrecorder.model.AccelerometerInfo;
import com.madalinadiaconu.arffrecorder.ui.ARFFRecorderActivity;

import de.greenrobot.event.EventBus;

/**
 * Created by Diaconu Madalina on 15.10.2016.
 * Class which manages the display of notifications
 */
public class NotificationHandler {

    private static final int NOTIFICATION_ID = 58;

    private static NotificationHandler instance;
    private static boolean isRegistered = false;
    private static NotificationManager notificationManager =
            (NotificationManager) App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

    private NotificationHandler() {
    }

    public static NotificationHandler getInstance() {
        if (instance == null) {
            instance = new NotificationHandler();
        }
        return instance;
    }

    private void showNotification(AccelerometerInfo accelerometerInfo) {
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(App.getAppContext())
                        .setSmallIcon(R.drawable.wind_rose)
                        .setContentTitle(App.getAppContext().getString(R.string.notification_title))
                        .setContentText(accelerometerInfo.toString());

        Intent intent = new Intent(App.getAppContext(), ARFFRecorderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(App.getAppContext(), 0, intent, 0);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void registerToAccelerometerChanges() {
        if (!isRegistered) {
            EventBus.getDefault().register(getInstance());
            isRegistered = true;
        }
    }

    public static void unregisterToAccelerometerChanges() {
        if (isRegistered) {
            EventBus.getDefault().unregister(getInstance());
            isRegistered = false;
        }
    }

    public static void removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void onEvent(AccelerometerInfo event){
        showNotification(event);
    }
}
