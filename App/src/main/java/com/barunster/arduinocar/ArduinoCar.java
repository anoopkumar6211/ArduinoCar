package com.barunster.arduinocar;

import android.app.Application;

import com.bugsense.trace.BugSenseHandler;

/**
 * Created by itzik on 12/24/13.
 */
public class ArduinoCar extends Application {

    private final static String APIKEY = "7d589131";

    @Override
    public void onCreate() {
        super.onCreate();

        BugSenseHandler.initAndStartSession(getApplicationContext(), APIKEY);

    }
}
