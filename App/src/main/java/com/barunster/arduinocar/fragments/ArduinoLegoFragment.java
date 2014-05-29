package com.barunster.arduinocar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.barunster.arduinocar.interfaces.FragmentMode;
import com.barunster.arduinocar.interfaces.SlideMenuListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by itzik on 3/9/14.
 */
public class ArduinoLegoFragment extends Fragment implements FragmentMode, SlideMenuListener{

    private static final String TAG = ArduinoLegoFragment.class.getSimpleName();
    private static final boolean DEBUG = true;
    public static final int DEFAULT_SPEED_POINTS = 255;
    public static final String CONTROLLER_ID = "controller_id";

    /* Controller Types*/
    public static final String FRAGMENT_TYPE_CUSTOM_CONTROLLER = "lego_fragment.type.custom_controller";
    public static final String FRAGMENT_TYPE_MULTIPLE = "lego_fragment.type.multiple";

/*    public static final String FRAGMENT_TYPE_STICK = "lego_fragment.type.stick";
    public static final String FRAGMENT_TYPE_MOTOR = "lego_fragment.type.motor";
    public static final String FRAGMENT_TYPE_SERVO = "lego_fragment.type.servo";
    public static final String FRAGMENT_TYPE_MULTIPLE = "lego_fragment.type.multiple";
    public static final String FRAGMENT_TYPE_ACCELEROMETER = "lego_fragment.type.accelerometer";

    public static final List<String> controllersTags =
            new ArrayList<String>( Arrays.asList( new String[]{FRAGMENT_TYPE_SERVO, FRAGMENT_TYPE_MOTOR,
                                                           FRAGMENT_TYPE_STICK, FRAGMENT_TYPE_MULTIPLE,
                                                           FRAGMENT_TYPE_ACCELEROMETER, FRAGMENT_TYPE_CUSTOM_CONTROLLER}  ) );*/

    /* Keys*/
    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final String SCREEN_WIDTH = "screen_width";
    public static final String SCREEN_HEIGHT = "screen_height";

    private String type;
    private float screenWidth, screenHeight;
    long controllerId = -1;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        if (args.containsKey(FRAGMENT_TYPE))
            type = args.getString(FRAGMENT_TYPE);
        else if (DEBUG)
            Log.e(TAG, "No type");

        if (args.containsKey(SCREEN_HEIGHT))
            screenHeight = args.getFloat(SCREEN_HEIGHT);
        else if (DEBUG)
            Log.e(TAG, "No height");

        if (args.containsKey(SCREEN_WIDTH))
            screenWidth = args.getFloat(SCREEN_WIDTH);
        else if (DEBUG)
            Log.e(TAG, "No width");

        if (args.containsKey(CONTROLLER_ID))
            controllerId = args.getLong(CONTROLLER_ID);
        else if (DEBUG)
            Log.e(TAG, "No Controller ID");
    }

    public String getType() {
        return type;
    }

    public float getScreenHeight() {
        return screenHeight;
    }

    public float getScreenWidth() {
        return screenWidth;
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onSlideMenuOpen() {

    }

    @Override
    public void onSlideMenuClosed() {

    }

    @Override
    public void onSettingsChanged() {

    }

    @Override
    public void onControllerSelected(long id) {

    }

    @Override
    public void onFullScreen() {

    }

    @Override
    public void onExitFullScreen() {

    }

    @Override
    public void openAddButtonOption() {

    }

    @Override
    public void onEditModeChanged(boolean editing) {

    }
}
