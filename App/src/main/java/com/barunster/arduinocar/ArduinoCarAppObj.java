package com.barunster.arduinocar;

import android.app.Application;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.barunster.arduinocar.custom_controllers_obj.AccelerometerHandler;
import com.barunster.arduinocar.database.CustomDBManager;
import com.barunster.arduinocar.views.SlideFadeMenu;

import braunster.btconnection.BTConnection;

/**
 * Created by itzik on 12/24/13.
 */
public class ArduinoCarAppObj extends Application {

    private final static String APIKEY = "a5522b00";

    private BTConnection connection;
    private SlideFadeMenu slideFadeMenu;
    private CustomDBManager customDBManager;
    private AccelerometerHandler accHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        connection = new BTConnection(getApplicationContext() );

        customDBManager = new CustomDBManager(getApplicationContext());

        accHandler = new AccelerometerHandler(getApplicationContext());

//        BugSenseHandler.initAndStartSession(getApplicationContext(), APIKEY);

    }

    public BTConnection getConnection() {
        return connection;
    }

    public SlideFadeMenu getSlideFadeMenu() {
        return slideFadeMenu;
    }

    public void setSlideFadeMenu(SlideFadeMenu slideFadeMenu) {
        LinearLayout.LayoutParams paramsSlideFade = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsSlideFade.gravity = Gravity.LEFT;

        slideFadeMenu.setLayoutParams(paramsSlideFade);

        this.slideFadeMenu = slideFadeMenu;
    }

    public CustomDBManager getCustomDBManager() {
        return customDBManager;
    }
}
