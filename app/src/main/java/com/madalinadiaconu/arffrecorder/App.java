package com.madalinadiaconu.arffrecorder;

import android.app.Application;
import android.content.Context;

import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.ClassLabel;
import com.madalinadiaconu.arffrecorder.pcse_dd_14.actclient.CoordinatorClient;

/**
 * Created by Diaconu Madalina on 15.10.2016.
 * Class extending the default Application class
 */
public class App extends Application {

    private static Context context;

    public App() {
        context = this;
    }

    public static Context getAppContext() {
        return context;
    }

}
